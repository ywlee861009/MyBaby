package com.mybaby.app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mybaby.app.core.data.BabyRepository
import com.mybaby.app.core.data.HealthRecordRepository
import com.mybaby.app.core.data.LetterRepository
import com.mybaby.app.core.data.ScheduleRepository
import com.mybaby.app.core.model.BabyGender
import com.mybaby.app.feature.home.HomeScreen
import com.mybaby.app.feature.home.HomeViewModel
import com.mybaby.app.feature.schedule.ScheduleScreen
import com.mybaby.app.feature.schedule.ScheduleViewModel
import com.mybaby.app.feature.schedule.ScheduleAddScreen
import com.mybaby.app.feature.schedule.ScheduleAddViewModel
import com.mybaby.app.feature.record.HealthRecordListScreen
import com.mybaby.app.feature.record.HealthRecordListViewModel
import com.mybaby.app.feature.record.HealthRecordAddScreen
import com.mybaby.app.feature.record.HealthRecordAddViewModel
import com.mybaby.app.feature.letter.LetterListScreen
import com.mybaby.app.feature.letter.LetterWriteScreen
import com.mybaby.app.feature.letter.LetterDetailScreen
import com.mybaby.app.feature.letter.LetterEditScreen
import com.mybaby.app.feature.letter.LetterListViewModel
import com.mybaby.app.feature.letter.LetterWriteViewModel
import com.mybaby.app.feature.letter.LetterDetailViewModel
import com.mybaby.app.feature.letter.LetterEditViewModel
import com.mybaby.app.feature.more.MoreScreen
import com.mybaby.app.feature.more.MoreViewModel
import com.mybaby.app.feature.setup.SetupBabyInfoScreen
import com.mybaby.app.feature.setup.SetupBabyInfoViewModel
import com.mybaby.app.feature.setup.SetupPregnancyInfoScreen
import com.mybaby.app.feature.setup.SetupPregnancyInfoViewModel
import com.mybaby.app.ui.theme.PumTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

private const val ANIM_DURATION = 280

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val screen: Screen,
)

val bottomNavItems = listOf(
    BottomNavItem("홈", Icons.Rounded.Home, Screen.Home),
    BottomNavItem("기록", Icons.Rounded.FavoriteBorder, Screen.HealthRecord),
    BottomNavItem("편지", Icons.Rounded.Edit, Screen.Letter.List),
    BottomNavItem("일정", Icons.Rounded.DateRange, Screen.Schedule),
    BottomNavItem("더보기", Icons.Rounded.Menu, Screen.More),
)

/** 최상위 탭 인덱스 반환. 탭이 아닌 화면(sub-screen)은 null */
private fun NavBackStackEntry.tabIndex(): Int? = when {
    destination.hasRoute(Screen.Home::class) -> 0
    destination.hasRoute(Screen.HealthRecord::class) -> 1
    destination.hasRoute(Screen.Letter.List::class) -> 2
    destination.hasRoute(Screen.Schedule::class) -> 3
    destination.hasRoute(Screen.More::class) -> 4
    else -> null
}

private fun NavDestination?.isLetterTab(): Boolean =
    this?.hasRoute(Screen.Letter.List::class) == true ||
            this?.hasRoute(Screen.Letter.Detail::class) == true

private fun NavDestination?.isSetupScreen(): Boolean =
    this?.hasRoute(Screen.Setup.BabyInfo::class) == true ||
            this?.hasRoute(Screen.Setup.PregnancyInfo::class) == true

@Composable
fun AppNavigation(
    startDestination: Screen = Screen.Home,
    babyRepository: BabyRepository,
    letterRepository: LetterRepository,
    healthRecordRepository: HealthRecordRepository,
    scheduleRepository: ScheduleRepository,
    onExit: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val showBottomBar = currentDestination?.hasRoute(Screen.Letter.Write::class) != true &&
            currentDestination?.hasRoute(Screen.Letter.Edit::class) != true &&
            currentDestination?.hasRoute(Screen.HealthRecordAdd::class) != true &&
            currentDestination?.hasRoute(Screen.ScheduleAdd::class) != true &&
            !currentDestination.isSetupScreen()

    // 최상위 탭에 있을 때만 백프레스 인터셉트
    val isOnTopLevelTab = currentDestination?.let {
        it.hasRoute(Screen.Home::class) ||
                it.hasRoute(Screen.HealthRecord::class) ||
                it.hasRoute(Screen.Letter.List::class) ||
                it.hasRoute(Screen.Schedule::class) ||
                it.hasRoute(Screen.More::class)
    } == true

    ExitOnDoubleBackPress(
        enabled = isOnTopLevelTab,
        onShowWarning = {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("한 번 더 누르면 종료됩니다")
            }
        },
        onExit = onExit
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF2D2020),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                PumBottomNavBar(
                    items = bottomNavItems,
                    currentDestination = currentDestination,
                    onItemClick = { item ->
                        navController.navigate(item.screen) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .padding(innerPadding),
            enterTransition = {
                val from = initialState.tabIndex()
                val to = targetState.tabIndex()
                when {
                    from != null && to != null ->
                        if (to > from) slideInHorizontally(tween(ANIM_DURATION)) { it }
                        else slideInHorizontally(tween(ANIM_DURATION)) { -it }
                    else ->
                        slideInHorizontally(tween(ANIM_DURATION)) { it } + fadeIn(tween(ANIM_DURATION))
                }
            },
            exitTransition = {
                val from = initialState.tabIndex()
                val to = targetState.tabIndex()
                when {
                    from != null && to != null ->
                        if (to > from) slideOutHorizontally(tween(ANIM_DURATION)) { -it }
                        else slideOutHorizontally(tween(ANIM_DURATION)) { it }
                    else ->
                        slideOutHorizontally(tween(ANIM_DURATION)) { -it } + fadeOut(tween(ANIM_DURATION))
                }
            },
            popEnterTransition = {
                slideInHorizontally(tween(ANIM_DURATION)) { -it } + fadeIn(tween(ANIM_DURATION))
            },
            popExitTransition = {
                slideOutHorizontally(tween(ANIM_DURATION)) { it } + fadeOut(tween(ANIM_DURATION))
            }
        ) {
            // ── 온보딩 화면 ──────────────────────────────────
            composable<Screen.Setup.BabyInfo> {
                val vm: SetupBabyInfoViewModel = viewModel { SetupBabyInfoViewModel() }
                SetupBabyInfoScreen(
                    viewModel = vm,
                    onNavigateNext = { nickname, gender, isBorn ->
                        navController.navigate(
                            Screen.Setup.PregnancyInfo(
                                nickname = nickname,
                                gender = gender.name,
                                isBorn = isBorn
                            )
                        )
                    }
                )
            }
            composable<Screen.Setup.PregnancyInfo> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.Setup.PregnancyInfo>()
                val vm: SetupPregnancyInfoViewModel = viewModel {
                    SetupPregnancyInfoViewModel(
                        babyRepository = babyRepository,
                        nickname = route.nickname,
                        gender = BabyGender.valueOf(route.gender),
                        isBorn = route.isBorn
                    )
                }
                SetupPregnancyInfoScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() },
                    onSetupComplete = {
                        navController.navigate(Screen.Home) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // ── 메인 화면 ─────────────────────────────────────
            composable<Screen.Home> {
                val homeViewModel: HomeViewModel = viewModel { HomeViewModel(babyRepository) }
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToRecord = { navController.navigate(Screen.HealthRecord) },
                    onNavigateToLetterWrite = { navController.navigate(Screen.Letter.Write) },
                    onNavigateToScheduleAdd = {
                        navController.navigate(Screen.Schedule) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToMore = {
                        navController.navigate(Screen.More) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSchedule = {
                        navController.navigate(Screen.Schedule) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable<Screen.HealthRecord> {
                val vm: HealthRecordListViewModel = viewModel { HealthRecordListViewModel(healthRecordRepository) }
                HealthRecordListScreen(
                    viewModel = vm,
                    onNavigateToAdd = { navController.navigate(Screen.HealthRecordAdd()) }
                )
            }
            composable<Screen.HealthRecordAdd> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.HealthRecordAdd>()
                val vm: HealthRecordAddViewModel = viewModel {
                    HealthRecordAddViewModel(
                        repository = healthRecordRepository,
                        babyRepository = babyRepository,
                        initialCategory = route.category
                    )
                }
                HealthRecordAddScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable<Screen.Letter.List> {
                val vm: LetterListViewModel = viewModel { LetterListViewModel(letterRepository) }
                LetterListScreen(
                    viewModel = vm,
                    onNavigateToWrite = { navController.navigate(Screen.Letter.Write) },
                    onNavigateToDetail = { id -> navController.navigate(Screen.Letter.Detail(id)) }
                )
            }
            composable<Screen.Letter.Write> {
                val vm: LetterWriteViewModel = viewModel { LetterWriteViewModel(letterRepository, babyRepository) }
                LetterWriteScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable<Screen.Letter.Detail> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.Letter.Detail>()
                val vm: LetterDetailViewModel = viewModel(key = route.id) {
                    LetterDetailViewModel(letterRepository, babyRepository, route.id)
                }
                LetterDetailScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.Letter.Edit(id))
                    }
                )
            }
            composable<Screen.Letter.Edit> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.Letter.Edit>()
                val vm: LetterEditViewModel = viewModel(key = route.id) {
                    LetterEditViewModel(letterRepository, babyRepository, route.id)
                }
                LetterEditScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable<Screen.Schedule> {
                val vm: ScheduleViewModel = viewModel { ScheduleViewModel(scheduleRepository) }
                ScheduleScreen(
                    viewModel = vm,
                    onNavigateToAdd = { date ->
                        navController.navigate(Screen.ScheduleAdd(date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()))
                    }
                )
            }
            composable<Screen.ScheduleAdd> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.ScheduleAdd>()
                val vm: ScheduleAddViewModel = viewModel {
                    ScheduleAddViewModel(
                        repository = scheduleRepository,
                        initialDate = if (route.dateMillis > 0L) {
                            Instant.fromEpochMilliseconds(route.dateMillis)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date
                        } else null
                    )
                }
                ScheduleAddScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable<Screen.More> {
                val vm: MoreViewModel = viewModel { MoreViewModel(babyRepository) }
                MoreScreen(viewModel = vm)
            }
        }
    }
}

@Composable
private fun PumBottomNavBar(
    items: List<BottomNavItem>,
    currentDestination: NavDestination?,
    onItemClick: (BottomNavItem) -> Unit
) {
    val colors = PumTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .navigationBarsPadding()
            .padding(start = 21.dp, end = 21.dp, top = 12.dp, bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(colors.surface)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = when (item.screen) {
                    is Screen.Letter.List -> currentDestination.isLetterTab()
                    else -> currentDestination?.hasRoute(item.screen::class) == true
                }

                PumNavItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PumNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = PumTheme.colors
    val contentColor = if (isSelected) colors.onPrimary else colors.onSurfaceSubtle

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(32.dp))
            .background(if (isSelected) colors.primary else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.label,
                color = contentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
