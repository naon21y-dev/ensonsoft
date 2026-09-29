package com.logic.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class AttendanceConfig {
    @Bean("attendanceClock")
    public Clock attendanceClock(@Value("${attendance.zone:Asia/Seoul}") String zone) {
        return Clock.system(ZoneId.of(zone));
    }
}
