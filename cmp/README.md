# Compose Multiplatform (CMP) App

이 폴더는 품(Pum) 산모수첩 앱의 핵심 개발 코드를 담고 있습니다.

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

- **Navigation Compose** — 타입 안전 화면 네비게이션
- **kotlinx-serialization** — JSON 직렬화 (라우팅)
- **kotlinx-datetime** — 멀티플랫폼 날짜/시간 처리
- **SQLDelight** — 멀티플랫폼 로컬 DB
- **Material 3** — 디자인 시스템 기반

버전 상세는 `gradle/libs.versions.toml` 참조.

## 모듈 구조

```
cmp/
├── composeApp/                     # 앱 모듈 (네비게이션, 엔트리포인트)
│   └── src/
│       ├── commonMain/             # 공통 코드 (Screen, AppNavigation, App)
│       ├── androidMain/            # MainActivity, Android 리소스
│       └── iosMain/                # MainViewController (ComposeUIViewController 브릿지)
│
├── core/                           # 핵심 모듈
│   ├── ui/                         # 디자인 시스템 (테마, 컬러, 타이포, 공용 컴포넌트)
│   ├── model/                      # 데이터 모델 및 Enum (Baby, Letter, HealthRecord, Schedule 등)
│   ├── database/                   # SQLDelight DB 스키마 & 플랫폼 드라이버
│   └── data/                       # Repository 인터페이스 및 구현체
│
├── feature/                        # 기능 모듈
│   ├── home/                       # 홈 대시보드 (임신 진행률, 퀵 액션, 체크리스트)
│   ├── record/                     # 건강 기록 (체중, 혈압, 태동, 사진, 메모)
│   ├── letter/                     # 아기에게 보내는 편지 (목록/작성/상세/수정)
│   ├── schedule/                   # 진료 일정 (캘린더, 카테고리, 알림)
│   ├── setup/                      # 온보딩 (아기 정보 → 출산 예정일)
│   └── more/                       # 더보기/프로필 (아기 정보 수정)
│
└── iosApp/                         # iOS 네이티브 프로젝트 (Swift)
```

## 아키텍처

- **패턴:** Compose + MVI (Intent → ViewModel → State → Composable UI)
- **데이터:** Repository 패턴 + SQLDelight 로컬 DB
- **네비게이션:** `Screen` sealed class (`@Serializable`) + Navigation Compose
- **모듈화:** 기능별/레이어별 Gradle 멀티모듈, `internal` 캡슐화

## 빌드 방법

```bash
# Android 디버그 빌드
./gradlew assembleDebug

# iOS 공유 프레임워크 컴파일 (시뮬레이터)
./gradlew :composeApp:compileKotlinIosSimulatorArm64

# iOS 공유 프레임워크 컴파일 (실기기)
./gradlew :composeApp:compileKotlinIosArm64
```
