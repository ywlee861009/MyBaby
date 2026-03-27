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
- `cmp/`: Compose Multiplatform 기반의 메인 앱 소스 코드. (추후 `commonMain`, `androidMain`, `iosMain` 등이 위치할 곳)
- `design/`: UI/UX 디자인 에셋, 기획서, 가이드라인.
- `docs/`: 프로젝트 일반 문서 및 추가적인 상세 사양서.

## 빌드 및 실행 (Building and Running)
- **TODO:** CMP 프로젝트 초기화 후 Gradle 명령어(`./gradlew ...`)를 여기에 기재하세요.
- 안드로이드 실행: `./gradlew :cmp:composeApp:run` (예상)
- iOS 실행: Xcode 연동 및 Gradle 태스크 필요.

## 개발 가이드라인 (Development Conventions)
- **언어:** 최신 Kotlin 문법 및 코루틴(Coroutines) 사용.
- **아키텍처:** CMP 권장 패턴(MVI 또는 MVVM) 지향.
- **UI:** Compose Multiplatform 전용 컴포넌트 우선 사용. 플랫폼별(Android/iOS) 특화 코드는 `expect`/`actual`을 통해 분리.
- **문서화:** 모든 주요 기능 설계는 `docs/` 또는 루트의 `.md` 파일에 기록.

## 필수 요구사항 (Mandatory Requirements)

1. **코드 수정 후 반드시 빌드 검증:** 코드를 수정한 뒤 답변을 주기 전에 반드시 `./gradlew assembleDebug`를 실행하여 빌드가 성공하는지 확인할 것.
2. **Compose + MVI 아키텍처:** UI는 Jetpack Compose, 상태 관리는 MVI(Model-View-Intent) 패턴을 따를 것. 각 화면은 Intent(사용자 액션) → Reducer/ViewModel → State → Composable UI 흐름을 유지한다.
3. **멀티모듈 프로젝트:** 기능별/레이어별로 Gradle 모듈을 분리하여 진행할 것. (예: `:app`, `:core:ui`, `:core:data`, `:feature:*` 등)
4. **모듈화 + 캡슐화 + 객체지향 원칙:** 모듈 간 의존성을 최소화하고, 내부 구현은 `internal`로 캡슐화하며, SOLID 원칙을 준수할 것.

## Gemini CLI 지침
- 새로운 기능을 추가할 때는 `cmp/` 폴더 내의 KMP 구조를 따르세요.
- 디자인 수정 요청 시 `design/` 폴더의 가이드라인을 먼저 확인하세요.
- 프로젝트 관련 모든 대화는 한국어로 진행됩니다.
- **모든 코드 변경 후에는 반드시 빌드 성공 여부를 확인하고 그 결과를 공유하세요.**
