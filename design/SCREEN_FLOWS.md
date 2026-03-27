# 산모수첩 화면 구성 및 플로우

## 목차

1. [전체 네비게이션 구조](#1-전체-네비게이션-구조)
2. [온보딩 플로우](#2-온보딩-플로우)
3. [홈 탭](#3-홈-탭)
4. [기록 탭](#4-기록-탭)
5. [편지 탭](#5-편지-탭)
6. [일정 탭](#6-일정-탭)
7. [더보기 탭](#7-더보기-탭)
8. [공통 화면](#8-공통-화면)
9. [화면 전환 플로우 다이어그램](#9-화면-전환-플로우-다이어그램)

---

## 1. 전체 네비게이션 구조

### 하단 탭 (BottomNavigationBar)

| 순서 | 탭 이름 | 아이콘 | Route |
|------|---------|--------|-------|
| 1 | 홈 | home | `Screen.Home` |
| 2 | 기록 | favorite-border | `Screen.HealthRecord` |
| 3 | 편지 | edit | `Screen.Letter` |
| 4 | 일정 | date-range | `Screen.Schedule` |
| 5 | 더보기 | menu | `Screen.More` |

### 네비게이션 원칙

- 탭 전환 시 각 탭의 상태 보존 (`saveState = true`, `restoreState = true`)
- 탭 내 하위 화면 진입 시 TopAppBar에 뒤로가기 표시
- 하위 화면에서도 하단 탭 유지 (모달/풀스크린 제외)
- 모달 화면(작성/편집)은 하단 탭 숨김

---

## 2. 온보딩 플로우

> 최초 실행 시 1회만 표시. 임신 기본 정보를 수집한다.

### 2-1. 웰컴 화면 (`Screen.Onboarding.Welcome`)

| 구성 요소 | 설명 |
|-----------|------|
| 일러스트 | 엄마와 아기 따뜻한 일러스트 |
| 타이틀 | "안녕하세요, 예비 맘!" (Heading 1) |
| 설명 | "소중한 아기와의 여정을 함께할게요" (Body 1) |
| 시작 버튼 | Primary Button — "시작하기" |

### 2-2. 출산 예정일 입력 (`Screen.Onboarding.DueDate`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 + "출산 예정일" |
| 안내 텍스트 | "출산 예정일을 알려주세요" (Heading 2) |
| DatePicker | 날짜 선택 컴포넌트 |
| 주차 표시 | 선택 시 현재 주차 자동 계산 표시 (Chip) |
| 다음 버튼 | Primary Button — "다음" |

### 2-3. 프로필 설정 (`Screen.Onboarding.Profile`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 + "프로필" |
| 아바타 | Avatar 컴포넌트 + 사진 추가 버튼 |
| 닉네임 입력 | TextField — "엄마 닉네임" |
| 태명 입력 | TextField — "아기 태명" (선택) |
| 완료 버튼 | Primary Button — "완료" |

### 온보딩 플로우

```
Welcome → DueDate → Profile → Home (메인)
                                 ↑
            (뒤로가기로 이전 단계 복귀 가능)
```

---

## 3. 홈 탭

> 임신 현황을 한눈에 보여주는 대시보드. 빠른 액션과 최근 활동을 제공한다.

### 3-1. 홈 화면 (`Screen.Home`)

스크롤 가능한 세로 레이아웃. 섹션별로 구성된다.

#### 섹션 A: 인사 헤더

| 구성 요소 | 설명 |
|-----------|------|
| 인사 텍스트 | "안녕하세요, {닉네임}님 💛" (Heading 2) |
| 날짜 | 오늘 날짜 (Caption) |
| 프로필 아바타 | 우측 상단 Avatar — 탭 시 더보기 탭 이동 |

#### 섹션 B: 임신 진행률

| 구성 요소 | 설명 |
|-----------|------|
| 주차 Chip | "24주 3일" (Chip, Primary) |
| ProgressIndicator | 전체 40주 대비 진행률 바 |
| D-day | "출산까지 D-112" (Body 2) |
| 아기 크기 비유 | "지금 아기는 옥수수만 해요 🌽" (Body 1) — 주차별 과일/채소 비유 |

#### 섹션 C: 빠른 액션 (가로 스크롤)

| 액션 | 아이콘 | 이동 |
|------|--------|------|
| 체중 기록 | scale | → 기록 추가 화면 (체중 탭) |
| 편지 쓰기 | edit | → 편지 작성 화면 |
| 일정 추가 | calendar-plus | → 일정 추가 화면 |
| 사진 추가 | camera | → 사진 추가 화면 |

각 액션은 원형 아이콘 + 캡션 텍스트 구성.

#### 섹션 D: 이번 주 체크리스트

| 구성 요소 | 설명 |
|-----------|------|
| 섹션 타이틀 | "이번 주 할 일" (Heading 3) + "더보기" TextButton |
| ChecklistCard | 주차별 체크리스트 항목 2~3개 미리보기 |

#### 섹션 E: 다가오는 일정

| 구성 요소 | 설명 |
|-----------|------|
| 섹션 타이틀 | "다가오는 일정" (Heading 3) + "더보기" TextButton |
| ScheduleCard | 가장 가까운 일정 1~2개 표시 |
| EmptyState | 일정 없으면 "등록된 일정이 없어요" |

#### 섹션 F: 최근 기록

| 구성 요소 | 설명 |
|-----------|------|
| 섹션 타이틀 | "최근 기록" (Heading 3) + "더보기" TextButton |
| RecordCard | 최근 건강 기록 1~2개 (가로 스크롤) |

---

## 4. 기록 탭

> 임신 기간 중 건강 기록을 관리한다. 체중, 혈압, 태동, 초음파 사진 등을 기록한다.

### 4-1. 기록 목록 화면 (`Screen.HealthRecord`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | "건강 기록" + 필터 IconButton |
| 카테고리 탭 | 가로 스크롤 Chip 목록: 전체 / 체중 / 혈압 / 태동 / 사진 / 메모 |
| 기록 리스트 | RecordCard 세로 목록 (날짜 내림차순) |
| FAB | "+" 기록 추가 |
| EmptyState | 기록 없을 때 "첫 번째 기록을 추가해 보세요" |

#### 카테고리별 RecordCard 표시

| 카테고리 | RecordCard 내용 |
|----------|----------------|
| 체중 | 날짜 + 체중(kg) + 변화량(+/-) + 정상범위 상태(Success/Warning) |
| 혈압 | 날짜 + 수축기/이완기 mmHg + 상태 |
| 태동 | 날짜 + 횟수 + 측정 시간 |
| 사진 | PhotoCard (초음파 썸네일 + 주차) — 그리드 2열 |
| 메모 | 날짜 + 메모 미리보기 텍스트 |

### 4-2. 기록 추가 화면 (`Screen.HealthRecord.Add`)

> 모달 풀스크린. 하단 탭 숨김.

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | "닫기" (X) + "기록 추가" + "저장" TextButton |
| 카테고리 선택 | Chip 그룹: 체중 / 혈압 / 태동 / 사진 / 메모 |
| 날짜 선택 | DatePicker (기본값: 오늘) |
| 입력 폼 | 카테고리에 따라 동적 변경 (아래 상세) |

#### 카테고리별 입력 폼

**체중:**
| 필드 | 컴포넌트 | 설명 |
|------|----------|------|
| 체중 | NumberInput | "kg" 단위, 소수점 1자리 |
| 메모 | TextField | 선택 입력 |

**혈압:**
| 필드 | 컴포넌트 | 설명 |
|------|----------|------|
| 수축기 | NumberInput | "mmHg" 단위 |
| 이완기 | NumberInput | "mmHg" 단위 |
| 메모 | TextField | 선택 입력 |

**태동:**
| 필드 | 컴포넌트 | 설명 |
|------|----------|------|
| 횟수 | NumberInput | "회" 단위 |
| 측정 시간 | TimePicker | 시:분 |
| 메모 | TextField | 선택 입력 |

**사진:**
| 필드 | 컴포넌트 | 설명 |
|------|----------|------|
| 사진 | 이미지 피커 | 갤러리에서 선택 또는 카메라 촬영 |
| 주차 | Chip (자동) | 현재 주차 자동 입력, 수정 가능 |
| 메모 | TextField | 선택 입력 |

**메모:**
| 필드 | 컴포넌트 | 설명 |
|------|----------|------|
| 제목 | TextField | 선택 입력 |
| 내용 | TextArea | 자유 텍스트 |

### 4-3. 기록 상세 화면 (`Screen.HealthRecord.Detail`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 + "기록 상세" + 편집/삭제 메뉴 (IconButton) |
| 날짜 & 주차 | 날짜 텍스트 + 주차 Chip |
| 수치 표시 | 카테고리별 수치 큰 글씨 (Heading 1) |
| 상태 표시 | 정상/주의/위험 Chip (Semantic 컬러) |
| 메모 | 메모 텍스트 (있을 경우) |
| 차트 | 해당 카테고리의 최근 추이 그래프 (선형 차트) |

### 4-4. 기록 편집 화면 (`Screen.HealthRecord.Edit`)

> 기록 추가 화면과 동일 레이아웃. 기존 값이 채워진 상태.

### 기록 탭 플로우

```
기록 목록 ──(FAB)──→ 기록 추가 ──(저장)──→ 기록 목록 (Snackbar: "저장되었습니다")
    │                                          │
    ├──(카드 탭)──→ 기록 상세 ──(편집)──→ 기록 편집 ──(저장)──→ 기록 상세
    │                  │
    │                  └──(삭제)──→ Dialog 확인 ──(확인)──→ 기록 목록 (Snackbar: "삭제되었습니다")
    │
    └──(카테고리 탭)──→ 필터링된 목록
```

---

## 5. 편지 탭

> 아기에게 보내는 편지를 작성하고 보관한다. 감성적인 UI로 특별한 경험을 제공한다.

### 5-1. 편지 목록 화면 (`Screen.Letter`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | "아기에게 보내는 편지" |
| 편지 수 | "{n}통의 편지" (Caption) |
| 편지 리스트 | LetterCard 세로 목록 (날짜 내림차순) |
| FAB | 편지 쓰기 (edit 아이콘) |
| EmptyState | "아기에게 첫 편지를 써보세요" + "편지 쓰기" 버튼 |

#### LetterCard 구성

| 요소 | 설명 |
|------|------|
| 주차 Chip | "24주차" (Chip) |
| 날짜 | "2026.03.27" (Caption) |
| 미리보기 | 편지 본문 첫 2줄 (Body 2, 말줄임) |
| "편지 보기 →" | TextButton 스타일 링크 |

### 5-2. 편지 작성 화면 (`Screen.Letter.Write`)

> 모달 풀스크린. 감성적인 편지지 배경.

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | "닫기" (X) + "편지 쓰기" + "저장" TextButton |
| 받는 사람 | "To. {태명}" (자동, Heading 3) |
| 날짜 | 오늘 날짜 자동 표시 (Caption) |
| 주차 | 현재 주차 Chip (자동) |
| 편지 내용 | TextArea — 부드러운 편지지 스타일 배경, 줄 간격 넓게 |
| 배경 선택 | 하단 가로 스크롤 — 편지지 배경 테마 3~5종 |

### 5-3. 편지 상세 화면 (`Screen.Letter.Detail`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 + 편집/삭제 메뉴 |
| 편지지 배경 | 선택한 배경 테마 적용 |
| 받는 사람 | "To. {태명}" (Heading 3) |
| 날짜 & 주차 | 날짜 + 주차 Chip |
| 편지 내용 | 본문 텍스트 (Body 1, 넓은 줄 간격) |
| 보낸 사람 | "From. {닉네임}" (하단) |

### 5-4. 편지 편집 화면 (`Screen.Letter.Edit`)

> 편지 작성 화면과 동일 레이아웃. 기존 내용 채워진 상태.

### 편지 탭 플로우

```
편지 목록 ──(FAB)──→ 편지 작성 ──(저장)──→ 편지 목록 (Snackbar: "편지가 저장되었습니다")
    │
    ├──(카드 탭)──→ 편지 상세 ──(편집)──→ 편지 편집 ──(저장)──→ 편지 상세
    │                  │
    │                  └──(삭제)──→ Dialog 확인 ──(확인)──→ 편지 목록
```

---

## 6. 일정 탭

> 진료 일정, 검사 일정 등을 달력으로 관리한다.

### 6-1. 일정 화면 (`Screen.Schedule`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | "일정" + 보기 전환 IconButton (월간/목록) |
| 월간 달력 | 달력 그리드 — 일정 있는 날짜에 Primary 도트 표시 |
| 선택 날짜 일정 | 달력 아래에 해당 날짜 ScheduleCard 목록 |
| FAB | "+" 일정 추가 |
| EmptyState | 선택 날짜에 일정 없으면 "이 날은 일정이 없어요" |

#### 월간 달력 구성

| 요소 | 설명 |
|------|------|
| 월 헤더 | "< 2026년 3월 >" (좌우 화살표로 월 이동) |
| 요일 헤더 | 일 / 월 / 화 / 수 / 목 / 금 / 토 (Caption) |
| 날짜 셀 | 오늘: Primary 원형 배경 / 선택일: Primary-light 배경 / 일정 있는 날: 하단 도트 |

### 6-2. 일정 목록 보기 (`Screen.Schedule` — 목록 모드)

| 구성 요소 | 설명 |
|-----------|------|
| 날짜별 그룹 | 날짜 헤더 (Body 2, Bold) + 해당 날짜 ScheduleCard들 |
| 정렬 | 날짜 오름차순 (다가오는 일정 먼저) |

### 6-3. 일정 추가 화면 (`Screen.Schedule.Add`)

> 모달 풀스크린.

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | "닫기" (X) + "일정 추가" + "저장" TextButton |
| 제목 | TextField — "진료 제목" |
| 날짜 | DatePicker |
| 시간 | TimePicker (선택) |
| 장소 | TextField — "병원/장소" (선택) |
| 카테고리 | Chip 그룹: 정기검진 / 초음파 / 혈액검사 / 예방접종 / 기타 |
| 메모 | TextArea — 추가 메모 (선택) |
| 알림 | 알림 설정: 없음 / 당일 / 1일 전 / 3일 전 / 1주 전 |

### 6-4. 일정 상세 화면 (`Screen.Schedule.Detail`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 + 편집/삭제 메뉴 |
| 카테고리 Chip | "정기검진" 등 |
| 제목 | Heading 2 |
| 날짜/시간 | 아이콘 + 텍스트 |
| 장소 | 아이콘 + 텍스트 |
| 알림 | 아이콘 + 알림 설정 텍스트 |
| 메모 | 메모 텍스트 (있을 경우) |

### 6-5. 일정 편집 화면 (`Screen.Schedule.Edit`)

> 일정 추가 화면과 동일. 기존 값 채워진 상태.

### 일정 탭 플로우

```
일정 (달력) ──(FAB)──→ 일정 추가 ──(저장)──→ 일정 (달력) (Snackbar: "일정이 추가되었습니다")
    │
    ├──(날짜 탭)──→ 해당 날짜 일정 목록
    │                    │
    │                    └──(카드 탭)──→ 일정 상세 ──(편집)──→ 일정 편집 ──(저장)──→ 일정 상세
    │                                      │
    │                                      └──(삭제)──→ Dialog ──→ 일정 (달력)
    │
    └──(보기 전환)──→ 일정 (목록 모드) ←──→ 일정 (달력 모드)
```

---

## 7. 더보기 탭

> 프로필, 설정, 앱 정보 등 부가 기능을 제공한다.

### 7-1. 더보기 화면 (`Screen.More`)

| 구성 요소 | 설명 |
|-----------|------|
| 프로필 섹션 | Avatar + 닉네임 + 주차 Chip + "프로필 편집" TextButton |
| Divider | |
| 메뉴 리스트 | 아래 메뉴 항목들 (아이콘 + 텍스트 + 우측 chevron) |

#### 메뉴 항목

| 그룹 | 메뉴 | 아이콘 | 이동 |
|------|------|--------|------|
| 임신 정보 | 출산 예정일 변경 | calendar | → 예정일 변경 화면 |
| 임신 정보 | 주차별 체크리스트 | check-square | → 체크리스트 화면 |
| 임신 정보 | 초음파 앨범 | image | → 사진 갤러리 화면 |
| 설정 | 알림 설정 | bell | → 알림 설정 화면 |
| 설정 | 다크 모드 | moon | 토글 스위치 (인라인) |
| 설정 | 데이터 백업 | cloud-upload | → 백업 화면 |
| 정보 | 앱 정보 | info | → 앱 정보 화면 |
| 정보 | 이용약관 | file-text | → 약관 화면 |
| 정보 | 개인정보 처리방침 | shield | → 개인정보 화면 |

### 7-2. 프로필 편집 화면 (`Screen.More.ProfileEdit`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 + "프로필 편집" + "저장" TextButton |
| 아바타 | Avatar (대형) + "사진 변경" TextButton |
| 닉네임 | TextField |
| 태명 | TextField |

### 7-3. 주차별 체크리스트 화면 (`Screen.More.Checklist`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 + "주차별 체크리스트" |
| 주차 선택 | 가로 스크롤 Chip 목록 (현재 주차 강조) |
| ChecklistCard | 해당 주차 체크 항목 목록 (체크박스 + 텍스트) |
| 진행률 | ProgressIndicator (완료/전체) |

### 7-4. 사진 갤러리 화면 (`Screen.More.PhotoGallery`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 + "초음파 앨범" |
| 그리드 | PhotoCard 2열 그리드 (주차순 또는 날짜순) |
| 탭 시 | 사진 상세 보기 (풀스크린 이미지 뷰어) |

### 7-5. 앱 정보 화면 (`Screen.More.AppInfo`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 + "앱 정보" |
| 앱 아이콘 | 앱 아이콘 이미지 |
| 앱 이름 | "산모수첩" (Heading 2) |
| 버전 | "v1.0.0" (Body 2) |
| 저작권 | 저작권 문구 (Caption) |

### 더보기 탭 플로우

```
더보기 ──→ 프로필 편집 ──(저장)──→ 더보기
    │
    ├──→ 예정일 변경 ──(저장)──→ 더보기 (홈 주차 갱신)
    ├──→ 주차별 체크리스트
    ├──→ 초음파 앨범 ──(사진 탭)──→ 사진 상세
    ├──→ 알림 설정
    ├──→ 데이터 백업
    ├──→ 앱 정보
    ├──→ 이용약관
    └──→ 개인정보 처리방침
```

---

## 8. 공통 화면

### 8-1. 삭제 확인 Dialog

> 기록, 편지, 일정 삭제 시 공통 사용.

| 구성 요소 | 설명 |
|-----------|------|
| 타이틀 | "{항목}을 삭제할까요?" (Heading 3) |
| 메시지 | "삭제된 {항목}은 복구할 수 없습니다." (Body 2) |
| 취소 버튼 | Secondary Button — "취소" |
| 삭제 버튼 | Primary Button (Error 색상) — "삭제" |

### 8-2. 저장 완료 Snackbar

| 구성 요소 | 설명 |
|-----------|------|
| 메시지 | "저장되었습니다 ✓" 등 |
| 액션 | "확인" TextButton |
| 자동 닫힘 | 3초 후 자동 사라짐 |

### 8-3. 뒤로가기 경고 Dialog

> 작성/편집 중 뒤로가기 시 변경사항이 있으면 표시.

| 구성 요소 | 설명 |
|-----------|------|
| 타이틀 | "작성을 그만둘까요?" |
| 메시지 | "저장하지 않은 내용은 사라집니다." |
| 계속 작성 | Secondary Button — "계속 작성" |
| 나가기 | Text Button — "나가기" |

---

## 9. 화면 전환 플로우 다이어그램

### 전체 앱 플로우

```
[앱 시작]
    │
    ├── (첫 실행) ──→ 온보딩: Welcome → DueDate → Profile ──→ [홈]
    │
    └── (이후 실행) ──→ [홈]


[하단 탭 네비게이션]
┌─────────────────────────────────────────────────────────┐
│                                                         │
│   [홈]    [기록]    [편지]    [일정]    [더보기]           │
│                                                         │
└─────────────────────────────────────────────────────────┘
     │         │         │         │          │
     ▼         ▼         ▼         ▼          ▼
   홈 화면   기록 목록   편지 목록   일정 달력   더보기 메뉴
```

### 크로스-탭 이동 (홈에서 다른 탭으로)

```
홈 화면
  ├── [빠른 액션: 체중 기록] ──→ 기록 추가 (체중)
  ├── [빠른 액션: 편지 쓰기] ──→ 편지 작성
  ├── [빠른 액션: 일정 추가] ──→ 일정 추가
  ├── [빠른 액션: 사진 추가] ──→ 기록 추가 (사진)
  ├── [체크리스트 더보기]     ──→ 주차별 체크리스트
  ├── [일정 더보기]           ──→ 일정 탭
  ├── [기록 더보기]           ──→ 기록 탭
  └── [프로필 아바타]         ──→ 더보기 탭
```

### Screen sealed class 확장 구조

```kotlin
@Serializable
sealed class Screen {
    // 온보딩
    @Serializable sealed class Onboarding : Screen() {
        @Serializable data object Welcome : Onboarding()
        @Serializable data object DueDate : Onboarding()
        @Serializable data object Profile : Onboarding()
    }

    // 메인 탭
    @Serializable data object Home : Screen()

    // 기록
    @Serializable data object HealthRecord : Screen()
    @Serializable data class HealthRecordAdd(val category: String? = null) : Screen()
    @Serializable data class HealthRecordDetail(val recordId: String) : Screen()
    @Serializable data class HealthRecordEdit(val recordId: String) : Screen()

    // 편지
    @Serializable data object Letter : Screen()
    @Serializable data object LetterWrite : Screen()
    @Serializable data class LetterDetail(val letterId: String) : Screen()
    @Serializable data class LetterEdit(val letterId: String) : Screen()

    // 일정
    @Serializable data object Schedule : Screen()
    @Serializable data object ScheduleAdd : Screen()
    @Serializable data class ScheduleDetail(val scheduleId: String) : Screen()
    @Serializable data class ScheduleEdit(val scheduleId: String) : Screen()

    // 더보기
    @Serializable data object More : Screen()
    @Serializable data object ProfileEdit : Screen()
    @Serializable data object DueDateChange : Screen()
    @Serializable data object WeeklyChecklist : Screen()
    @Serializable data object PhotoGallery : Screen()
    @Serializable data object NotificationSettings : Screen()
    @Serializable data object DataBackup : Screen()
    @Serializable data object AppInfo : Screen()
    @Serializable data object Terms : Screen()
    @Serializable data object Privacy : Screen()
}
```

---

## 부록: 화면별 MVI 상태 요약

> 각 화면에서 관리해야 할 주요 State/Intent 정리.

### 홈

| State | 타입 | 설명 |
|-------|------|------|
| nickname | String | 사용자 닉네임 |
| babyName | String | 태명 |
| currentWeek | Int | 현재 임신 주차 |
| currentDay | Int | 현재 주차 내 일수 |
| dDay | Int | 출산까지 남은 일수 |
| recentRecords | List | 최근 기록 목록 |
| upcomingSchedules | List | 다가오는 일정 목록 |
| weeklyChecklist | List | 이번 주 체크리스트 |

### 기록

| State | 타입 | 설명 |
|-------|------|------|
| records | List | 기록 목록 |
| selectedCategory | Category | 선택된 카테고리 필터 |
| isLoading | Boolean | 로딩 상태 |

| Intent | 설명 |
|--------|------|
| SelectCategory | 카테고리 필터 변경 |
| DeleteRecord | 기록 삭제 |
| SaveRecord | 기록 저장 |

### 편지

| State | 타입 | 설명 |
|-------|------|------|
| letters | List | 편지 목록 |
| letterContent | String | 작성 중 편지 내용 |
| selectedBackground | Int | 선택된 편지지 배경 |

### 일정

| State | 타입 | 설명 |
|-------|------|------|
| selectedDate | LocalDate | 달력 선택 날짜 |
| currentMonth | YearMonth | 현재 표시 월 |
| schedules | Map | 날짜별 일정 |
| viewMode | ViewMode | 달력/목록 보기 모드 |
