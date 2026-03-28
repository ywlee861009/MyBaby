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

## 4. 비기능적 요구사항 및 확장성 (Non-Functional & Scalability)
- **[NF1] 로컬 우선 저장:** 초기 버전은 기기 내 로컬 DB(SQLDelight 등)에 저장한다.
- **[NF2] 서버 연동 고려:** 
    - `LetterRepository` 인터페이스를 추상화하여, 향후 `LocalDataSource`에서 `RemoteDataSource`로 손쉽게 전환하거나 동기화 로직을 추가할 수 있도록 설계한다.
    - 각 편지는 고유 ID(UUID)와 서버 동기화 상태 플래그를 가져야 한다.
- **[NF3] 오프라인 지원:** 서버 연동 후에도 오프라인에서 작성 및 로컬 저장이 가능해야 하며, 온라인 상태가 되면 동기화한다.

## 5. 데이터 모델 (Data Model - Draft)
```kotlin
data class Letter(
    val id: String,         // UUID
    val content: String,    // 편지 내용
    val createdAt: Long,    // 생성일 (Timestamp)
    val updatedAt: Long,    // 수정일 (Timestamp)
    val syncStatus: SyncStatus // PENDING, SYNCED (서버 연동 대비)
)
```

## 7. 현재 구현 상태 (Implementation Status)
- **[완료] 데이터 모델 및 DB:** `Letter.kt` 모델과 SQLDelight(`Letter.sq`) 테이블 정의 완료.
- **[완료] 레포지토리:** `LetterRepository` 인터페이스 및 `LetterRepositoryImpl` 구현 완료. (SQLDelight 연동 및 하루 1회 작성 제한 로직 포함)
- **[완료] UI (MVI):**
    - `LetterContract.kt`: State와 Intent 정의.
    - `LetterViewModel.kt`: MVI 패턴 기반 상태 관리 및 비즈니스 로직.
    - `LetterScreen.kt`: Jetpack Compose 기반 UI. 현재 단일 화면 내에서 리스트와 작성/수정 모드를 토글하는 방식으로 구현됨 (`isWritingMode`).
- **[진행 중] 디자인 고도화:** `SCREEN_FLOWS.md`의 감성적인 편지지 배경 테마 및 상세 화면 분리 등은 고도화 단계에서 진행 예정.
