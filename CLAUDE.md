# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Language

모든 대화와 커밋 메시지는 한국어로 작성한다.

## Build Commands

All commands run from `cmp/` directory:

```bash
# Android 디버그 빌드
cd cmp && ./gradlew assembleDebug

# iOS 공유 프레임워크 컴파일 (시뮬레이터)
cd cmp && ./gradlew :composeApp:compileKotlinIosSimulatorArm64

# iOS 공유 프레임워크 컴파일 (실기기)
cd cmp && ./gradlew :composeApp:compileKotlinIosArm64
```

**코드 수정 후 반드시 `./gradlew assembleDebug`로 빌드 검증할 것.**

## Architecture

- **Compose Multiplatform (CMP)** — 단일 코드베이스로 Android/iOS 동시 개발
- **패턴:** Compose + MVI (Intent → ViewModel/Reducer → State → Composable UI)
- **패키지:** `com.mybaby.app`

### 소스 구조 (`cmp/composeApp/src/`)

| 소스셋 | 역할 |
|---------|------|
| `commonMain` | 공통 비즈니스 로직, UI, 테마, 네비게이션 |
| `androidMain` | MainActivity, Android 리소스 |
| `iosMain` | MainViewController (ComposeUIViewController 브릿지) |

플랫폼 분기가 필요하면 `expect`/`actual`로 분리한다.

### 네비게이션

`Screen` sealed class (`@Serializable`) + Navigation Compose 사용. 하단 탭 5개: 홈, 기록, 편지, 일정, 더보기.

### 테마

Material 3 기반 커스텀 테마 (`MyBabyTheme`). 코랄핑크 Primary + 라벤더 Secondary + 따뜻한 오프화이트 배경. 다크모드 지원.

## Design System

`design/DESIGN_SYSTEM.md` — 컬러, 타이포, 간격, 컴포넌트 사양 정의.
`design/app-design.pen` — Pencil MCP 도구로만 읽기/수정 가능 (Read/Grep 사용 금지).

## Module Conventions

기능별/레이어별 Gradle 모듈 분리 예정 (`:core:ui`, `:core:data`, `:feature:*`).
모듈 간 의존성 최소화, `internal` 캡슐화, SOLID 원칙 준수.

## 필수 요구사항

1. **코드 수정 후 반드시 빌드 검증:** 코드를 수정한 뒤 답변을 주기 전에 반드시 `./gradlew assembleDebug`를 실행하여 빌드가 성공하는지 확인할 것.
2. **Compose + MVI 아키텍처:** UI는 Jetpack Compose, 상태 관리는 MVI(Model-View-Intent) 패턴을 따를 것. 각 화면은 Intent(사용자 액션) → Reducer/ViewModel → State → Composable UI 흐름을 유지한다.
3. **멀티모듈 프로젝트:** 기능별/레이어별로 Gradle 모듈을 분리하여 진행할 것. (예: `:app`, `:core:ui`, `:core:data`, `:feature:*` 등)
4. **모듈화 + 캡슐화 + 객체지향 원칙:** 모듈 간 의존성을 최소화하고, 내부 구현은 `internal`로 캡슐화하며, SOLID 원칙을 준수할 것.

## Key Versions

Kotlin 2.1.0 · Compose Multiplatform 1.7.3 · AGP 8.7.3 · minSdk 26 · targetSdk 35 · Java 17
버전 관리는 `cmp/gradle/libs.versions.toml` 참조.
