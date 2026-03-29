# 산모수첩 데이터 모델 명세

## 목차

1. [엔티티 정의](#1-엔티티-정의)
2. [SQLDelight DB 스키마](#2-sqldelight-db-스키마)
3. [Enum 타입](#3-enum-타입)
4. [Repository 인터페이스](#4-repository-인터페이스)
5. [비즈니스 로직 계산](#5-비즈니스-로직-계산)

---

## 1. 엔티티 정의

### Baby (아기/사용자 프로필)

```kotlin
data class Baby(
    val id: String,                    // UUID
    val nickname: String,              // 엄마 닉네임
    val babyName: String,              // 태명
    val gender: BabyGender,            // 성별
    val dueDate: LocalDate?,           // 출산 예정일 (미출생 시)
    val birthDate: LocalDate?,         // 생일 (출생 후)
    val avatarUri: String?,            // 프로필 이미지 로컬 경로
    val createdAt: Long                // epoch millis
)
```

**규칙:**
- `dueDate`와 `birthDate` 중 하나만 non-null
- 앱 전체에서 Baby는 1개만 존재 (단일 프로필)

---

### HealthRecord (건강 기록)

```kotlin
data class HealthRecord(
    val id: String,                    // UUID
    val category: RecordCategory,      // 카테고리
    val date: LocalDate,               // 기록 날짜
    val weekNumber: Int,               // 임신 주차 (자동 계산)
    val weightKg: Double?,             // 체중 (체중 카테고리)
    val systolicBp: Int?,              // 수축기 혈압 (혈압 카테고리)
    val diastolicBp: Int?,             // 이완기 혈압 (혈압 카테고리)
    val kickCount: Int?,               // 태동 횟수 (태동 카테고리)
    val kickTimeMinutes: Int?,         // 태동 측정 시간 (분, 태동 카테고리)
    val photoUri: String?,             // 초음파 사진 로컬 경로 (사진 카테고리)
    val memoTitle: String?,            // 메모 제목 (메모 카테고리)
    val memoContent: String?,          // 메모 내용 (메모/기타 보조 메모)
    val createdAt: Long,               // epoch millis
    val updatedAt: Long                // epoch millis
)
```

**정상 범위 기준 (건강 수치 상태 계산용):**

| 카테고리 | 정상 | 주의 | 위험 |
|----------|------|------|------|
| 체중 증가/주 | 0.3~0.5kg | 0.5~0.8kg | 0.8kg 초과 또는 감소 |
| 수축기 혈압 | < 120 mmHg | 120~139 | ≥ 140 |
| 이완기 혈압 | < 80 mmHg | 80~89 | ≥ 90 |
| 태동 (2시간 기준) | ≥ 10회 | 6~9회 | < 6회 |

---

### Letter (편지)

```kotlin
data class Letter(
    val id: String,                    // UUID
    val content: String,               // 편지 본문
    val background: LetterBackground,  // 편지지 배경 테마
    val weekNumber: Int,               // 작성 시점 임신 주차
    val date: LocalDate,               // 작성 날짜
    val createdAt: Long,               // epoch millis
    val updatedAt: Long                // epoch millis
)
```

---

### Schedule (일정)

```kotlin
data class Schedule(
    val id: String,                    // UUID
    val title: String,                 // 일정 제목
    val date: LocalDate,               // 날짜
    val time: LocalTime?,              // 시간 (선택)
    val location: String?,             // 장소 (선택)
    val category: ScheduleCategory,    // 카테고리
    val memo: String?,                 // 메모 (선택)
    val notification: NotificationTiming, // 알림 설정
    val isCompleted: Boolean = false,  // 완료 여부
    val createdAt: Long,               // epoch millis
    val updatedAt: Long                // epoch millis
)
```

---

### ChecklistItem (주차별 체크리스트)

```kotlin
data class ChecklistItem(
    val id: String,                    // UUID
    val weekNumber: Int,               // 해당 주차
    val title: String,                 // 항목 제목
    val description: String?,          // 부가 설명 (선택)
    val isCompleted: Boolean,          // 완료 여부
    val isDefault: Boolean = true,     // true: 기본 제공, false: 사용자 추가
    val createdAt: Long
)
```

**기본 체크리스트 항목 예시 (일부):**

| 주차 | 항목 |
|------|------|
| 4~8주 | 산부인과 첫 방문, 엽산 복용 시작 |
| 9~12주 | 1차 기형아 검사, 임신 확인서 발급 |
| 13~16주 | 쿼드 검사, 임산부 등록 |
| 17~20주 | 정밀 초음파, 태아 성별 확인 |
| 21~24주 | 임신성 당뇨 검사, 철분제 복용 |
| 25~28주 | 빈혈 검사, 분만 병원 예약 |
| 29~32주 | 3차 정밀 초음파, 분만 준비 시작 |
| 33~36주 | 태아 심박 모니터링, 입원 가방 준비 |
| 37~40주 | 주 1회 산전 진찰, 분만 신호 숙지 |

---

## 2. SQLDelight DB 스키마

**DB 이름:** `PumDatabase`
**파일 위치:** `core/database/src/commonMain/sqldelight/com/mybaby/app/`

### Baby.sq

```sql
CREATE TABLE Baby (
    id TEXT NOT NULL PRIMARY KEY,
    nickname TEXT NOT NULL,
    babyName TEXT NOT NULL,
    gender TEXT NOT NULL,         -- BabyGender enum name
    dueDate TEXT,                 -- ISO-8601 date string (YYYY-MM-DD), nullable
    birthDate TEXT,               -- ISO-8601 date string, nullable
    avatarUri TEXT,
    createdAt INTEGER NOT NULL
);

selectFirst:
SELECT * FROM Baby LIMIT 1;

insert:
INSERT OR REPLACE INTO Baby VALUES (?, ?, ?, ?, ?, ?, ?, ?);

update:
UPDATE Baby SET
    nickname = ?,
    babyName = ?,
    gender = ?,
    dueDate = ?,
    birthDate = ?,
    avatarUri = ?
WHERE id = ?;

deleteAll:
DELETE FROM Baby;
```

### HealthRecord.sq

```sql
CREATE TABLE HealthRecord (
    id TEXT NOT NULL PRIMARY KEY,
    category TEXT NOT NULL,       -- RecordCategory enum name
    date TEXT NOT NULL,           -- YYYY-MM-DD
    weekNumber INTEGER NOT NULL,
    weightKg REAL,
    systolicBp INTEGER,
    diastolicBp INTEGER,
    kickCount INTEGER,
    kickTimeMinutes INTEGER,
    photoUri TEXT,
    memoTitle TEXT,
    memoContent TEXT,
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL
);

selectAll:
SELECT * FROM HealthRecord ORDER BY date DESC, createdAt DESC;

selectByCategory:
SELECT * FROM HealthRecord WHERE category = ? ORDER BY date DESC;

selectById:
SELECT * FROM HealthRecord WHERE id = ? LIMIT 1;

selectRecentN:
SELECT * FROM HealthRecord ORDER BY date DESC LIMIT :n;

insert:
INSERT INTO HealthRecord VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

update:
UPDATE HealthRecord SET
    category = ?,
    date = ?,
    weekNumber = ?,
    weightKg = ?,
    systolicBp = ?,
    diastolicBp = ?,
    kickCount = ?,
    kickTimeMinutes = ?,
    photoUri = ?,
    memoTitle = ?,
    memoContent = ?,
    updatedAt = ?
WHERE id = ?;

deleteById:
DELETE FROM HealthRecord WHERE id = ?;
```

### Letter.sq

```sql
CREATE TABLE Letter (
    id TEXT NOT NULL PRIMARY KEY,
    content TEXT NOT NULL,
    background TEXT NOT NULL,     -- LetterBackground enum name
    weekNumber INTEGER NOT NULL,
    date TEXT NOT NULL,           -- YYYY-MM-DD
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL
);

selectAll:
SELECT * FROM Letter ORDER BY date DESC, createdAt DESC;

selectById:
SELECT * FROM Letter WHERE id = ? LIMIT 1;

countAll:
SELECT COUNT(*) FROM Letter;

insert:
INSERT INTO Letter VALUES (?, ?, ?, ?, ?, ?, ?);

update:
UPDATE Letter SET
    content = ?,
    background = ?,
    updatedAt = ?
WHERE id = ?;

deleteById:
DELETE FROM Letter WHERE id = ?;
```

### Schedule.sq

```sql
CREATE TABLE Schedule (
    id TEXT NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    date TEXT NOT NULL,           -- YYYY-MM-DD
    time TEXT,                    -- HH:mm, nullable
    location TEXT,
    category TEXT NOT NULL,       -- ScheduleCategory enum name
    memo TEXT,
    notification TEXT NOT NULL,   -- NotificationTiming enum name
    isCompleted INTEGER NOT NULL DEFAULT 0,  -- Boolean (0/1)
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL
);

selectAll:
SELECT * FROM Schedule ORDER BY date ASC, time ASC;

selectByDate:
SELECT * FROM Schedule WHERE date = ? ORDER BY time ASC;

selectById:
SELECT * FROM Schedule WHERE id = ? LIMIT 1;

selectUpcoming:
SELECT * FROM Schedule WHERE date >= :today ORDER BY date ASC LIMIT :n;

selectByDateRange:
SELECT * FROM Schedule WHERE date >= :start AND date <= :end ORDER BY date ASC;

insert:
INSERT INTO Schedule VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);

update:
UPDATE Schedule SET
    title = ?,
    date = ?,
    time = ?,
    location = ?,
    category = ?,
    memo = ?,
    notification = ?,
    isCompleted = ?,
    updatedAt = ?
WHERE id = ?;

deleteById:
DELETE FROM Schedule WHERE id = ?;
```

### ChecklistItem.sq

```sql
CREATE TABLE ChecklistItem (
    id TEXT NOT NULL PRIMARY KEY,
    weekNumber INTEGER NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    isCompleted INTEGER NOT NULL DEFAULT 0,
    isDefault INTEGER NOT NULL DEFAULT 1,
    createdAt INTEGER NOT NULL
);

selectByWeek:
SELECT * FROM ChecklistItem WHERE weekNumber = ? ORDER BY isDefault DESC, createdAt ASC;

selectCurrentWeekRange:
SELECT * FROM ChecklistItem WHERE weekNumber >= :start AND weekNumber <= :end;

updateCompleted:
UPDATE ChecklistItem SET isCompleted = ? WHERE id = ?;

insert:
INSERT INTO ChecklistItem VALUES (?, ?, ?, ?, ?, ?, ?);

deleteById:
DELETE FROM ChecklistItem WHERE id = ?;
```

---

## 3. Enum 타입

```kotlin
// core/model/src/commonMain/kotlin/com/mybaby/app/model/

enum class BabyGender {
    MALE, FEMALE, UNKNOWN
}

enum class RecordCategory {
    ALL,      // 필터 전용 (DB 저장 안 함)
    WEIGHT,
    BLOOD_PRESSURE,
    KICK,
    PHOTO,
    MEMO
}

enum class LetterBackground(val colorHex: String) {
    DEFAULT("#FFF8F0"),
    BLUE("#F0F4FF"),
    GREEN("#F5FFF0"),
    PINK("#FFF5F8"),
    YELLOW("#FFFFF0")
}

enum class ScheduleCategory {
    CHECKUP,        // 정기검진
    ULTRASOUND,     // 초음파
    BLOOD_TEST,     // 혈액검사
    VACCINATION,    // 예방접종
    OTHER           // 기타
}

enum class NotificationTiming {
    NONE,       // 없음
    SAME_DAY,   // 당일 오전 9시
    ONE_DAY,    // 1일 전 오전 9시
    THREE_DAYS, // 3일 전 오전 9시
    ONE_WEEK    // 1주 전 오전 9시
}

enum class RecordHealthStatus {
    NORMAL,  // 정상 (Success 색상)
    WARNING, // 주의 (Warning 색상)
    DANGER   // 위험 (Error 색상)
}
```

---

## 4. Repository 인터페이스

모두 `core/data/src/commonMain/kotlin/com/mybaby/app/data/repository/` 위치.

### BabyRepository

```kotlin
interface BabyRepository {
    fun getBaby(): Flow<Baby?>
    suspend fun saveBaby(baby: Baby)
    suspend fun updateBaby(baby: Baby)
    suspend fun deleteBaby()
}
```

### HealthRecordRepository

```kotlin
interface HealthRecordRepository {
    fun getAllRecords(): Flow<List<HealthRecord>>
    fun getRecordsByCategory(category: RecordCategory): Flow<List<HealthRecord>>
    fun getRecentRecords(n: Int): Flow<List<HealthRecord>>
    suspend fun getRecordById(id: String): HealthRecord?
    suspend fun insertRecord(record: HealthRecord)
    suspend fun updateRecord(record: HealthRecord)
    suspend fun deleteRecord(id: String)
}
```

### LetterRepository

```kotlin
interface LetterRepository {
    fun getAllLetters(): Flow<List<Letter>>
    fun getLetterCount(): Flow<Int>
    suspend fun getLetterById(id: String): Letter?
    suspend fun insertLetter(letter: Letter)
    suspend fun updateLetter(letter: Letter)
    suspend fun deleteLetter(id: String)
}
```

### ScheduleRepository

```kotlin
interface ScheduleRepository {
    fun getAllSchedules(): Flow<List<Schedule>>
    fun getSchedulesByDate(date: LocalDate): Flow<List<Schedule>>
    fun getSchedulesByDateRange(start: LocalDate, end: LocalDate): Flow<List<Schedule>>
    fun getUpcomingSchedules(today: LocalDate, limit: Int): Flow<List<Schedule>>
    suspend fun getScheduleById(id: String): Schedule?
    suspend fun insertSchedule(schedule: Schedule)
    suspend fun updateSchedule(schedule: Schedule)
    suspend fun deleteSchedule(id: String)
}
```

### ChecklistRepository

```kotlin
interface ChecklistRepository {
    fun getChecklistByWeek(weekNumber: Int): Flow<List<ChecklistItem>>
    suspend fun toggleItem(id: String, isCompleted: Boolean)
    suspend fun insertItem(item: ChecklistItem)
    suspend fun deleteItem(id: String)
    suspend fun seedDefaultChecklist()  // 기본 체크리스트 초기 데이터 삽입
}
```

---

## 5. 비즈니스 로직 계산

### 임신 주차 계산

```kotlin
// dueDate(출산 예정일) 기준
fun calculateCurrentWeek(dueDate: LocalDate): Pair<Int, Int> {
    val today = LocalDate.today()
    val totalDays = 280 - today.daysUntil(dueDate)  // 40주 = 280일
    val week = (totalDays / 7).coerceIn(0, 42)
    val day = (totalDays % 7).coerceIn(0, 6)
    return Pair(week, day)
}

// birthDate(생일) 기준
fun calculateAgeWeeks(birthDate: LocalDate): Pair<Int, Int> {
    val totalDays = birthDate.daysUntil(LocalDate.today())
    return Pair(totalDays / 7, totalDays % 7)
}
```

### D-Day 계산

```kotlin
fun calculateDDay(dueDate: LocalDate): Int {
    return LocalDate.today().daysUntil(dueDate)
}
```

### 건강 수치 상태 계산

```kotlin
fun getBloodPressureStatus(systolic: Int, diastolic: Int): RecordHealthStatus {
    return when {
        systolic >= 140 || diastolic >= 90 -> RecordHealthStatus.DANGER
        systolic >= 120 || diastolic >= 80 -> RecordHealthStatus.WARNING
        else -> RecordHealthStatus.NORMAL
    }
}

fun getWeightChangeStatus(changeKg: Double): RecordHealthStatus {
    return when {
        changeKg > 0.8 || changeKg < 0 -> RecordHealthStatus.DANGER
        changeKg > 0.5 -> RecordHealthStatus.WARNING
        else -> RecordHealthStatus.NORMAL
    }
}

fun getKickStatus(count: Int): RecordHealthStatus {
    return when {
        count < 6 -> RecordHealthStatus.DANGER
        count < 10 -> RecordHealthStatus.WARNING
        else -> RecordHealthStatus.NORMAL
    }
}
```

### UUID 생성

```kotlin
// 플랫폼별 expect/actual
expect fun generateUuid(): String

// Android actual
actual fun generateUuid(): String = java.util.UUID.randomUUID().toString()

// iOS actual
actual fun generateUuid(): String = platform.Foundation.NSUUID().UUIDString()
```

### 데이터 초기화 순서 (앱 최초 실행)

```
1. DB 생성 (PumDatabase)
2. ChecklistRepository.seedDefaultChecklist() 실행
3. Baby 데이터 확인
   - 없음 → Onboarding(BabyInfo)
   - 있음 → Home
```
