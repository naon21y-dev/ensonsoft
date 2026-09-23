# 장비 관리

화면: `http://localhost:5175/equipments` (로그인 필요)

기존 Axios JWT 인터셉터와 Router 인증 가드를 재사용합니다. 등록 시 상태는 `NORMAL`이며, 장비 코드는 등록 후 변경하지 않습니다. 일반 정보 수정과 상태 변경은 별도 API입니다.

## 실행

- Backend: `cd backend` 후 `./gradlew.bat bootRun` (8081)
- Frontend: `cd frontend` 후 `npm run dev -- --port 5175 --strictPort`
- 이미 실행 중인 서버가 있다면 백엔드를 재시작하여 새 Entity와 API를 반영합니다.
- 기존 `application.yaml`의 `ddl-auto: update`로 `equipments`, `equipment_status_histories` 테이블이 생성됩니다. DB 설정은 변경하지 않았습니다.

## API

모든 요청에 `Authorization: Bearer <accessToken>`을 전달합니다.

| Method | 경로 | 기능 |
| --- | --- | --- |
| POST | `/api/equipments` | 장비 등록 |
| GET | `/api/equipments` | 목록 및 복합 검색 |
| GET | `/api/equipments/{id}` | 상세 |
| GET | `/api/equipments/code/{equipmentCode}` | 코드 조회 |
| GET | `/api/equipments/site/{siteId}` | 현장별 조회 |
| GET | `/api/equipments/search/name?name=카메라` | 이름 검색 |
| GET | `/api/equipments/status/{status}` | 상태별 조회 |
| GET | `/api/equipments/type/{equipmentType}` | 유형별 조회 |
| PUT | `/api/equipments/{id}` | 정보 수정 |
| PATCH | `/api/equipments/{id}/status?status=ERROR&reason=통신장애` | 상태 변경 및 이력 저장 |
| GET | `/api/equipments/{id}/histories` | 최신 상태 이력 조회 |
| DELETE | `/api/equipments/{id}` | 장비 및 해당 상태 이력 삭제 |

목록 API의 선택적 쿼리 파라미터 `name`, `siteId`, `status`, `equipmentType`은 AND로 적용됩니다. KPI는 검색 결과와 별도로 전체 장비 기준입니다.

등록 예시 (`siteId`는 실제 현장 ID로 변경):

```json
{
  "siteId": 1,
  "equipmentCode": "EQ-001",
  "name": "서울TG 1번 CCTV",
  "equipmentType": "CCTV",
  "manufacturer": "Samsung",
  "modelName": "CCTV-1000",
  "serialNumber": "SN-001",
  "installLocation": "서울TG 1차로",
  "installedAt": "2026-09-23T10:00:00",
  "ipAddress": "192.168.0.101",
  "description": "1차로 영상 관제 CCTV"
}
```

수정 시 같은 구조에서 `equipmentCode`를 제외합니다. 선택 필드의 빈 날짜는 `null`을 사용합니다. 변경자는 요청 본문이 아닌 Spring Security의 `Authentication.getName()`으로 기록됩니다. 같은 상태 변경은 거부합니다.

장비가 연결된 현장은 삭제할 수 없습니다. 현장 삭제 전에 장비를 다른 현장으로 이동하거나 삭제해야 합니다. 장비 삭제 시 해당 장비의 상태 이력도 삭제되며, 화면에서 이를 확인받습니다.

## 검증

- `cd backend` → `./gradlew.bat build`
- `cd frontend` → `npm run build`
- `EquipmentServiceTest`: 기본 상태, 중복 코드, 없는 현장, 상태 이력, 동일 상태 거부, 사유 길이, 정보 수정, 삭제 순서.
- `EquipmentApiIntegrationTest`: 실제 PostgreSQL/JPA와 JWT 보안 필터를 사용하는 API 등록·조회·복합 검색·수정·상태 변경·이력·삭제 및 입력 검증, 비인증 접근 차단.
- 장비 API 통합 테스트는 임시 현장·사용자·장비를 만들고 각 테스트 종료 시 트랜잭션을 롤백합니다. 기존 `ProjectApplicationTests`의 애플리케이션 초기화 동작은 유지합니다.
