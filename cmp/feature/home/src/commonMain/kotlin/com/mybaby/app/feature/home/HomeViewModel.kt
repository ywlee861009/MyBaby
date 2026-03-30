package com.mybaby.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybaby.app.core.data.BabyRepository
import com.mybaby.app.core.data.ChecklistRepository
import com.mybaby.app.core.data.HealthRecordRepository
import com.mybaby.app.core.data.ScheduleRepository
import com.mybaby.app.core.model.ChecklistItem as DomainChecklistItem
import com.mybaby.app.ui.components.ChecklistItem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HomeViewModel(
    private val babyRepository: BabyRepository,
    private val checklistRepository: ChecklistRepository,
    private val scheduleRepository: ScheduleRepository,
    private val healthRecordRepository: HealthRecordRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _events = Channel<HomeUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        handleIntent(HomeIntent.LoadData)
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadData, HomeIntent.Refresh -> loadData()
            is HomeIntent.ToggleChecklistItem -> toggleChecklistItem(intent.itemId)
            HomeIntent.OnWeightRecordClick -> sendEvent(HomeUiEvent.NavigateToHealthRecord)
            HomeIntent.OnLetterWriteClick -> sendEvent(HomeUiEvent.NavigateToLetterWrite)
            HomeIntent.OnScheduleAddClick -> sendEvent(HomeUiEvent.NavigateToScheduleAdd)
            HomeIntent.OnMoreChecklistClick -> sendEvent(HomeUiEvent.NavigateToMore)
            HomeIntent.OnMoreScheduleClick -> sendEvent(HomeUiEvent.NavigateToSchedule)
            HomeIntent.OnMoreRecordClick -> sendEvent(HomeUiEvent.NavigateToHealthRecord)
        }
    }

    private fun sendEvent(event: HomeUiEvent) {
        viewModelScope.launch { _events.send(event) }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val baby = babyRepository.getBaby().first()
                val nowMillis = Clock.System.now().toEpochMilliseconds()
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                val todayLabel = "${today.monthNumber}월 ${today.dayOfMonth}일 ${today.dayOfWeek.korLabel()}"

                val msPerDay = 24L * 3600L * 1000L
                val msPerWeek = 7L * msPerDay
                val dueDate = baby?.dueDate
                val (currentWeek, currentDay, dDay) = if (dueDate != null) {
                    val pregnancyStartMillis = dueDate - 280 * msPerDay
                    val elapsedMillis = nowMillis - pregnancyStartMillis
                    val week = (elapsedMillis / msPerWeek).toInt().coerceIn(0, 40)
                    val day = ((elapsedMillis % msPerWeek) / msPerDay).toInt().coerceIn(0, 6)
                    val daysRemaining = ((dueDate - nowMillis) / msPerDay).toInt().coerceAtLeast(0)
                    Triple(week, day, daysRemaining)
                } else {
                    Triple(0, 0, 0)
                }

                // 체크리스트: DB에서 현재 주차 데이터 로드, 없으면 기본값 생성
                val checklistItems = loadOrSeedChecklist(currentWeek)

                // 일정: DB에서 오늘 이후 가장 가까운 일정 2개 로드
                val allSchedules = scheduleRepository.getAllSchedules().first()
                val upcomingSchedules = allSchedules
                    .filter { it.dateMillis >= nowMillis - msPerDay }
                    .sortedBy { it.dateMillis }
                    .take(2)

                // 건강기록: DB에서 최근 기록 2개 로드
                val allRecords = healthRecordRepository.getAllRecords().first()
                val recentRecords = allRecords.take(2)

                _state.update {
                    it.copy(
                        isLoading = false,
                        nickname = baby?.nickname ?: "엄마",
                        todayLabel = todayLabel,
                        currentWeek = currentWeek,
                        currentDay = currentDay,
                        dDay = dDay,
                        babySizeDescription = getBabySizeDescription(currentWeek),
                        weeklyChecklist = checklistItems,
                        upcomingSchedules = upcomingSchedules,
                        recentRecords = recentRecords
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private suspend fun loadOrSeedChecklist(weekNumber: Int): List<ChecklistItem> {
        val existing = checklistRepository.getItemsByWeek(weekNumber).first()
        if (existing.isNotEmpty()) {
            return existing.map { it.toUiModel() }
        }

        // 현재 주차에 체크리스트가 없으면 기본 항목 생성
        val now = Clock.System.now().toEpochMilliseconds()
        val defaults = getDefaultChecklistItems(weekNumber)
        defaults.forEachIndexed { index, text ->
            val item = DomainChecklistItem(
                id = "cl_${weekNumber}_$index",
                text = text,
                isChecked = false,
                weekNumber = weekNumber,
                createdAt = now + index
            )
            checklistRepository.saveItem(item)
        }
        return checklistRepository.getItemsByWeek(weekNumber).first().map { it.toUiModel() }
    }

    private fun getDefaultChecklistItems(week: Int): List<String> {
        val base = mutableListOf("엽산 복용하기")
        when (week) {
            in 0..12 -> {
                base.add("산부인과 초진 예약하기")
                base.add("임신 확인서 발급받기")
            }
            in 13..16 -> {
                base.add("철분제 복용 시작하기")
                base.add("기형아 검사 일정 확인하기")
            }
            in 17..20 -> {
                base.add("철분제 복용하기")
                base.add("정밀 초음파 예약 확인하기")
            }
            in 21..24 -> {
                base.add("철분제 복용하기")
                base.add("임신성 당뇨 검사 준비하기")
            }
            in 25..28 -> {
                base.add("철분제 복용하기")
                base.add("백일해 예방접종 확인하기")
            }
            in 29..32 -> {
                base.add("철분제 복용하기")
                base.add("출산 가방 준비하기")
            }
            in 33..36 -> {
                base.add("철분제 복용하기")
                base.add("분만 방법 상담하기")
            }
            in 37..40 -> {
                base.add("태동 횟수 확인하기")
                base.add("입원 준비물 최종 점검하기")
            }
            else -> {
                base.add("철분제 복용하기")
                base.add("산부인과 예약 확인하기")
            }
        }
        return base
    }

    private fun toggleChecklistItem(itemId: String) {
        viewModelScope.launch {
            val currentItem = _state.value.weeklyChecklist.find { it.id == itemId } ?: return@launch
            val newChecked = !currentItem.isChecked

            // DB 업데이트
            checklistRepository.updateChecked(itemId, newChecked)

            // UI 상태 업데이트
            _state.update { state ->
                state.copy(
                    weeklyChecklist = state.weeklyChecklist.map { item ->
                        if (item.id == itemId) item.copy(isChecked = newChecked) else item
                    }
                )
            }
        }
    }

    private fun DomainChecklistItem.toUiModel() = ChecklistItem(
        id = id,
        text = text,
        isChecked = isChecked
    )

    private fun getBabySizeDescription(week: Int): String = when (week) {
        in 1..4 -> "양귀비 씨앗"
        in 5..8 -> "블루베리 (1.5cm)"
        in 9..12 -> "자두 (5cm)"
        in 13..16 -> "복숭아 (10cm)"
        in 17..20 -> "바나나 (15cm)"
        in 21..24 -> "옥수수 (30cm)"
        in 25..28 -> "오이 (35cm)"
        in 29..32 -> "가지 (40cm)"
        in 33..36 -> "파인애플 (45cm)"
        in 37..40 -> "수박 (50cm)"
        else -> "성장 중"
    }
}

private fun DayOfWeek.korLabel(): String = when (this) {
    DayOfWeek.MONDAY -> "월요일"
    DayOfWeek.TUESDAY -> "화요일"
    DayOfWeek.WEDNESDAY -> "수요일"
    DayOfWeek.THURSDAY -> "목요일"
    DayOfWeek.FRIDAY -> "금요일"
    DayOfWeek.SATURDAY -> "토요일"
    DayOfWeek.SUNDAY -> "일요일"
    else -> ""
}
