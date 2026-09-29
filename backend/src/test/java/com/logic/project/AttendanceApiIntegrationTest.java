package com.logic.project;

import com.logic.project.config.JwtConfig;
import com.logic.project.domain.*;
import com.logic.project.dto.AttendanceRequest;
import com.logic.project.repository.*;
import com.logic.project.service.AttendanceService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Real transactions and security filters, isolated from application/production databases.
@SpringBootTest
class AttendanceApiIntegrationTest {
    @Autowired WebApplicationContext context;
    @Autowired JwtConfig jwt;
    @Autowired MemberRepository members;
    @Autowired AttendanceRepository rows;
    @Autowired AttendanceService service;
    @MockitoBean(name = "attendanceClock") Clock clock;
    final ZoneId zone = ZoneId.of("Asia/Seoul");
    AtomicReference<Instant> now;
    LocalDate date;
    Member employee, other, admin;
    MockMvc mvc;
    String token, adminToken;

    Member member(String suffix, Member.Role role) {
        String username = "att-" + suffix + UUID.randomUUID().toString().substring(0, 8);
        return members.saveAndFlush(Member.builder().username(username).name(username)
                .email(username + "@example.test").password("unused-test-password").role(role).build());
    }
    @BeforeEach void setup() {
        date = LocalDate.now(zone).plusDays(2);
        now = new AtomicReference<>(date.atTime(8, 0).atZone(zone).toInstant());
        when(clock.getZone()).thenReturn(zone);
        when(clock.instant()).thenAnswer(invocation -> now.get());
        employee = member("user", Member.Role.USER);
        other = member("other", Member.Role.USER);
        admin = member("admin", Member.Role.ADMIN);
        token = "Bearer " + jwt.generateToken(employee.getUsername(), "USER");
        adminToken = "Bearer " + jwt.generateToken(admin.getUsername(), "ADMIN");
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }
    @AfterEach void cleanup() {
        rows.deleteAll(rows.findByMemberIdAndWorkDateBetweenOrderByWorkDateDesc(employee.getId(), date.minusDays(1), date.plusDays(3)));
        rows.deleteAll(rows.findByMemberIdAndWorkDateBetweenOrderByWorkDateDesc(other.getId(), date.minusDays(1), date.plusDays(3)));
        members.deleteAllById(List.of(employee.getId(), other.getId(), admin.getId()));
    }
    void checkIn(String type) throws Exception {
        mvc.perform(post("/api/attendance/me/check-in").header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON).content("{\"workType\":\"" + type + "\",\"notes\":\"야간 점검\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("WORKING"));
    }

    @Test void lifecycleUsesServerTimeAndAuthenticatedIdentity() throws Exception {
        mvc.perform(get("/api/attendance/me/today").header("Authorization", token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.attendance.status").value("NOT_CHECKED_IN"));
        mvc.perform(post("/api/attendance/me/check-out").header("Authorization", token)).andExpect(status().isBadRequest());
        Instant start = now.get();
        mvc.perform(post("/api/attendance/me/check-in").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content("{\"workType\":\"DUTY\",\"memberId\":" + other.getId() + ",\"workDate\":\"2000-01-01\",\"checkInAt\":\"2000-01-01T00:00:00Z\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.memberId").value(employee.getId()))
                .andExpect(jsonPath("$.checkInAt").value(start.toString())).andExpect(jsonPath("$.workDate").value(date.toString()));
        mvc.perform(post("/api/attendance/me/check-in").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content("{\"workType\":\"NORMAL\"}")).andExpect(status().isBadRequest());
        mvc.perform(patch("/api/attendance/me/notes").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content("{\"notes\":\" 점검 완료 \"}")).andExpect(status().isOk()).andExpect(jsonPath("$.notes").value("점검 완료"));
        now.set(start.plusSeconds(8 * 3600 + 60));
        mvc.perform(post("/api/attendance/me/check-out").header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content("{\"checkOutAt\":\"2000-01-01T00:00:00Z\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.workedSeconds").value(28860))
                .andExpect(jsonPath("$.checkOutAt").value(now.get().toString())).andExpect(jsonPath("$.status").value("CHECKED_OUT"));
        mvc.perform(post("/api/attendance/me/check-out").header("Authorization", token)).andExpect(status().isBadRequest());
        mvc.perform(get("/api/attendance/me/history").header("Authorization", token).param("from", date.toString()).param("to", date.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.records.length()").value(1))
                .andExpect(jsonPath("$.records[0].notes").value("점검 완료"));
        assertTrue(rows.findByMemberIdAndWorkDate(other.getId(), date).isEmpty());
    }
    @Test void overnightShiftClosesOriginalDateAndAllowsNextShift() throws Exception {
        now.set(date.atTime(23, 0).atZone(zone).toInstant());
        checkIn("EMERGENCY");
        now.set(date.plusDays(1).atTime(3, 0).atZone(zone).toInstant());
        var today = service.today(employee.getUsername());
        assertEquals(Attendance.Status.NOT_CHECKED_IN, today.attendance().status());
        assertEquals(date, today.activeAttendance().workDate());
        assertThrows(IllegalArgumentException.class, () -> service.checkIn(employee.getUsername(), new AttendanceRequest(Attendance.WorkType.NORMAL, "")));
        var closed = service.checkOut(employee.getUsername());
        assertEquals(date, closed.workDate());
        assertEquals(14400, closed.workedSeconds());
        service.checkIn(employee.getUsername(), new AttendanceRequest(Attendance.WorkType.SUBSTITUTE, "대근"));
        assertEquals(date.plusDays(1), service.today(employee.getUsername()).activeAttendance().workDate());
    }
    @Test void adminRosterIncludesMissingRowsAndSummaryIsIndependentOfFilters() throws Exception {
        checkIn("DUTY");
        var unfiltered = service.day(date, null, null, null);
        assertTrue(unfiltered.summary().notCheckedIn() >= 1);
        assertTrue(unfiltered.employees().stream().noneMatch(r -> r.memberId().equals(admin.getId())));
        var absent = unfiltered.employees().stream().filter(r -> r.memberId().equals(other.getId())).findFirst().orElseThrow();
        assertEquals(Attendance.Status.NOT_CHECKED_IN, absent.status());
        assertNull(absent.checkInAt());
        mvc.perform(get("/api/admin/attendance").header("Authorization", adminToken).param("date", date.toString())
                .param("search", employee.getUsername()).param("status", "WORKING").param("workType", "DUTY"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.employees.length()").value(1))
                .andExpect(jsonPath("$.summary.total").value((int) unfiltered.summary().total()));
        mvc.perform(get("/api/admin/attendance/members/{id}", other.getId()).header("Authorization", adminToken)
                .param("from", date.toString()).param("to", date.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.records[0].status").value("NOT_CHECKED_IN"));
    }
    @Test void securityBlocksAnonymousAndNonAdminEvenWithForgedRoleClaim() throws Exception {
        mvc.perform(get("/api/admin/attendance")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/attendance/me/today")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/attendance").header("Authorization", token)).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/attendance/members/{id}", other.getId()).header("Authorization", token)).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/attendance").header("Authorization", "Bearer " + jwt.generateToken(employee.getUsername(), "ADMIN")))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/attendance/me/check-in").header("Authorization", adminToken).contentType(MediaType.APPLICATION_JSON)
                .content("{\"workType\":\"NORMAL\"}")).andExpect(status().isForbidden());
        employee.setEnabled(false); members.saveAndFlush(employee);
        mvc.perform(get("/api/attendance/me/today").header("Authorization", token)).andExpect(status().isBadRequest());
    }
    @Test void invalidInputAndRangesDoNotWriteRecords() throws Exception {
        for (String body : List.of("{}", "{\"workType\":\"UNKNOWN\"}", "{\"workType\":\"NORMAL\",\"notes\":\"" + "a".repeat(2001) + "\"}")) {
            mvc.perform(post("/api/attendance/me/check-in").header("Authorization", token).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("attendance.errors.invalidInput"));
        }
        mvc.perform(get("/api/attendance/me/history").header("Authorization", token).param("from", date.minusDays(366).toString()).param("to", date.toString()))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/attendance").header("Authorization", adminToken).param("date", "not-a-date")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/attendance").header("Authorization", adminToken).param("date", date.plusDays(1).toString())).andExpect(status().isBadRequest());
        assertTrue(rows.findByMemberIdAndWorkDate(employee.getId(), date).isEmpty());
    }
    @Test void concurrentCheckInsAndCheckOutsAllowOnlyOneWinner() throws Exception {
        assertEquals(1, race(() -> service.checkIn(employee.getUsername(), new AttendanceRequest(Attendance.WorkType.NORMAL, ""))));
        assertEquals(1, rows.findByMemberIdAndWorkDateBetweenOrderByWorkDateDesc(employee.getId(), date, date).size());
        now.set(now.get().plusSeconds(3600));
        assertEquals(1, race(() -> service.checkOut(employee.getUsername())));
        assertEquals(3600, service.today(employee.getUsername()).attendance().workedSeconds());
    }
    int race(Runnable action) throws Exception {
        try (var pool = Executors.newFixedThreadPool(2)) {
            var ready = new CountDownLatch(2);
            var start = new CountDownLatch(1);
            Callable<Integer> task = () -> {
                ready.countDown(); assertTrue(start.await(10, TimeUnit.SECONDS));
                try { action.run(); return 1; } catch (IllegalArgumentException expected) { return 0; }
            };
            var a = pool.submit(task); var b = pool.submit(task);
            assertTrue(ready.await(10, TimeUnit.SECONDS)); start.countDown();
            return a.get(20, TimeUnit.SECONDS) + b.get(20, TimeUnit.SECONDS);
        }
    }
}
