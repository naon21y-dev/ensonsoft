# 유지보수 관리

로그인 후 `http://localhost:5175/maintenance` 또는 상단 **유지보수 관리** 메뉴를 사용합니다. 백엔드를 재시작하면 기존 `ddl-auto: update` 설정으로 신규 테이블 `maintenances`, `maintenance_process_histories`가 생성됩니다.

## 확인 순서

1. **장애 접수**에서 현장과 해당 현장의 장비를 선택합니다.
2. 제목, 장애 내용, 발생 시간을 입력하고 접수합니다. 장애 코드는 자동 발급되고 JWT 사용자가 접수자로 저장됩니다.
3. 목록의 **상세**를 열어 현장·장비와 장애 내용을 확인합니다.
4. **처리 시작**을 누르면 `REPORTED → IN_PROGRESS`로 변경됩니다.
5. 처리 결과를 입력하고 **처리 완료**를 누르면 `IN_PROGRESS → COMPLETED`로 변경되며 완료 시간이 저장됩니다.
6. 처리 이력에서 접수·시작·완료의 사용자, 시간, 처리 결과를 확인합니다.

KPI는 전체 장애 기준이며 목록에서는 상태별로 조회할 수 있습니다. 처리 결과는 완료 시 필수입니다. 단계 건너뛰기, 중복 시작·완료, 완료된 장애의 재처리는 허용하지 않습니다.

## API

기존 JWT `Authorization: Bearer <accessToken>`을 사용합니다.

| Method | 경로 | 기능 |
| --- | --- | --- |
| POST | `/api/maintenance` | 장애 접수 |
| GET | `/api/maintenance` | 최신 접수순 목록 |
| GET | `/api/maintenance/{id}` | 상세 |
| GET | `/api/maintenance/equipment/{equipmentId}` | 장비별 장애 |
| PATCH | `/api/maintenance/{id}/processing` | 처리 시작 |
| PATCH | `/api/maintenance/{id}/complete` | 처리 완료 |
| GET | `/api/maintenance/{id}/histories` | 최신 처리순 이력 |

접수 요청 (`equipmentId`는 실제 장비 ID):

```json
{
  "equipmentId": 1,
  "title": "CCTV 영상 수신 불가",
  "content": "영상이 표시되지 않습니다.",
  "occurredAt": "2026-09-23T10:00:00"
}
```

완료 요청:

```json
{ "result": "케이블 교체 후 정상 영상 수신 확인" }
```

접수자와 처리자는 요청 본문으로 지정하지 않으며 `Authentication.getName()`을 사용합니다. 처리 상태와 이력은 같은 트랜잭션에서 저장하고 동시 처리 요청은 장애 행 잠금으로 순서대로 검증합니다.

현장은 연결된 장비의 현재 소속 현장으로 표시됩니다. 유지보수는 장비의 운영 상태를 자동 변경하지 않습니다. 장애 기록의 참조를 보존하기 위해 연결된 장비는 DB 외래 키에 의해 삭제가 제한됩니다. 기존 장비 서비스는 변경하지 않았습니다.

## 검증

- Backend: `cd backend` 후 `./gradlew.bat build`
- Frontend: `cd frontend` 후 `npm run build`
- `MaintenanceApiIntegrationTest`는 실제 PostgreSQL/JPA 및 JWT 보안 필터로 접수부터 완료까지의 흐름, 사용자 기록, 장비별 조회, 입력 오류, 잘못된 상태 전이, 비인증 접근을 검증합니다.
- 테스트에서 생성한 사용자·현장·장비·장애·이력은 트랜잭션 종료 시 롤백됩니다.
