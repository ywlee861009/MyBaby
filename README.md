# 품(Pum)

엄마의 품처럼 따뜻하고 안전하게 아이와의 기록을 간직하는 산모수첩 앱입니다.
Compose Multiplatform을 사용하여 Android 및 iOS에서 동일한 감성을 전달합니다.

## 프로젝트 개요

- **의미:** 아이를 품고 있는 10개월의 시간과, 태어날 아이를 안아줄 엄마의 '품'을 상징합니다.
- **플랫폼:** Android & iOS (Compose Multiplatform)
- **주요 가치:** 따뜻함, 안전함(로컬 우선 저장), 기록의 가치

## 기술 스택

| 항목 | 버전 |
|------|------|
| Kotlin | 2.1.0 |
| Compose Multiplatform | 1.7.3 |
| Android Gradle Plugin | 8.7.3 |
| Min SDK (Android) | 26 |
| Target SDK (Android) | 35 |
| JVM Target | 17 |

### 주요 라이브러리

| 라이브러리 | 용도 |
|------------|------|
| Navigation Compose | 타입 안전 화면 네비게이션 |
| kotlinx-serialization | JSON 직렬화 (라우팅) |
| kotlinx-datetime | 멀티플랫폼 날짜/시간 처리 |
| SQLDelight | 멀티플랫폼 로컬 DB |
| Material 3 | 디자인 시스템 기반 |

## 프로젝트 구조

```
MyBaby/
├── cmp/                            # Compose Multiplatform 메인 코드
│   ├── composeApp/                 # 앱 모듈 (네비게이션, 엔트리포인트)
│   │   └── src/
│   │       ├── commonMain/         # 공통 코드
│   │       ├── androidMain/        # Android 플랫폼 코드
│   │       └── iosMain/            # iOS 플랫폼 코드
│   │
│   ├── core/                       # 핵심 모듈
│   │   ├── ui/                     # 디자인 시스템 & 공용 컴포넌트
│   │   ├── model/                  # 데이터 모델 및 Enum
│   │   ├── database/               # SQLDelight DB 스키마 & 드라이버
│   │   └── data/                   # Repository 구현체
│   │
│   ├── feature/                    # 기능 모듈
│   │   ├── home/                   # 홈 대시보드
│   │   ├── record/                 # 건강 기록
│   │   ├── letter/                 # 아기에게 보내는 편지
│   │   ├── schedule/               # 진료 일정
│   │   ├── setup/                  # 온보딩
│   │   └── more/                   # 더보기/프로필
│   │
│   └── iosApp/                     # iOS 네이티브 프로젝트 (Swift)
│
├── design/                         # 디자인 문서 및 리소스
│   ├── DESIGN_SYSTEM.md            # 컬러, 타이포, 컴포넌트 명세
│   ├── SCREEN_FLOWS.md             # 화면 플로우 및 네비게이션
│   └── DATA_MODELS.md              # 데이터 모델 및 스키마
│
└── docs/                           # 기능 명세 문서
    └── features/
        ├── home-dashboard.md
        ├── daily-letter.md
        ├── onboarding-setup.md
        └── more-profile.md
```

## 아키텍처

- **패턴:** Compose + MVI (Intent → ViewModel/Reducer → State → Composable UI)
- **데이터:** Repository 패턴 + SQLDelight 로컬 DB
- **네비게이션:** `Screen` sealed class (`@Serializable`) + Navigation Compose
- **모듈화:** 기능별/레이어별 Gradle 멀티모듈 (`:core:*`, `:feature:*`)

### 하단 네비게이션 (5탭)

| 탭 | 기능 | 화면 |
|----|------|------|
| 홈 | 임신 진행률, 퀵 액션, 체크리스트 | `Screen.Home` |
| 기록 | 체중, 혈압, 태동, 사진, 메모 | `Screen.HealthRecord` |
| 편지 | 아기에게 보내는 일일 편지 | `Screen.Letter.List` |
| 일정 | 진료 일정 및 알림 관리 | `Screen.Schedule` |
| 더보기 | 아기 정보 수정, 프로필 | `Screen.More` |

## 주요 기능 및 구현 현황

### 구현 완료

- **[홈 대시보드](./docs/features/home-dashboard.md):** 임신 주차 진행률, 아기 크기 비유, 퀵 액션 4종, 주간 체크리스트, 다가오는 일정, 최근 기록 (MVI 구현, 더미 데이터)
- **[온보딩 (Setup)](./docs/features/onboarding-setup.md):** 태명/성별/출산여부 입력 → 출산 예정일 선택 → 주차 자동 계산 (2단계 플로우, DB 연동)
- **[아기에게 보내는 편지](./docs/features/daily-letter.md):** 편지 목록/작성/상세/수정 4개 화면, 5가지 편지지 테마, 1일 1회 작성 제한, 임신 주차 자동 기록 (MVI + DB 연동)
- **[건강 기록 (Health Record)](./design/SCREEN_FLOWS.md):** 체중/혈압/태동/사진/메모 5개 카테고리, 카테고리별 필터, 정상/주의/위험 상태 표시, 기록 추가/수정/삭제 (MVI + DB 연동)
- **[진료 일정 (Schedule)](./design/SCREEN_FLOWS.md):** 캘린더 기반 월간 뷰, 5개 카테고리(정기검진/초음파/혈액검사/예방접종/기타), 알림 설정(당일~1주 전), 완료 토글 (MVI + DB 연동)
- **[더보기/프로필](./docs/features/more-profile.md):** 아기 정보 수정, 프로필 관리

### 구현 예정

- 서버 동기화 (클라우드 백업)
- 태아 성장 차트
- 초음파 사진 갤러리
- 출산 준비 체크리스트 고도화
- 푸시 알림 시스템

## 디자인 시스템

Material 3 기반 커스텀 테마 (`MyBabyTheme`). 다크모드 지원.

| 요소 | 설명 |
|------|------|
| Primary | 코랄 핑크 (#FF8FAB) |
| Secondary | 라벤더 (#B57BEE) |
| 배경 | 따뜻한 오프화이트 (#FFFAF5) |
| 폰트 | Pretendard (Regular/Medium/SemiBold/Bold) |
| 간격 | 4dp 기반 시스템 |

자세한 사양은 [디자인 시스템 가이드](./design/DESIGN_SYSTEM.md)를 참조하세요.

## 빌드 방법

모든 명령은 `cmp/` 디렉토리에서 실행합니다.

```bash
# Android 디버그 빌드
cd cmp && ./gradlew assembleDebug

# iOS 공유 프레임워크 컴파일 (시뮬레이터)
cd cmp && ./gradlew :composeApp:compileKotlinIosSimulatorArm64

# iOS 공유 프레임워크 컴파일 (실기기)
cd cmp && ./gradlew :composeApp:compileKotlinIosArm64
```

## 상세 문서

- [전체 화면 플로우 및 네비게이션](./design/SCREEN_FLOWS.md)
- [디자인 시스템 가이드](./design/DESIGN_SYSTEM.md)
- [데이터 모델 및 스키마](./design/DATA_MODELS.md)
