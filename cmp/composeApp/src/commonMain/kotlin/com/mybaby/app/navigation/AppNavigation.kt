package com.mybaby.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val screen: Screen,
)

val bottomNavItems = listOf(
    BottomNavItem("홈", Icons.Rounded.Home, Screen.Home),
    BottomNavItem("기록", Icons.Rounded.FavoriteBorder, Screen.HealthRecord),
    BottomNavItem("편지", Icons.Rounded.Edit, Screen.Letter),
    BottomNavItem("일정", Icons.Rounded.DateRange, Screen.Schedule),
    BottomNavItem("더보기", Icons.Rounded.Menu, Screen.More),
)

import androidx.lifecycle.viewmodel.compose.viewModel
import com.mybaby.app.feature.home.HomeScreen
import com.mybaby.app.feature.home.HomeViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hasRoute(item.screen::class) == true,
                        onClick = {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Home> {
                val homeViewModel: HomeViewModel = viewModel { HomeViewModel() }
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToRecord = {
                        navController.navigate(Screen.HealthRecord)
                    }
                )
            }
            composable<Screen.HealthRecord> {
                PlaceholderScreen("건강 기록")
            }
            composable<Screen.Letter> {
                PlaceholderScreen("태아에게 보내는 편지")
            }
            composable<Screen.Schedule> {
                PlaceholderScreen("진료 일정")
            }
            composable<Screen.More> {
                PlaceholderScreen("더보기")
            }
        }
    }
}
