# 산모수첩 디자인 시스템 요구사항

## 1. 브랜드 컨셉

- **키워드:** 따뜻함, 안정감, 부드러움, 사랑
- **무드:** 아기와 엄마의 유대감을 떠올리게 하는 포근하고 밝은 분위기
- **톤:** 친근하고 다정한 말투, 부담 없는 UI

---

## 2. 컬러 팔레트

### Primary
| 이름 | 용도 | 비고 |
|------|------|------|
| Primary | 주요 버튼, 강조 요소, 탭 활성 상태 | 부드러운 핑크 또는 코랄 계열 |
| Primary Variant | Primary의 어두운 변형, 눌림 상태 | |

### Secondary
| 이름 | 용도 | 비고 |
|------|------|------|
| Secondary | 보조 강조, 태그, 배지 | 따뜻한 라벤더 또는 연보라 계열 |
| Secondary Variant | Secondary의 어두운 변형 | |

### Neutral
| 이름 | 용도 | 비고 |
|------|------|------|
| Background | 앱 배경 | 따뜻한 오프화이트 (#FFFAF5 계열) |
| Surface | 카드, 시트, 다이얼로그 배경 | 화이트 |
| On Surface | 본문 텍스트 | 다크 그레이 (순수 블랙 사용 금지) |
| Outline | 입력 필드 테두리, 구분선 | 연한 그레이 |

### Semantic
| 이름 | 용도 |
|------|------|
| Success | 건강 수치 정상 범위 표시 |
| Warning | 주의가 필요한 건강 수치 |
| Error | 입력 오류, 위험 수치 |
| Info | 팁, 안내 메시지 |

---

## 3. 타이포그래피

### 폰트 패밀리
- **한글:** Pretendard 또는 Noto Sans KR (가독성 우선)
- **숫자/영문:** 동일 패밀리 사용

### 텍스트 스타일
| 스타일 | 크기 | 굵기 | 용도 |
|--------|------|------|------|
| Heading 1 | 24sp | Bold | 화면 제목 |
| Heading 2 | 20sp | SemiBold | 섹션 제목 |
| Heading 3 | 18sp | SemiBold | 카드 제목 |
| Body 1 | 16sp | Regular | 본문 텍스트 |
| Body 2 | 14sp | Regular | 보조 텍스트, 설명 |
| Caption | 12sp | Regular | 날짜, 힌트, 라벨 |
| Button | 16sp | SemiBold | 버튼 텍스트 |

### 줄 간격
- Body 텍스트: 1.5 배수
- Heading: 1.3 배수

---

## 4. 아이콘

- **스타일:** Rounded, 2px stroke, 부드러운 인상
- **크기 규격:** 24dp (기본), 20dp (소형), 32dp (대형)
- **컬러:** On Surface (기본), Primary (활성/강조)
- **참고 스타일:** Material Symbols Rounded 또는 유사 라운드 아이콘셋

---

## 5. 간격 및 레이아웃

### 간격 체계 (4dp 기반)
| 토큰 | 값 | 용도 |
|------|-----|------|
| spacing-xs | 4dp | 아이콘과 텍스트 사이 |
| spacing-sm | 8dp | 관련 요소 간 간격 |
| spacing-md | 16dp | 섹션 내 요소 간 간격 |
| spacing-lg | 24dp | 섹션 간 간격 |
| spacing-xl | 32dp | 화면 상하 여백 |

### 화면 패딩
- 좌우 패딩: 16dp (기본)
- 카드 내부 패딩: 16dp

---

## 6. 모서리 둥글기 (Border Radius)

| 토큰 | 값 | 용도 |
|------|-----|------|
| radius-sm | 8dp | 칩, 태그, 작은 버튼 |
| radius-md | 12dp | 카드, 입력 필드 |
| radius-lg | 16dp | 바텀시트, 다이얼로그 |
| radius-full | 9999dp | 원형 아바타, FAB |

---

## 7. 그림자 (Elevation)

| 단계 | 용도 |
|------|------|
| Level 0 | 배경, 평면 요소 |
| Level 1 | 카드 (미세한 그림자) |
| Level 2 | 플로팅 버튼, 드롭다운 |
| Level 3 | 바텀시트, 다이얼로그 |

- 그림자 색상: 블랙 투명도 기반 (부드러운 느낌)

---

## 8. 핵심 컴포넌트 목록

### 버튼
| 종류 | 설명 |
|------|------|
| Primary Button | 채워진 형태, Primary 색상, radius-sm |
| Secondary Button | 아웃라인 형태, Primary 테두리 |
| Text Button | 배경 없음, Primary 색상 텍스트 |
| Icon Button | 아이콘만 있는 원형 버튼 |
| FAB | 주요 액션용 플로팅 버튼 (편지 쓰기 등) |

### 입력
| 종류 | 설명 |
|------|------|
| Text Field | 라벨 + 입력 필드, Outlined 스타일 |
| Text Area | 여러 줄 입력 (편지 작성용) |
| Date Picker | 날짜 선택 |
| Number Input | 건강 수치 입력 (체중, 혈압 등) |

### 카드
| 종류 | 설명 |
|------|------|
| Record Card | 건강 기록 항목 표시 (날짜 + 수치 + 상태) |
| Letter Card | 편지 미리보기 (날짜 + 첫 문장 미리보기) |
| Checklist Card | 체크리스트 항목 그룹 |
| Schedule Card | 진료 일정 표시 (날짜 + 병원 + 메모) |
| Photo Card | 초음파 사진 썸네일 + 주차 정보 |

### 네비게이션
| 종류 | 설명 |
|------|------|
| Bottom Navigation Bar | 하단 탭 (홈, 기록, 편지, 일정, 더보기) |
| Top App Bar | 화면 제목 + 뒤로가기/액션 버튼 |

### 피드백
| 종류 | 설명 |
|------|------|
| Snackbar | 저장 완료, 삭제 등 간단한 피드백 |
| Dialog | 삭제 확인, 중요 알림 |
| Empty State | 데이터 없을 때 안내 일러스트 + 메시지 |

### 기타
| 종류 | 설명 |
|------|------|
| Chip / Tag | 주차 표시 (예: "24주차"), 카테고리 구분 |
| Progress Indicator | 임신 진행률 바 (주차 기반) |
| Divider | 섹션 구분선 |
| Avatar | 프로필 이미지 (원형) |

---

## 9. 다크 모드

- 지원 예정 (추후 정의)
- 기본 원칙: 배경은 순수 블랙 대신 다크 그레이 사용, Primary 색상은 밝기 조정

---

## 10. 접근성

- 최소 터치 영역: 48dp x 48dp
- 색상 대비: WCAG AA 기준 충족 (4.5:1 이상)
- 텍스트 크기: 시스템 폰트 크기 설정 존중
