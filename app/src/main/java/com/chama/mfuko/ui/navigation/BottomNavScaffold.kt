package com.chama.mfuko.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.chama.mfuko.ui.features.activity.ActivityScreen
import com.chama.mfuko.ui.features.home.HomeScreen
import com.chama.mfuko.ui.features.nests.members.MembersScreen
import com.chama.mfuko.ui.features.notifications.NotificationsScreen
import com.chama.mfuko.ui.features.profile.ProfileDrawerContent
import com.chama.mfuko.ui.features.profile.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private data class BottomTab(
    val screen: Screen,
    val label: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
)

private val baseTabs = listOf(
    BottomTab(Screen.HomeTab, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomTab(Screen.ActivityTab, "Activity", Icons.AutoMirrored.Filled.ListAlt, Icons.AutoMirrored.Outlined.ListAlt),
    BottomTab(Screen.NotificationsTab, "Alerts", Icons.Filled.Notifications, Icons.Outlined.Notifications)
)

private val membersTab =
    BottomTab(Screen.MembersTab, "Members", Icons.Filled.Group, Icons.Outlined.Group)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavScaffold(
    onNavigateToLogin: () -> Unit,
    onNavigateToManageLoans: (Long) -> Unit,
    onNavigateToSwitchNest: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToNestSettings: (Long) -> Unit
) {
    val innerNavController = rememberNavController()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val profileState = profileViewModel.state.value
    val userIsManager = profileState.userRole.equals("manager", ignoreCase = true)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        profileViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ProfileViewModel.UiEvent.NavigateToLogin -> {
                    scope.launch { drawerState.close() }
                    onNavigateToLogin()
                }
                is ProfileViewModel.UiEvent.NavigateToSwitchNest -> {
                    scope.launch { drawerState.close() }
                    onNavigateToSwitchNest()
                }
            }
        }
    }

    val tabs = if (userIsManager) baseTabs + membersTab else baseTabs

    val backStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentTitle = tabs.find { it.screen.route == currentRoute }?.label ?: "My Nest"

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            ProfileDrawerContent(
                viewModel      = profileViewModel,
                onSwitchNest   = profileViewModel::onSwitchNestClick,
                onSettings     = {
                    scope.launch { drawerState.close() }
                    onNavigateToSettings()
                },
                onLogout       = profileViewModel::onLogoutClick
            )
        }
    ) {
        Scaffold(
            // Unified TopAppBar treatment (DESIGN_SYSTEM.md §6.5) — surface
            // background everywhere, no per-screen exceptions.
            topBar = {
                TopAppBar(
                    title = { Text(currentTitle, style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                // Pill indicator on the selected tab (DESIGN_SYSTEM.md §6.5).
                NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
                    tabs.forEach { tab ->
                        val selected = currentRoute == tab.screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                innerNavController.navigate(tab.screen.route) {
                                    popUpTo(innerNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector        = if (selected) tab.filledIcon else tab.outlinedIcon,
                                    contentDescription = tab.label
                                )
                            },
                            label = { Text(tab.label, style = MaterialTheme.typography.labelMedium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController    = innerNavController,
                startDestination = Screen.HomeTab.route,
                modifier         = Modifier.padding(paddingValues)
            ) {
                composable(Screen.HomeTab.route) {
                    HomeScreen(onNavigateToManageLoans = onNavigateToManageLoans)
                }
                composable(Screen.ActivityTab.route) { ActivityScreen() }
                composable(Screen.NotificationsTab.route) { NotificationsScreen() }
                composable(Screen.MembersTab.route) {
                    MembersScreen(onNavigateToNestSettings = onNavigateToNestSettings)
                }
            }
        }
    }
}
