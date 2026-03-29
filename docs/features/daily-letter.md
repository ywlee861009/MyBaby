# 기능 명세: 아기에게 보내는 매일 편지 (Daily Letter to Baby)

태아와 산모 사이의 정서적 교감을 돕기 위해 매일 한 번의 편지를 기록하는 기능입니다.

## 1. 개요 (Overview)
- **목적:** 태아에게 하고 싶은 말을 기록하고 나중에 다시 볼 수 있는 타임라인 제공.
- **주요 가치:** 정서적 유대감 형성, 임신 기간의 기록 자산화.

## 2. 사용자 시나리오 (User Scenarios)
1. 사용자는 '편지' 탭에서 지금까지 쓴 편지 목록을 타임라인 형태로 확인한다.
2. 사용자는 '새 편지 쓰기' 버튼을 눌러 오늘 분량의 편지를 작성한다.
3. 오늘 이미 편지를 작성했다면, 추가 작성이 불가능하며 기존 편지 수정만 가능하다.
4. 작성된 편지는 저장되어 타임라인에 즉시 반영된다.

## 3. 기능적 요구사항 (Functional Requirements)
- **[F1] 편지 작성:** 본문 텍스트를 입력하여 편지를 작성할 수 있어야 한다.
- **[F2] 1일 1회 제한:** 시스템은 사용자가 오늘 이미 편지를 작성했는지 확인해야 하며, 하루에 두 번 이상의 편지 생성을 차단해야 한다. (수정은 허용)
- **[F3] 편지 목록(타임라인):** 작성일 역순으로 편지 목록을 노출한다.
- **[F4] 편지 상세/수정:** 과거에 쓴 편지를 확인하고 내용을 수정할 수 있다.
- **[F5] 편지지 테마 색상 선택:** 편지 작성 및 수정 시 편지지 배경 색상을 5가지 중 선택할 수 있다.
- **[F6] 임신 주차 기록:** 편지 작성 시점의 임신 주차가 자동으로 기록되어 상세 화면에서 확인 가능하다.

## 4. 비기능적 요구사항 및 확장성 (Non-Functional & Scalability)
- **[NF1] 로컬 우선 저장:** 초기 버전은 기기 내 로컬 DB(SQLDelight)에 저장한다.
- **[NF2] 서버 연동 고려:**
    - `LetterRepository` 인터페이스를 추상화하여, 향후 `LocalDataSource`에서 `RemoteDataSource`로 손쉽게 전환하거나 동기화 로직을 추가할 수 있도록 설계한다.
    - 각 편지는 고유 ID와 서버 동기화 상태 플래그(`SyncStatus`)를 가진다.
- **[NF3] 오프라인 지원:** 서버 연동 후에도 오프라인에서 작성 및 로컬 저장이 가능해야 하며, 온라인 상태가 되면 동기화한다.

## 5. 데이터 모델 (Data Model)
```kotlin
data class Letter(
    val id: String,                              // "letter_{timestamp}_{random}"
    val content: String,                         // 편지 내용
    val createdAt: Long,                         // 생성일 (Timestamp)
    val updatedAt: Long,                         // 수정일 (Timestamp)
    val weekNumber: Int = 0,                     // 작성 시점 임신 주차 (자동 계산)
    val themeColor: String = "#FFF8F0",          // 편지지 배경 색상 (hex)
    val syncStatus: SyncStatus = SyncStatus.PENDING  // PENDING, SYNCED, FAILED
)

// 편지지 테마 색상 옵션 (5가지)
// "#FFF8F0" — 따뜻한 오프화이트 (기본)
// "#F0F4FF" — 연한 파란색
// "#F5FFF0" — 연한 녹색
// "#FFF5F8" — 연한 분홍색
// "#FFFFF0" — 연한 노란색
```

## 6. 화면 구성 (Screens)

4개의 독립 화면으로 분리되어 있으며, 각각 MVI 패턴으로 구현된다.

| 화면 | 파일 | ViewModel |
|------|------|-----------|
| 편지 목록 | `LetterListScreen.kt` | `LetterListViewModel.kt` |
| 편지 작성 | `LetterWriteScreen.kt` | `LetterWriteViewModel.kt` |
| 편지 상세 | `LetterDetailScreen.kt` | `LetterDetailViewModel.kt` |
| 편지 수정 | `LetterEditScreen.kt` | `LetterEditViewModel.kt` |

Contract 정의 위치: `LetterContract.kt` (4개 화면의 State/Intent/Event 통합 정의)

### 편지 목록 (`LetterListScreen`)
- TopAppBar: "아기에게 보내는 편지"
- FAB (편지 쓰기 버튼): `canWriteToday == false`일 때만 활성화
- 편지 카드 (LazyColumn): 제목, 작성일, 편지지 배경색 미리보기
- 빈 상태: "아직 편지가 없어요" 표시

### 편지 작성 (`LetterWriteScreen`)
- TopBar: 닫기 버튼 + 저장 버튼
- 편지지 영역 (선택된 themeColor 배경):
  - "To. {babyNickname}" 헤더
  - 임신 주차 배지 (weekNumber > 0일 때)
  - 본문 입력 (BasicTextField)
- 하단: 테마 색상 선택 (5가지, 가로 스크롤)
- 저장 버튼 활성화 조건: `content.isNotBlank()`

### 편지 상세 (`LetterDetailScreen`)
- TopBar: "편지" 타이틀 + 메뉴 버튼 (수정 / 삭제)
- 편지지 표시 (themeColor 배경)
- 본문, 작성일, 임신 주차 정보
- 삭제 시 확인 다이얼로그

### 편지 수정 (`LetterEditScreen`)
- 편지 작성 화면과 동일한 구조
- 기존 내용을 form에 로드 (`.first()`로 1회만 로드하여 사용자 입력 보호)

## 7. 현재 구현 상태 (Implementation Status)
- **[완료] 데이터 모델 및 DB:** `Letter.kt` 모델과 SQLDelight(`Letter.sq`) 테이블 정의 완료. `weekNumber`, `themeColor`, `syncStatus` 필드 포함.
- **[완료] 레포지토리:** `LetterRepository` 인터페이스 및 `LetterRepositoryImpl` 구현 완료. (SQLDelight Flow 연동 및 하루 1회 작성 제한 로직 포함)
- **[완료] UI (MVI):**
    - `LetterContract.kt`: 4개 화면의 State, Intent, Event 정의.
    - `LetterListViewModel.kt`, `LetterWriteViewModel.kt`, `LetterDetailViewModel.kt`, `LetterEditViewModel.kt`: MVI 패턴 구현.
    - 4개 화면 (`LetterListScreen`, `LetterWriteScreen`, `LetterDetailScreen`, `LetterEditScreen`) 분리 구현.
- **[완료] 디자인:** 감성적인 편지지 배경 테마 5가지 및 화면 분리 구현 완료.
- **[진행 중] 서버 동기화:** `syncStatus` 필드는 정의되어 있으나, 실제 서버 동기화 로직은 미구현 (모든 편지는 `PENDING` 상태).
