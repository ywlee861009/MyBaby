# 기능 명세: 온보딩 (Onboarding / Setup)

최초 실행 시 1회 표시되며, 사용자의 임신 기본 정보를 수집하고 아기 프로필을 설정하는 기능입니다.

## 1. 개요 (Overview)
- **목적:** 출산 예정일을 기반으로 주차를 자동 계산하고, 앱 사용의 개인화 환경 구축.
- **주요 가치:** 개인 맞춤형 임신 관리 서비스 시작.

## 2. 사용자 시나리오 (User Scenarios)
1. 사용자는 앱을 처음 실행하고 환영 메시지를 확인한다.
2. 사용자는 아기의 태명, 성별, 태어났는지 여부를 입력한다.
3. 사용자는 출산 예정일(또는 생일)을 선택한다.
4. 선택한 날짜를 기준으로 현재 임신 주차와 일수가 자동 계산되어 표시된다.
5. 모든 정보가 입력되면 '완료' 버튼을 눌러 홈 대시보드로 진입한다.

## 3. 기능적 요구사항 (Functional Requirements)
- **[F1] 아기 정보 입력:** 태명(닉네임), 성별, 출생 여부를 입력받아야 한다.
- **[F2] 날짜 선택 (출산 예정일/생일):** DatePicker를 통해 날짜를 선택받아야 한다.
  - 임신 중인 경우: 출산 예정일 선택
  - 이미 출산한 경우: 생일 선택
- **[F3] 주차 및 일수 자동 계산:** 선택한 출산 예정일을 기반으로 현재 임신 주차(280일 기준)를 자동 계산한다. 이미 출산한 경우에는 주차를 표시하지 않는다.
- **[F4] 초기 데이터 저장:** 입력된 정보를 로컬 DB(`BabyRepository`)에 영구 저장한다.
- **[F5] 완료 후 화면 전환:** 온보딩 완료 시 다시는 온보딩이 뜨지 않도록 처리하고 홈으로 이동한다.

## 4. 화면 구성 (Screens)

**2단계 화면 흐름:** `SetupBabyInfoScreen` → `SetupPregnancyInfoScreen` → `HomeScreen`

### 1단계: 아기 정보 (`SetupBabyInfoScreen`)
- 태명 입력 TextField
- 성별 선택 (GenderChip: 남아 / 여아 / 모름)
- 현재 상태 선택 (StatusChip: 임신 중 / 이미 출산)
- 다음 버튼 (닉네임 입력 시 활성화)

### 2단계: 날짜 선택 (`SetupPregnancyInfoScreen`)
- 뒤로가기 버튼
- 날짜 선택 버튼 (DatePickerDialog)
  - 임신 중: "출산 예정일 선택"
  - 이미 출산: "아기 생일 선택"
- 선택된 날짜 표시
- 예상 임신 주차 표시 (임신 중일 때만)
- 저장 버튼 (날짜 선택 시 활성화)

## 5. 데이터 모델 (Data Model)
```kotlin
data class Baby(
    val id: String,              // "baby_1" (최대 1개 아기 고정)
    val nickname: String,        // 태명
    val gender: BabyGender,      // BOY, GIRL, UNKNOWN
    val dueDate: Long? = null,   // 출산 예정일 Timestamp (임신 중일 때 설정)
    val birthDate: Long? = null, // 출산일 Timestamp (이미 출산일 때 설정)
    val createdAt: Long          // 최초 등록 시각
)

enum class BabyGender { BOY, GIRL, UNKNOWN }
```

> **Note:** 임신 중/이미 출산 구분은 별도 `isBorn: Boolean` 필드 없이 `dueDate`와 `birthDate`의 null 여부로 판단한다.
> - `dueDate != null` → 임신 중
> - `birthDate != null` → 이미 출산

### 임신 주차 계산 로직
```kotlin
val pregnancyStartMillis = dueDate - 280L * 24 * 3600 * 1000
val elapsedMillis = now - pregnancyStartMillis
val weeks = (elapsedMillis / (7L * 24 * 3600 * 1000)).toInt().coerceIn(0, 40)
val days  = ((elapsedMillis % (7L * 24 * 3600 * 1000)) / (24 * 3600 * 1000)).toInt().coerceIn(0, 6)
```

## 6. 현재 구현 상태 (Implementation Status)
- **[완료] 데이터 모델 및 DB:** `Baby.kt` 모델과 SQLDelight(`Baby.sq`) 테이블 정의 완료. (selectFirst, insert, deleteAll 쿼리 포함)
- **[완료] 레포지토리:** `BabyRepository` 인터페이스 및 `BabyRepositoryImpl` 구현 완료. (초기 정보 저장 및 Flow 기반 조회)
- **[완료] UI (MVI):**
    - `feature:setup` 모듈 내 `SetupBabyInfoScreen` + `SetupBabyInfoViewModel` — 태명, 성별, 임신 상태 입력.
    - `feature:setup` 모듈 내 `SetupPregnancyInfoScreen` + `SetupPregnancyInfoViewModel` — 날짜 선택 및 주차 자동 계산.
    - `SetupContract.kt`: 두 화면의 State, Intent, Event 통합 정의.
- **[완료] 시작화면 분기:** `App.kt`에서 `BabyRepository.getBaby().first()` 결과로 온보딩 여부 결정. Baby가 없으면 `Screen.Setup.BabyInfo`, 있으면 `Screen.Home`으로 이동.
- **[진행 중] 디자인 고도화:** `SCREEN_FLOWS.md`에 정의된 웰컴 화면 일러스트 등 감성적인 UI 요소 추가 예정.
