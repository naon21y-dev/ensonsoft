# 근태관리

직원 메뉴 **내 근태** (`/attendance`), 관리자 메뉴 **근태관리** (`/admin/attendance`)를 제공한다.
기존 JWT 인증과 Axios 인스턴스를 사용하며, 고정 UI와 오류 문구는 KO/EN/JP로 전환된다.
이름·아이디·직원이 입력한 특이사항은 원문을 보존한다.

## 기록 기준

- 직원은 `Member.role=USER` 계정이다. 관리자 목록에는 비활성 USER 계정도 포함되며 비활성 계정은 근태를 기록할 수 없다.
- 서버 시간과 `Asia/Seoul` 기준 업무일을 사용한다. 필요한 경우 Render 환경변수 `ATTENDANCE_ZONE`으로 변경할 수 있다. 프론트는 API가 반환한 시간대로 표시한다.
- 직원별 하루 한 번 출근/퇴근한다. `NORMAL`, `DUTY`, `EMERGENCY`, `SUBSTITUTE`는 출근 시 선택하고 원본 enum으로 저장한다.
- `attendances` 테이블에 회원 FK, 업무일, 출근/퇴근 Instant, 근무구분, 최대 2,000자 특이사항을 저장한다. `(member_id, work_date)` 유니크 제약과 회원 행의 비관적 잠금으로 중복 요청을 막는다.
- 자정을 넘긴 근무는 원래 출근일에 귀속된다. 이전 출근의 퇴근을 먼저 기록한 뒤 새 날짜에 출근할 수 있다.
- 근무시간은 출근부터 퇴근까지의 경과시간이며 휴게시간 공제나 급여 계산은 하지 않는다.
- 출근 기록이 없는 날은 조회 시 `NOT_CHECKED_IN`으로 계산한다. 미출근 행을 DB에 생성하지 않는다. 가입일 이전은 제외하며 휴가·결근 판단을 의미하지 않는다.
- 관리자 요약은 선택일의 전체 USER 기준이며 검색/필터에 따라 바뀌지 않는다. 직원별 이력은 최대 366일이다.
- 테이블은 기존 프로젝트의 Hibernate DDL 설정을 따른다. 운영 설정이 `validate`/`none`인 환경에서는 배포 전에 별도 스키마 생성이 필요하다. 기존 `application.yaml`은 변경하지 않았다.

## API

| 메서드 | 경로 | 권한/내용 |
|---|---|---|
| GET | `/api/attendance/me/today` | USER, 오늘 기록 및 미퇴근 기록 |
| POST | `/api/attendance/me/check-in` | USER, `{workType, notes}`; 시각/직원 ID 입력 없음 |
| POST | `/api/attendance/me/check-out` | USER, 요청 본문 없음 |
| PATCH | `/api/attendance/me/notes` | USER, `{notes}`; 미퇴근 또는 오늘 기록의 특이사항 |
| GET | `/api/attendance/me/history?from=&to=` | USER, 본인 이력 |
| GET | `/api/admin/attendance?date=&search=&status=&workType=` | ADMIN, 날짜별 요약과 직원 목록 |
| GET | `/api/admin/attendance/members/{id}?from=&to=` | ADMIN, 직원별 이력 |

Spring Security가 관리자 경로를 ADMIN으로 제한한다. 프론트 라우터/메뉴 숨김과 별개로 서버에서 차단하며, 본인 기록은 인증된 username으로만 찾는다.

## 검증

프론트: `npm run test:i18n`, `npm run build`.
브라우저: Vite 실행 후 `node tests/attendance.browser.cjs`와 `node tests/i18n.browser.cjs`.
Playwright가 별도 경로에 있으면 `PLAYWRIGHT_MODULE`에 해당 패키지 경로를 지정한다. 기본 브라우저는 Edge이며 `BROWSER_CHANNEL`로 변경 가능하다. 브라우저 검증의 API 요청은 모두 fixture로 처리한다.

백엔드: `gradlew.bat build`. 테스트용 PostgreSQL을 먼저 실행해야 한다. `src/test/resources/application.properties`가 운영 설정과 테스트 DB를 분리하며 테스트 종료 시 스키마를 삭제한다. **테스트 환경변수에는 운영/개발 DB를 지정하지 않는다.**

| 테스트 환경변수 | 기본값 |
|---|---|
| `TEST_DATABASE_URL` | `jdbc:postgresql://127.0.0.1:55439/postgres` |
| `TEST_DATABASE_USERNAME` | `postgres` |
| `TEST_DATABASE_PASSWORD` | 빈 값 |

Windows에서 별도 임시 클러스터를 실행하는 예시(설치 경로는 환경에 맞게 변경):

```powershell
& 'C:\Program Files\PostgreSQL\18\bin\initdb.exe' -D "$env:TEMP\ensonsoft-attendance-tests" -U postgres -A trust --encoding=UTF8 --locale=C
& 'C:\Program Files\PostgreSQL\18\bin\pg_ctl.exe' -D "$env:TEMP\ensonsoft-attendance-tests" -l "$env:TEMP\ensonsoft-attendance-tests.log" -o '-p 55439 -h 127.0.0.1' -w start
# backend 디렉터리에서 .\gradlew.bat build 실행
& 'C:\Program Files\PostgreSQL\18\bin\pg_ctl.exe' -D "$env:TEMP\ensonsoft-attendance-tests" -m fast -w stop
```

근태 통합 테스트는 실제 PostgreSQL 트랜잭션, JWT 필터, 동시 출근/퇴근, 관리자 권한, 서버 시각, 익일 퇴근, 입력 검증을 포함한다.
