# Gemini CLI Project Context: 품(Pum)

이 파일은 Gemini CLI가 이 프로젝트를 이해하고 작업할 수 있도록 돕는 지침 문서입니다.

## 프로젝트 개요 (Project Overview)
- **이름:** 품(Pum) - '엄마의 품'과 '기록을 품다'라는 의미.
- **목적:** 산모와 아기를 위한 건강 기록 및 관리용 모바일 앱 (Android & iOS).
- **기술 스택:** 
    - **Language:** Kotlin
    - **UI Framework:** Compose Multiplatform (CMP)
    - **Multiplatform SDK:** Kotlin Multiplatform (KMP)
- **주요 기능:** 산모 건강 기록, 태아 성장 기록, 진료 일정 관리, 출산 준비 체크리스트 등.

## 프로젝트 구조 (Directory Structure)
- `cmp/`: Compose Multiplatform 기반의 메인 앱 소스 코드.
    - `composeApp/`: Android/iOS 공통 UI 및 플랫폼별 엔트리 포인트. (`commonMain`, `androidMain`, `iosMain`)
    - `core/`: 공통 기능 (data, database, model, ui).
    - `feature/`: 각 기능별 모듈 (home, letter).
- `design/`: UI/UX 디자인 에셋, 기획서, 가이드라인.
- `docs/`: 프로젝트 일반 문서 및 추가적인 상세 사양서.

## 빌드 및 실행 (Building and Running)
- 프로젝트 빌드: `./gradlew :cmp:composeApp:assembleDebug`
- 안드로이드 앱 실행: `./gradlew :cmp:composeApp:installDebug`
- iOS 앱 실행: Xcode에서 `cmp/iosApp/iosApp.xcworkspace`를 열어 실행하거나 Gradle 스크립트 활용.
- 테스트 실행: `./gradlew :cmp:composeApp:test`

## 개발 가이드라인 (Development Conventions)
- **언어:** 최신 Kotlin 문법 및 코루틴(Coroutines) 사용.
- **아키텍처:** MVI (Model-View-Intent) 패턴 준수. (StateFlow, Channel 활용)
- **UI:** Compose Multiplatform 전용 컴포넌트 우선 사용. `PumTheme`을 통한 디자인 시스템 적용.
- **문서화:** 모든 주요 기능 설계는 `docs/` 또는 루트의 `.md` 파일에 기록.

## 필수 요구사항 (Mandatory Requirements)

1. **코드 수정 후 반드시 빌드 검증:** 코드를 수정한 뒤 답변을 주기 전에 반드시 `./gradlew :cmp:composeApp:assembleDebug`를 실행하여 빌드가 성공하는지 확인할 것.
2. **Compose + MVI 아키텍처:** UI는 Jetpack Compose, 상태 관리는 MVI(Model-View-Intent) 패턴을 따를 것. 각 화면은 Intent(사용자 액션) → Reducer/ViewModel → State → Composable UI 흐름을 유지한다.
3. **멀티모듈 프로젝트:** `:composeApp`, `:core:*`, `:feature:*` 구조 유지.
4. **모듈화 + 캡슐화 + 객체지향 원칙:** 캡슐화 및 SOLID 원칙 준수.

## 구현 현황 (Current Status)
- **[완료] 디자인 시스템:** `core:ui` 모듈에 `PumTheme` 및 기본 컴포넌트 구현 완료.
- **[완료] 아기에게 보내는 편지:** `feature:letter` 모듈 구현 완료. 로컬 DB(SQLDelight) 연동 및 MVI 패턴 적용.
- **[MVP 완료] 홈 대시보드:** `feature:home` 모듈 구현 완료. 주차 및 디데이 요약 정보 표시.
- **[준비 중] 건강 기록:** `:feature:record` 모듈 구조 정의 및 구현 예정. (현재 `settings.gradle.kts`에만 포함됨)

## Gemini CLI 지침
- 새로운 기능을 추가할 때는 `cmp/` 폴더 내의 KMP 구조를 따르세요.
- 디자인 수정 요청 시 `design/` 폴더의 가이드라인을 먼저 확인하세요.
- 프로젝트 관련 모든 대화는 한국어로 진행됩니다.
- **모든 코드 변경 후에는 반드시 빌드 성공 여부를 확인하고 그 결과를 공유하세요.**
