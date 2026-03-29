# 산모수첩 디자인 시스템

## 1. 브랜드 컨셉

- **키워드:** 따뜻함, 안정감, 부드러움, 사랑
- **무드:** 아기와 엄마의 유대감을 떠올리게 하는 포근하고 밝은 분위기
- **톤:** 친근하고 다정한 말투, 부담 없는 UI

---

## 2. 컬러 팔레트

### Primary (코랄 핑크)

| 토큰 | Light Hex | Dark Hex | 용도 |
|------|-----------|----------|------|
| `primary` | `#FF8FAB` | `#FF8FAB` | 주요 버튼, 강조 요소, 탭 활성 상태 |
| `primaryVariant` | `#E0607A` | `#E0607A` | 눌림 상태 |
| `primaryLight` | `#FFF0F3` | `#3D1F27` | 선택 배경, Chip 배경 |
| `onPrimary` | `#FFFFFF` | `#FFFFFF` | Primary 위 텍스트/아이콘 |

### Secondary (라벤더)

| 토큰 | Light Hex | Dark Hex | 용도 |
|------|-----------|----------|------|
| `secondary` | `#B57BEE` | `#B57BEE` | 보조 강조, 태그, 배지 |
| `secondaryVariant` | `#9B72C8` | `#9B72C8` | 눌림 상태 |
| `secondaryLight` | `#F3EDFB` | `#2A1E3D` | 보조 배경 강조 |
| `onSecondary` | `#FFFFFF` | `#FFFFFF` | Secondary 위 텍스트/아이콘 |

### Neutral

| 토큰 | Light Hex | Dark Hex | 용도 |
|------|-----------|----------|------|
| `background` | `#FFFAF5` | `#1A1414` | 앱 전체 배경 |
| `surface` | `#FFFFFF` | `#2C2222` | 카드, 시트, 다이얼로그 배경 |
| `surfaceVariant` | `#FFF8F2` | `#3A2E2E` | 입력 필드, 구분 영역 배경 |
| `onSurface` | `#2D2020` | `#F5E6E6` | 본문 텍스트 (주요) |
| `onSurfaceSubtle` | `#9E8A8A` | `#9E8888` | 보조 텍스트, 플레이스홀더 |
| `outline` | `#E8DDD5` | `#4A3C3C` | 입력 필드 테두리, 구분선 |

### Semantic

| 토큰 | Hex | Light BG | 용도 |
|------|-----|----------|------|
| `success` | `#4CAF50` | `#E8F5E9` | 정상 범위 건강 수치 |
| `warning` | `#FF9800` | `#FFF3E0` | 주의 수치 |
| `error` | `#E53935` | `#FFEBEE` | 입력 오류, 위험 수치 |
| `info` | `#2196F3` | `#E3F2FD` | 팁, 안내 메시지 |

### 편지지 테마 색상

| 테마명 | Light Hex | Dark Hex |
|--------|-----------|----------|
| `letterDefault` | `#FFF8F0` | `#2E2318` |
| `letterBlue` | `#F0F4FF` | `#1A1F35` |
| `letterGreen` | `#F5FFF0` | `#1A2E1A` |
| `letterPink` | `#FFF5F8` | `#2E1A1F` |
| `letterYellow` | `#FFFFF0` | `#2E2E18` |

---

## 3. 타이포그래피

### 폰트
- **패밀리:** Pretendard (앱 번들 포함)
  - Weights: Regular(400), Medium(500), SemiBold(600), Bold(700)
  - Fallback: Noto Sans KR

### 텍스트 스타일

| 토큰 | 크기 | 굵기 | 줄높이 | 자간 | 용도 |
|------|------|------|--------|------|------|
| `heading1` | 24sp | Bold 700 | 1.3× | -0.5 | 화면 메인 제목 |
| `heading2` | 20sp | SemiBold 600 | 1.3× | -0.3 | 섹션 제목 |
| `heading3` | 18sp | SemiBold 600 | 1.3× | -0.2 | 카드 제목 |
| `body1` | 16sp | Regular 400 | 1.5× | 0 | 본문 텍스트 |
| `body1Medium` | 16sp | Medium 500 | 1.5× | 0 | 강조 본문 |
| `body2` | 14sp | Regular 400 | 1.5× | 0 | 보조 텍스트 |
| `body2Medium` | 14sp | Medium 500 | 1.5× | 0 | 강조 보조 |
| `caption` | 12sp | Regular 400 | 1.4× | 0.2 | 날짜, 힌트 |
| `captionBold` | 12sp | SemiBold 600 | 1.4× | 0.2 | 강조 캡션 |
| `button` | 16sp | SemiBold 600 | 1.0× | 0.1 | 버튼 텍스트 |
| `buttonSmall` | 14sp | SemiBold 600 | 1.0× | 0.1 | 소형 버튼 |

---

## 4. 아이콘

- **스타일:** Material Symbols Rounded
- **크기:** 20dp (소형), 24dp (기본), 32dp (대형), 48dp (Empty State)
- **컬러:**
  - 기본: `onSurface`
  - 활성: `primary`
  - 비활성: `onSurfaceSubtle`
  - Primary 버튼 위: `onPrimary`

---

## 5. 간격 (4dp 기반)

| 토큰 | 값 | 용도 |
|------|-----|------|
| `spacing2` | 2dp | 극소 보정 |
| `spacingXs` | 4dp | 아이콘↔텍스트 |
| `spacingSm` | 8dp | 관련 요소 간 |
| `spacingMd` | 16dp | 기본 요소 간, 카드 내부 패딩 |
| `spacingLg` | 24dp | 섹션 간 |
| `spacingXl` | 32dp | 화면 상하 여백 |
| `spacingXxl` | 48dp | 특대 여백 |

**화면 패딩:** 좌우 16dp / 카드 내부 16dp / 바텀시트 상단 24dp

---

## 6. Border Radius

| 토큰 | 값 | 용도 |
|------|-----|------|
| `radiusXs` | 4dp | 인라인 뱃지 |
| `radiusSm` | 8dp | Chip, 입력 필드 |
| `radiusMd` | 12dp | 카드 |
| `radiusLg` | 16dp | 바텀시트, 다이얼로그 |
| `radiusXl` | 24dp | 홈 강조 카드 |
| `radiusFull` | 9999dp | 아바타, FAB, Pill 버튼 |

---

## 7. 그림자 (Elevation)

| 레벨 | 값 | 용도 |
|------|-----|------|
| Level 0 | 없음 | 배경, 평면 요소 |
| Level 1 | `0 1dp 3dp rgba(0,0,0,0.06)` | 카드 |
| Level 2 | `0 2dp 8dp rgba(0,0,0,0.10)` | FAB, 드롭다운 |
| Level 3 | `0 4dp 16dp rgba(0,0,0,0.12)` | 바텀시트, 다이얼로그 |

다크 모드: 그림자 제거, 대신 surface 색상으로 레이어 구분.

---

## 8. 컴포넌트 상세 스펙

### 8-1. 버튼

#### PrimaryButton

| 속성 | 값 |
|------|-----|
| 높이 | 52dp |
| 패딩 좌우 | 24dp |
| Border Radius | `radiusFull` (Pill) |
| 텍스트 | `button` (16sp SemiBold) |

| 상태 | 배경 | 텍스트 |
|------|------|--------|
| Enabled | `#FF8FAB` | `#FFFFFF` |
| Pressed | `#E0607A` | `#FFFFFF` |
| Loading | `#FF8FAB` 40% 투명 | CircularProgress 20dp 흰색 |
| Disabled | `#E8DDD5` | `#9E8A8A` |

#### SecondaryButton (Outlined)

| 속성 | 값 |
|------|-----|
| 높이 | 52dp |
| 배경 | 투명 |
| 테두리 | 1.5dp `primary` |
| 텍스트 색상 | `primary` |
| Border Radius | `radiusFull` |

#### TextButton

| 속성 | 값 |
|------|-----|
| 텍스트 | `body2Medium`, `primary` |
| 패딩 | 4dp 8dp |
| 최소 터치 | 48dp × 48dp |

#### IconButton

| 속성 | 값 |
|------|-----|
| 크기 | 40dp × 40dp |
| 아이콘 | 24dp |
| Border Radius | `radiusFull` |

#### FAB

| 속성 | 값 |
|------|-----|
| 크기 | 56dp × 56dp |
| 배경 | `primary` |
| 아이콘 | 24dp `onPrimary` |
| Elevation | Level 2 |
| 위치 | 화면 우하단, 탭바 위 16dp |

---

### 8-2. 입력

#### TextField

| 속성 | 값 |
|------|-----|
| 높이 | 56dp |
| 배경 | `surfaceVariant` |
| 패딩 내부 좌우 | 16dp |
| Border Radius | `radiusSm` |

| 상태 | 테두리 | 라벨 색상 |
|------|--------|-----------|
| 기본 | 1dp `outline` | `onSurfaceSubtle` |
| 포커스 | 2dp `primary` | `primary` |
| 에러 | 2dp `error` | `error` |
| 비활성 | 1dp `outline` 50% | `onSurfaceSubtle` 50% |

- 에러 메시지: `caption` `error`, 아래 4dp 간격

#### TextArea (다중 행)

| 속성 | 값 |
|------|-----|
| 최소 높이 | 120dp |
| 줄 높이 | 1.8× (편지 작성 시) |
| 나머지 | TextField와 동일 |

---

### 8-3. 카드

#### LetterCard

| 속성 | 값 |
|------|-----|
| 배경 | `surface` |
| Border Radius | `radiusMd` |
| Elevation | Level 1 |
| 패딩 | 16dp |
| 구성 | 주차 Chip + 날짜(Caption) + 미리보기 2줄(Body2, maxLines=2 Ellipsis) + "편지 보기 →" TextButton |

#### RecordCard

| 속성 | 값 |
|------|-----|
| 배경 | `surface` |
| Border Radius | `radiusMd` |
| Elevation | Level 1 |
| 패딩 | 16dp |
| 구성 | 카테고리 아이콘(24dp) + 날짜(Caption) + 수치(Heading3) + 상태 Chip |

#### ScheduleCard

| 속성 | 값 |
|------|-----|
| 배경 | `surface` |
| 좌측 강조선 | 4dp `primary` |
| Border Radius | `radiusMd` |
| 패딩 | 12dp 16dp |
| 구성 | 카테고리 Chip + 제목(Body1Medium) + 날짜/시간(Caption) + 장소(Caption + 아이콘) |

---

### 8-4. 네비게이션

#### Bottom Navigation Bar (Pill 스타일)

| 속성 | 값 |
|------|-----|
| 높이 | 56dp + 시스템 네비게이션바 |
| 배경 | `surface` |
| 상단 구분선 | 1dp `outline` |
| Pill 크기 | 64dp × 32dp |
| Pill Border Radius | `radiusFull` |

| 상태 | Pill 배경 | 아이콘/텍스트 |
|------|-----------|---------------|
| 활성 | `primaryLight` | `primary` |
| 비활성 | 없음 | `onSurfaceSubtle` |

#### Top App Bar

| 속성 | 값 |
|------|-----|
| 높이 | 56dp + 상태바 |
| 배경 | `background` |
| 제목 | `heading3`, `onSurface` |
| 아이콘 | 24dp `onSurface` |

---

### 8-5. Chip / Tag

| 종류 | 배경 | 텍스트 | Radius | 패딩 | 텍스트 스타일 |
|------|------|--------|--------|------|--------------|
| Primary (주차) | `primaryLight` | `primary` | `radiusSm` | 4dp 10dp | `captionBold` |
| Secondary (카테고리) | `secondaryLight` | `secondaryVariant` | `radiusSm` | 4dp 10dp | `captionBold` |
| Success | `#E8F5E9` | `#4CAF50` | `radiusSm` | 4dp 10dp | `captionBold` |
| Warning | `#FFF3E0` | `#FF9800` | `radiusSm` | 4dp 10dp | `captionBold` |
| Error | `#FFEBEE` | `#E53935` | `radiusSm` | 4dp 10dp | `captionBold` |
| Filter Chip (비선택) | `outline` | `onSurfaceSubtle` | `radiusFull` | 6dp 14dp | `body2Medium` |
| Filter Chip (선택) | `primaryLight` | `primary` | `radiusFull` | 6dp 14dp | `body2Medium` |

---

### 8-6. 피드백

#### Snackbar

| 속성 | 값 |
|------|-----|
| 배경 | `onSurface` (#2D2020) |
| 텍스트 | `#FFFFFF`, `body2` |
| 액션 텍스트 | `primary` |
| Border Radius | `radiusSm` |
| 위치 | 탭바 위 12dp |
| 자동 닫힘 | 3초 |

#### Dialog

| 속성 | 값 |
|------|-----|
| 배경 | `surface` |
| Border Radius | `radiusLg` |
| 패딩 | 24dp |
| 딤 Overlay | `#00000066` |
| 제목 | `heading3` |
| 내용 | `body2`, `onSurfaceSubtle` |
| 버튼 정렬 | 우측, 간격 8dp |

#### Empty State

| 속성 | 값 |
|------|-----|
| 아이콘 | 48dp, `onSurfaceSubtle` |
| 타이틀 | `body1Medium`, `onSurfaceSubtle` |
| 설명 | `body2`, `onSurfaceSubtle` |
| 레이아웃 | 세로 중앙 정렬, 요소 간 12dp |

#### Skeleton (로딩)

| 속성 | 값 |
|------|-----|
| 기본 색상 | `outline` |
| 하이라이트 색상 | `surfaceVariant` |
| 애니메이션 | shimmer 좌→우, 주기 1.2초 |
| 형태 | 실제 컴포넌트 형태 유지 |
| 텍스트 대체 | 높이 14dp, `radiusSm` 사각형 |

---

### 8-7. Progress Indicator (임신 진행 바)

| 속성 | 값 |
|------|-----|
| 높이 | 8dp |
| 배경 | `outline` |
| 채움 | `primary` |
| Border Radius | `radiusFull` |
| 진행률 계산 | (현재 주차 / 40) × 100% |

---

## 9. 애니메이션 & 전환 효과

### 화면 전환

| 전환 | 효과 | 시간 |
|------|------|------|
| 탭 전환 | Fade (opacity 0→1) | 200ms |
| 하위 화면 진입 | Slide from right | 300ms |
| 모달/풀스크린 | Slide from bottom | 350ms |
| 뒤로가기 | 역방향 슬라이드 | 300ms |

### 인터랙션

| 요소 | 효과 | 시간 |
|------|------|------|
| 버튼 눌림 | Scale 1.0→0.95 | 100ms |
| 버튼 해제 | Scale 0.95→1.0 | 150ms |
| 카드 탭 | Ripple (Primary 10% 투명) | 200ms |
| FAB 등장 | Scale 0→1 + Fade | 250ms |
| Snackbar 등장/퇴장 | Slide from bottom / Fade | 250ms / 200ms |
| 체크박스 | CheckMark draw | 200ms |
| Progress Bar | Linear animate | 500ms |

### Easing

| 용도 | Easing |
|------|--------|
| 화면 진입 | EaseOutCubic |
| 화면 퇴장 | EaseInCubic |
| 인터랙션 | EaseInOut |
| 탄성 | spring(dampingRatio=0.8, stiffness=300) |

---

## 10. 다크 모드

- **전환 방식:** 시스템 설정 자동 반영 + 앱 내 수동 토글 (더보기 탭)
- **원칙:**
  - 배경: 순수 블랙 대신 다크 브라운-그레이 (`#1A1414`)
  - Primary: 라이트 모드와 동일 유지 (`#FF8FAB`)
  - Surface 계층: background < surface < surfaceVariant 밝기 순 유지
  - Semantic 색상 변경 없음
  - 그림자: 제거, outline 색상으로 레이어 경계 처리

---

## 11. 접근성

- 최소 터치 영역: 48dp × 48dp
- 색상 대비: WCAG AA 기준 (일반 텍스트 4.5:1 이상)
  - `onSurface`(#2D2020) on `background`(#FFFAF5): ~13:1 ✓
  - 버튼 텍스트는 `onPrimary`(#FFFFFF) 사용 (Primary 위 직접 텍스트 금지)
- 텍스트 크기: `sp` 단위 사용 (시스템 폰트 크기 설정 존중)
- 스크린 리더: 모든 아이콘에 `contentDescription` 제공
- 포커스 표시: `primary` 2dp 테두리
