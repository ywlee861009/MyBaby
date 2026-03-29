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
9. [권한 요청 플로우](#9-권한-요청-플로우)
10. [화면 전환 플로우 다이어그램](#10-화면-전환-플로우-다이어그램)
11. [MVI 상태 전체 명세](#11-mvi-상태-전체-명세)

---

## 1. 전체 네비게이션 구조

### 하단 탭 (BottomNavigationBar)

| 순서 | 탭 이름 | 아이콘 | Route |
|------|---------|--------|-------|
| 1 | 홈 | home | `Screen.Home` |
| 2 | 기록 | favorite_border | `Screen.HealthRecord` |
| 3 | 편지 | edit | `Screen.Letter` |
| 4 | 일정 | date_range | `Screen.Schedule` |
| 5 | 더보기 | menu | `Screen.More` |

### 네비게이션 원칙

- 탭 전환 시 각 탭의 상태 보존 (`saveState = true`, `restoreState = true`)
- 탭 내 하위 화면 진입 시 TopAppBar에 뒤로가기 표시
- 모달 화면(작성/편집) → 하단 탭 숨김
- 하위 화면에서도 하단 탭 유지 (모달/풀스크린 제외)

---

## 2. 온보딩 플로우

> 최초 실행 시 1회만 표시. Baby 데이터 없으면 온보딩 진입.

### 2-1. 아기 정보 입력 (`Screen.Setup.BabyInfo`)

| 구성 요소 | 설명 |
|-----------|------|
| 타이틀 | "아기의 태명을 알려주세요" (Heading 2) |
| 태명 입력 | TextField — 최대 20자 |
| 성별 선택 | Chip 그룹: 남자아이 / 여자아이 / 모름 |
| 출생 여부 | "이미 태어났어요" 토글 |
| 다음 버튼 | PrimaryButton — "다음" (태명 입력 전 Disabled) |

### 2-2. 임신/출산 정보 입력 (`Screen.Setup.PregnancyInfo`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 |
| 조건 분기 | 이미 태어난 경우 → 생일 DatePicker / 미출생 → 출산 예정일 DatePicker |
| 주차 자동 표시 | 예정일 선택 시 현재 주차 Chip으로 자동 계산 |
| 완료 버튼 | PrimaryButton — "시작하기" |

### 온보딩 플로우

```
[앱 시작] → Baby 데이터 확인
    ├── 없음 → BabyInfo → PregnancyInfo → Home (popUpTo 0, inclusive)
    └── 있음 → Home
```

---

## 3. 홈 탭

> 임신 현황 대시보드. 빠른 액션과 최근 활동 제공.

### 3-1. 홈 화면 (`Screen.Home`)

스크롤 가능한 세로 레이아웃. 섹션별 구성.

#### 섹션 A: 인사 헤더

| 구성 요소 | 설명 |
|-----------|------|
| 인사 텍스트 | "안녕하세요, {닉네임}님" (Heading 2) |
| 날짜 | 오늘 날짜 (Caption) |
| 프로필 아바타 | 우측 상단 — 탭 시 더보기 탭 이동 |

#### 섹션 B: 임신 진행률

| 구성 요소 | 설명 |
|-----------|------|
| 주차 Chip | "{n}주 {d}일" (Primary Chip) |
| ProgressIndicator | 전체 40주 대비 진행률 바 |
| D-day | "출산까지 D-{n}" (Body 2) |
| 아기 크기 비유 | 주차별 과일/채소 비유 텍스트 (Body 1) |

**주차별 아기 크기 비유 (1~40주):**

| 주차 범위 | 비유 |
|-----------|------|
| 4~6주 | 참깨 |
| 7~8주 | 블루베리 |
| 9~10주 | 포도 |
| 11~12주 | 자두 |
| 13~15주 | 복숭아 |
| 16~18주 | 아보카도 |
| 19~21주 | 망고 |
| 22~24주 | 옥수수 |
| 25~27주 | 양상추 |
| 28~30주 | 가지 |
| 31~33주 | 파인애플 |
| 34~36주 | 멜론 |
| 37~40주 | 수박 |

#### 섹션 C: 빠른 액션 (가로 스크롤)

| 액션 | 아이콘 | 이동 |
|------|--------|------|
| 체중 기록 | scale | → HealthRecordAdd(category="weight") |
| 편지 쓰기 | edit | → LetterWrite |
| 일정 추가 | calendar_add_on | → ScheduleAdd |
| 사진 추가 | photo_camera | → HealthRecordAdd(category="photo") |

각 액션: 원형 아이콘(40dp, primaryLight 배경) + Caption 텍스트

#### 섹션 D: 이번 주 체크리스트

| 구성 요소 | 설명 |
|-----------|------|
| 섹션 타이틀 | "이번 주 할 일" (Heading 3) + "더보기" TextButton |
| ChecklistCard | 주차별 항목 2~3개 미리보기 (체크박스 포함) |
| EmptyState | "이번 주 할 일이 없어요" |

#### 섹션 E: 다가오는 일정

| 구성 요소 | 설명 |
|-----------|------|
| 섹션 타이틀 | "다가오는 일정" (Heading 3) + "더보기" TextButton |
| ScheduleCard | 가장 가까운 일정 1~2개 |
| EmptyState | "등록된 일정이 없어요" |

#### 섹션 F: 최근 기록

| 구성 요소 | 설명 |
|-----------|------|
| 섹션 타이틀 | "최근 기록" (Heading 3) + "더보기" TextButton |
| RecordCard | 최근 건강 기록 2개 (가로 스크롤) |
| EmptyState | "기록이 없어요" |

---

## 4. 기록 탭

> 체중, 혈압, 태동, 초음파 사진, 메모 등 건강 기록 관리.

### 4-1. 기록 목록 화면 (`Screen.HealthRecord`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | "건강 기록" + 필터 IconButton |
| 카테고리 탭 | 가로 스크롤 Filter Chip: 전체 / 체중 / 혈압 / 태동 / 사진 / 메모 |
| 기록 리스트 | RecordCard 세로 목록 (날짜 내림차순) |
| FAB | "+" 기록 추가 |
| EmptyState | "첫 번째 기록을 추가해 보세요" |
| 로딩 | RecordCard Skeleton 3개 |
| 에러 | "기록을 불러오지 못했어요" + 재시도 버튼 |

#### 카테고리별 RecordCard 표시

| 카테고리 | 표시 내용 |
|----------|-----------|
| 체중 | 날짜 + 체중(kg) + 변화량(+/-) + 상태 Chip |
| 혈압 | 날짜 + 수축기/이완기 mmHg + 상태 Chip |
| 태동 | 날짜 + 횟수 + 측정 시간 |
| 사진 | PhotoCard 그리드 2열 (초음파 썸네일 + 주차) |
| 메모 | 날짜 + 미리보기 텍스트 2줄 |

### 4-2. 기록 추가 화면 (`Screen.HealthRecordAdd`)

> 모달 풀스크린. 하단 탭 숨김.

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | X 닫기 + "기록 추가" + "저장" TextButton |
| 카테고리 선택 | Filter Chip 그룹 |
| 날짜 선택 | DatePicker (기본: 오늘) |
| 입력 폼 | 카테고리별 동적 변경 |

**체중:**

| 필드 | 컴포넌트 |
|------|----------|
| 체중 | NumberInput ("kg", 소수점 1자리) |
| 메모 | TextField (선택) |

**혈압:**

| 필드 | 컴포넌트 |
|------|----------|
| 수축기 | NumberInput ("mmHg") |
| 이완기 | NumberInput ("mmHg") |
| 메모 | TextField (선택) |

**태동:**

| 필드 | 컴포넌트 |
|------|----------|
| 횟수 | NumberInput ("회") |
| 측정 시간 | TimePicker |
| 메모 | TextField (선택) |

**사진:**

| 필드 | 컴포넌트 |
|------|----------|
| 사진 | 이미지 피커 (갤러리/카메라 선택 BottomSheet) |
| 주차 | Chip (자동, 수정 가능) |
| 메모 | TextField (선택) |

**메모:**

| 필드 | 컴포넌트 |
|------|----------|
| 제목 | TextField (선택) |
| 내용 | TextArea (필수) |

### 4-3. 기록 상세 화면 (`Screen.HealthRecordDetail`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 + "기록 상세" + 편집/삭제 메뉴 |
| 날짜 & 주차 | 날짜 텍스트 + 주차 Chip |
| 수치 표시 | 카테고리별 수치 (Heading 1) |
| 상태 Chip | 정상/주의/위험 (Semantic 컬러) |
| 메모 | 메모 텍스트 (있을 경우) |
| 차트 | 해당 카테고리 최근 추이 선형 차트 |
| 에러 | "기록을 불러오지 못했어요" |

### 4-4. 기록 편집 화면 (`Screen.HealthRecordEdit`)

> 기록 추가 화면과 동일 레이아웃. 기존 값 채워진 상태.

### 기록 탭 플로우

```
기록 목록 ──(FAB)──→ 기록 추가 ──(저장)──→ 기록 목록 (Snackbar: "저장되었습니다")
    │
    ├──(카드 탭)──→ 기록 상세 ──(편집)──→ 기록 편집 ──(저장)──→ 기록 상세
    │                  │
    │                  └──(삭제)──→ Dialog ──(확인)──→ 기록 목록 (Snackbar: "삭제되었습니다")
    │
    └──(카테고리 Chip)──→ 필터링된 목록
```

---

## 5. 편지 탭

> 아기에게 보내는 편지 작성 및 보관. 감성적인 편지지 UI.

### 5-1. 편지 목록 화면 (`Screen.Letter`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | "아기에게 보내는 편지" |
| 편지 수 | "{n}통의 편지" (Caption) |
| 편지 리스트 | LetterCard 세로 목록 (날짜 내림차순) |
| FAB | 편지 쓰기 (edit 아이콘) |
| EmptyState | "아기에게 첫 편지를 써보세요" + "편지 쓰기" 버튼 |
| 로딩 | LetterCard Skeleton 3개 |
| 에러 | "편지를 불러오지 못했어요" + 재시도 버튼 |

#### LetterCard 구성

| 요소 | 설명 |
|------|------|
| 주차 Chip | "{n}주차" |
| 날짜 | "YYYY.MM.DD" (Caption) |
| 미리보기 | 본문 첫 2줄 (Body2, Ellipsis) |
| 링크 | "편지 보기 →" (TextButton) |

### 5-2. 편지 작성 화면 (`Screen.LetterWrite`)

> 모달 풀스크린. 감성적인 편지지 배경.

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | X 닫기 + "편지 쓰기" + "저장" TextButton |
| 받는 사람 | "To. {태명}" (Heading 3, 자동) |
| 날짜 | 오늘 날짜 (Caption, 자동) |
| 주차 | 현재 주차 Chip (자동) |
| 편지 내용 | TextArea (편지지 배경 위, 줄 간격 1.8×) |
| 배경 선택 | 하단 가로 스크롤, 배경 테마 5종 원형 프리뷰 |

**배경 선택 UI:**
- 크기: 36dp 원형 (선택 시 primary 2dp 테두리)
- 5종: letterDefault, letterBlue, letterGreen, letterPink, letterYellow

### 5-3. 편지 상세 화면 (`Screen.LetterDetail`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 + 편집/삭제 메뉴 (IconButton) |
| 편지지 배경 | 선택한 배경 테마 전체 |
| 받는 사람 | "To. {태명}" (Heading 3) |
| 날짜 & 주차 | 날짜 + 주차 Chip |
| 편지 내용 | 본문 텍스트 (Body1, 줄 간격 1.8×) |
| 보낸 사람 | "From. {닉네임}" (하단 우측 정렬) |
| 에러 | "편지를 불러오지 못했어요" |

### 5-4. 편지 편집 화면 (`Screen.LetterEdit`)

> 편지 작성 화면과 동일. 기존 내용 채워진 상태.

### 편지 탭 플로우

```
편지 목록 ──(FAB)──→ 편지 작성 ──(저장)──→ 편지 목록 (Snackbar: "편지가 저장되었습니다")
    │
    └──(카드 탭)──→ 편지 상세 ──(편집)──→ 편지 편집 ──(저장)──→ 편지 상세
                       │
                       └──(삭제)──→ Dialog ──(확인)──→ 편지 목록
```

---

## 6. 일정 탭

> 진료, 검사 일정 달력 관리.

### 6-1. 일정 달력 화면 (`Screen.Schedule`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | "일정" + 보기 전환 IconButton |
| 월간 달력 | 달력 그리드 — 일정 있는 날짜에 Primary 도트 |
| 선택 날짜 일정 | 달력 아래 ScheduleCard 목록 |
| FAB | "+" 일정 추가 |
| EmptyState | "이 날은 일정이 없어요" |
| 로딩 | Skeleton |
| 에러 | "일정을 불러오지 못했어요" + 재시도 |

#### 달력 구성

| 요소 | 설명 |
|------|------|
| 월 헤더 | "< 2026년 3월 >" (좌우 화살표로 월 이동) |
| 요일 헤더 | 일/월/화/수/목/금/토 (Caption) |
| 오늘 | Primary 원형 배경 |
| 선택일 | primaryLight 배경 |
| 일정 있는 날 | 하단 Primary 도트 4dp |

### 6-2. 일정 목록 모드 (`Screen.Schedule` — 목록 모드)

| 구성 요소 | 설명 |
|-----------|------|
| 날짜별 그룹 | 날짜 헤더(Body2 Bold) + ScheduleCard들 |
| 정렬 | 날짜 오름차순 |

### 6-3. 일정 추가 화면 (`Screen.ScheduleAdd`)

> 모달 풀스크린.

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | X 닫기 + "일정 추가" + "저장" TextButton |
| 제목 | TextField (필수) |
| 날짜 | DatePicker |
| 시간 | TimePicker (선택) |
| 장소 | TextField (선택) |
| 카테고리 | Filter Chip: 정기검진 / 초음파 / 혈액검사 / 예방접종 / 기타 |
| 메모 | TextArea (선택) |
| 알림 | Chip 그룹: 없음 / 당일 / 1일 전 / 3일 전 / 1주 전 |

### 6-4. 일정 상세 화면 (`Screen.ScheduleDetail`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 + 편집/삭제 메뉴 |
| 카테고리 Chip | |
| 제목 | Heading 2 |
| 날짜/시간 | 아이콘 + 텍스트 |
| 장소 | 아이콘 + 텍스트 |
| 알림 | 아이콘 + 알림 설정 텍스트 |
| 메모 | 있을 경우 표시 |
| 에러 | "일정을 불러오지 못했어요" |

### 6-5. 일정 편집 화면 (`Screen.ScheduleEdit`)

> 일정 추가 화면과 동일. 기존 값 채워진 상태.

### 일정 탭 플로우

```
일정 (달력) ──(FAB)──→ 일정 추가 ──(저장)──→ 일정 (Snackbar: "일정이 추가되었습니다")
    │
    ├──(날짜 탭)──→ 해당 날짜 일정 목록
    │                   └──(카드 탭)──→ 일정 상세 ──(편집)──→ 일정 편집 ──(저장)──→ 일정 상세
    │                                      └──(삭제)──→ Dialog ──→ 일정 달력
    │
    └──(보기 전환)──→ 일정 목록 모드 ↔ 달력 모드
```

---

## 7. 더보기 탭

### 7-1. 더보기 화면 (`Screen.More`)

| 구성 요소 | 설명 |
|-----------|------|
| 프로필 섹션 | Avatar + 닉네임 + 주차 Chip + "프로필 편집" TextButton |
| Divider | |
| 메뉴 리스트 | 아이콘 + 텍스트 + 우측 chevron |

#### 메뉴 항목

| 그룹 | 메뉴 | 이동 |
|------|------|------|
| 임신 정보 | 출산 예정일 변경 | → DueDateChange |
| 임신 정보 | 주차별 체크리스트 | → WeeklyChecklist |
| 임신 정보 | 초음파 앨범 | → PhotoGallery |
| 설정 | 알림 설정 | → NotificationSettings |
| 설정 | 다크 모드 | 인라인 토글 스위치 |
| 설정 | 데이터 백업 | → DataBackup |
| 정보 | 앱 정보 | → AppInfo |
| 정보 | 이용약관 | → Terms |
| 정보 | 개인정보 처리방침 | → Privacy |

### 7-2. 프로필 편집 (`Screen.More.ProfileEdit`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 + "프로필 편집" + "저장" TextButton |
| Avatar | 대형 + "사진 변경" TextButton |
| 닉네임 | TextField |
| 태명 | TextField |

### 7-3. 주차별 체크리스트 (`Screen.More.WeeklyChecklist`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 + "주차별 체크리스트" |
| 주차 선택 | 가로 스크롤 Filter Chip (현재 주차 강조) |
| ChecklistCard | 체크박스 + 항목 텍스트 목록 |
| 진행률 | ProgressIndicator (완료/전체) |

### 7-4. 초음파 앨범 (`Screen.More.PhotoGallery`)

| 구성 요소 | 설명 |
|-----------|------|
| TopAppBar | 뒤로가기 + "초음파 앨범" |
| 그리드 | PhotoCard 2열 (주차순/날짜순) |
| 탭 시 | 풀스크린 이미지 뷰어 (pinch-to-zoom) |

### 7-5. 앱 정보 (`Screen.More.AppInfo`)

| 구성 요소 | 설명 |
|-----------|------|
| 앱 아이콘 | 72dp 원형 |
| 앱 이름 | "산모수첩" (Heading 2) |
| 버전 | "v1.0.0" (Body 2) |
| 저작권 | Caption |

---

## 8. 공통 화면

### 8-1. 삭제 확인 Dialog

| 구성 요소 | 설명 |
|-----------|------|
| 타이틀 | "{항목}을 삭제할까요?" |
| 메시지 | "삭제된 {항목}은 복구할 수 없습니다." |
| 취소 | SecondaryButton — "취소" |
| 삭제 | PrimaryButton (error 색상) — "삭제" |

### 8-2. 뒤로가기 경고 Dialog

> 작성/편집 중 변경사항이 있을 때 표시.

| 구성 요소 | 설명 |
|-----------|------|
| 타이틀 | "작성을 그만둘까요?" |
| 메시지 | "저장하지 않은 내용은 사라집니다." |
| 계속 작성 | SecondaryButton — "계속 작성" |
| 나가기 | TextButton — "나가기" |

### 8-3. Snackbar

| 상황 | 메시지 |
|------|--------|
| 저장 완료 | "저장되었습니다" |
| 편지 저장 | "편지가 저장되었습니다" |
| 일정 저장 | "일정이 추가되었습니다" |
| 삭제 완료 | "삭제되었습니다" |
| 오류 | "오류가 발생했어요. 다시 시도해 주세요" |
| 네트워크 없음 | "인터넷 연결을 확인해 주세요" |

---

## 9. 권한 요청 플로우

### 9-1. 카메라 / 갤러리 권한 (사진 추가 시)

```
[사진 추가 버튼 탭]
    │
    ├── 권한 있음 ──→ 이미지 피커 BottomSheet
    │                   ├── 카메라 촬영 → 카메라 실행 → 사진 선택 완료
    │                   └── 갤러리 선택 → 갤러리 실행 → 사진 선택 완료
    │
    └── 권한 없음 ──→ 권한 요청 Dialog
                        ├── 허용 → 시스템 권한 다이얼로그 → 허용 → 이미지 피커
                        ├── 거부 → 아무 동작 없음
                        └── 영구 거부 → "설정에서 권한을 허용해 주세요" Dialog → 설정 앱 이동
```

### 9-2. 알림 권한 (일정 알림 설정 시)

```
[알림 설정 선택 (당일/1일 전 등)]
    │
    ├── 권한 있음 ──→ 알림 예약 완료
    │
    └── 권한 없음 (Android 13+) ──→ 시스템 권한 다이얼로그
                                        ├── 허용 → 알림 예약 완료
                                        └── 거부 → Snackbar "알림을 받으려면 알림 권한이 필요해요"
```

---

## 10. 화면 전환 플로우 다이어그램

### 전체 앱 플로우

```
[앱 시작]
    ├── Baby 없음 → BabyInfo → PregnancyInfo → [홈]
    └── Baby 있음 → [홈]

[하단 탭]
 [홈] ─ [기록] ─ [편지] ─ [일정] ─ [더보기]
```

### 크로스-탭 이동 (홈 → 다른 탭)

```
홈 화면
  ├── [체중 기록]    → HealthRecordAdd(category=weight)
  ├── [편지 쓰기]    → LetterWrite
  ├── [일정 추가]    → ScheduleAdd
  ├── [사진 추가]    → HealthRecordAdd(category=photo)
  ├── [체크리스트 더보기] → WeeklyChecklist
  ├── [일정 더보기]  → Schedule 탭
  ├── [기록 더보기]  → HealthRecord 탭
  └── [프로필 아바타] → More 탭
```

### Screen sealed class 전체 구조

```kotlin
@Serializable
sealed class Screen {
    // 온보딩
    @Serializable sealed class Setup : Screen() {
        @Serializable data object BabyInfo : Setup()
        @Serializable data class PregnancyInfo(
            val nickname: String,
            val gender: String,
            val isBorn: Boolean
        ) : Setup()
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

## 11. MVI 상태 전체 명세

### 홈 (`HomeViewModel`)

```kotlin
data class HomeState(
    val isLoading: Boolean = true,
    val nickname: String = "",
    val babyName: String = "",
    val currentWeek: Int = 0,
    val currentDay: Int = 0,
    val dDay: Int = 0,
    val babySizeDescription: String = "",
    val recentRecords: List<HealthRecord> = emptyList(),
    val upcomingSchedules: List<Schedule> = emptyList(),
    val weeklyChecklist: List<ChecklistItem> = emptyList(),
    val error: String? = null
)

sealed class HomeIntent {
    object LoadData : HomeIntent()
    object NavigateToLetterWrite : HomeIntent()
    data class NavigateToRecordAdd(val category: String) : HomeIntent()
    object NavigateToScheduleAdd : HomeIntent()
    object NavigateToMore : HomeIntent()
    object NavigateToHealthRecord : HomeIntent()
    object NavigateToSchedule : HomeIntent()
    object NavigateToWeeklyChecklist : HomeIntent()
    data class ToggleChecklistItem(val itemId: String) : HomeIntent()
}
```

### 기록 목록 (`HealthRecordListViewModel`)

```kotlin
data class HealthRecordListState(
    val isLoading: Boolean = true,
    val records: List<HealthRecord> = emptyList(),
    val selectedCategory: RecordCategory = RecordCategory.ALL,
    val error: String? = null
)

sealed class HealthRecordListIntent {
    object LoadRecords : HealthRecordListIntent()
    data class SelectCategory(val category: RecordCategory) : HealthRecordListIntent()
    data class DeleteRecord(val recordId: String) : HealthRecordListIntent()
    object NavigateToAdd : HealthRecordListIntent()
    data class NavigateToDetail(val recordId: String) : HealthRecordListIntent()
}
```

### 기록 추가/편집 (`HealthRecordEditViewModel`)

```kotlin
data class HealthRecordEditState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val category: RecordCategory = RecordCategory.WEIGHT,
    val date: LocalDate = LocalDate.today(),
    val weightKg: String = "",
    val systolicBp: String = "",
    val diastolicBp: String = "",
    val kickCount: String = "",
    val kickTime: LocalTime? = null,
    val photoUri: String? = null,
    val weekNumber: Int = 0,
    val memoTitle: String = "",
    val memoContent: String = "",
    val error: String? = null,
    val isDirty: Boolean = false,
    val saveSuccess: Boolean = false
)

sealed class HealthRecordEditIntent {
    data class SetCategory(val category: RecordCategory) : HealthRecordEditIntent()
    data class SetDate(val date: LocalDate) : HealthRecordEditIntent()
    data class SetWeight(val value: String) : HealthRecordEditIntent()
    data class SetSystolicBp(val value: String) : HealthRecordEditIntent()
    data class SetDiastolicBp(val value: String) : HealthRecordEditIntent()
    data class SetKickCount(val value: String) : HealthRecordEditIntent()
    data class SetKickTime(val time: LocalTime) : HealthRecordEditIntent()
    data class SetPhoto(val uri: String) : HealthRecordEditIntent()
    data class SetMemoTitle(val value: String) : HealthRecordEditIntent()
    data class SetMemoContent(val value: String) : HealthRecordEditIntent()
    object Save : HealthRecordEditIntent()
    object RequestBack : HealthRecordEditIntent()  // isDirty 확인 후 경고 Dialog
}
```

### 편지 목록 (`LetterListViewModel`)

```kotlin
data class LetterListState(
    val isLoading: Boolean = true,
    val letters: List<Letter> = emptyList(),
    val error: String? = null
)

sealed class LetterListIntent {
    object LoadLetters : LetterListIntent()
    object NavigateToWrite : LetterListIntent()
    data class NavigateToDetail(val letterId: String) : LetterListIntent()
}
```

### 편지 작성/편집 (`LetterEditViewModel`)

```kotlin
data class LetterEditState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val babyName: String = "",
    val nickname: String = "",
    val content: String = "",
    val selectedBackground: LetterBackground = LetterBackground.DEFAULT,
    val currentWeek: Int = 0,
    val date: LocalDate = LocalDate.today(),
    val isDirty: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

sealed class LetterEditIntent {
    data class SetContent(val content: String) : LetterEditIntent()
    data class SetBackground(val background: LetterBackground) : LetterEditIntent()
    object Save : LetterEditIntent()
    object RequestBack : LetterEditIntent()
}
```

### 편지 상세 (`LetterDetailViewModel`)

```kotlin
data class LetterDetailState(
    val isLoading: Boolean = true,
    val letter: Letter? = null,
    val error: String? = null
)

sealed class LetterDetailIntent {
    data class LoadLetter(val letterId: String) : LetterDetailIntent()
    object NavigateToEdit : LetterDetailIntent()
    object DeleteLetter : LetterDetailIntent()
    object ConfirmDelete : LetterDetailIntent()
}
```

### 일정 (`ScheduleViewModel`)

```kotlin
data class ScheduleState(
    val isLoading: Boolean = true,
    val selectedDate: LocalDate = LocalDate.today(),
    val currentMonth: YearMonth = YearMonth.now(),
    val schedulesByDate: Map<LocalDate, List<Schedule>> = emptyMap(),
    val viewMode: ScheduleViewMode = ScheduleViewMode.CALENDAR,
    val error: String? = null
)

sealed class ScheduleIntent {
    object LoadSchedules : ScheduleIntent()
    data class SelectDate(val date: LocalDate) : ScheduleIntent()
    data class ChangeMonth(val yearMonth: YearMonth) : ScheduleIntent()
    data class ToggleViewMode(val mode: ScheduleViewMode) : ScheduleIntent()
    object NavigateToAdd : ScheduleIntent()
    data class NavigateToDetail(val scheduleId: String) : ScheduleIntent()
    data class DeleteSchedule(val scheduleId: String) : ScheduleIntent()
}

enum class ScheduleViewMode { CALENDAR, LIST }
```

### 일정 추가/편집 (`ScheduleEditViewModel`)

```kotlin
data class ScheduleEditState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val title: String = "",
    val date: LocalDate = LocalDate.today(),
    val time: LocalTime? = null,
    val location: String = "",
    val category: ScheduleCategory = ScheduleCategory.CHECKUP,
    val memo: String = "",
    val notification: NotificationTiming = NotificationTiming.NONE,
    val isDirty: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

sealed class ScheduleEditIntent {
    data class SetTitle(val value: String) : ScheduleEditIntent()
    data class SetDate(val date: LocalDate) : ScheduleEditIntent()
    data class SetTime(val time: LocalTime?) : ScheduleEditIntent()
    data class SetLocation(val value: String) : ScheduleEditIntent()
    data class SetCategory(val category: ScheduleCategory) : ScheduleEditIntent()
    data class SetMemo(val value: String) : ScheduleEditIntent()
    data class SetNotification(val timing: NotificationTiming) : ScheduleEditIntent()
    object Save : ScheduleEditIntent()
    object RequestBack : ScheduleEditIntent()
}
```

### 더보기 (`MoreViewModel`)

```kotlin
data class MoreState(
    val isLoading: Boolean = true,
    val nickname: String = "",
    val babyName: String = "",
    val currentWeek: Int = 0,
    val avatarUri: String? = null,
    val isDarkMode: Boolean = false,
    val error: String? = null
)

sealed class MoreIntent {
    object LoadProfile : MoreIntent()
    data class ToggleDarkMode(val enabled: Boolean) : MoreIntent()
    object NavigateToProfileEdit : MoreIntent()
    object NavigateToDueDateChange : MoreIntent()
    object NavigateToWeeklyChecklist : MoreIntent()
    object NavigateToPhotoGallery : MoreIntent()
    object NavigateToNotificationSettings : MoreIntent()
    object NavigateToDataBackup : MoreIntent()
    object NavigateToAppInfo : MoreIntent()
    object NavigateToTerms : MoreIntent()
    object NavigateToPrivacy : MoreIntent()
}
```

### 공통 UiEvent (단발성 이벤트)

```kotlin
sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    object NavigateBack : UiEvent()
    data class NavigateTo(val screen: Screen) : UiEvent()
    data class ShowDeleteDialog(val itemType: String) : UiEvent()
    object ShowUnsavedChangesDialog : UiEvent()
    data class RequestPermission(val permission: String) : UiEvent()
    object OpenAppSettings : UiEvent()
}
```

### 에러 상태 처리 원칙

| 에러 종류 | 처리 방법 |
|-----------|-----------|
| 데이터 로드 실패 | `error: String?` → 화면에 에러 UI + 재시도 버튼 |
| 저장 실패 | `UiEvent.ShowSnackbar("오류가 발생했어요. 다시 시도해 주세요")` |
| 입력 유효성 오류 | 해당 필드 TextField 에러 상태 + 에러 메시지 |
| 권한 거부 | `UiEvent.ShowSnackbar(...)` 또는 설정 이동 Dialog |
| 네트워크 없음 | `UiEvent.ShowSnackbar("인터넷 연결을 확인해 주세요")` |
