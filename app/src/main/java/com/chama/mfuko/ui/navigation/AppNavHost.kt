package com.chama.mfuko.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.chama.mfuko.ui.features.auth.login.LoginScreen
import com.chama.mfuko.ui.features.auth.register.RegisterScreen
import com.chama.mfuko.ui.features.loans.LoanListScreen
import com.chama.mfuko.ui.features.nests.create.CreateNestSuccessScreen
import com.chama.mfuko.ui.features.nests.create.WelcomeScreen
import com.chama.mfuko.ui.features.nests.settings.NestSettingsScreen
import com.chama.mfuko.ui.features.settings.SettingsScreen

@Composable
fun AppNavHost(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {

        // ── Auth ─────────────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToWelcome = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin   = { navController.navigateUp() },
                onRegisterSuccess   = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Nest setup ────────────────────────────────────────────────────────
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onSuccess = {
                    // User joined a nest → go to dashboard. launchSingleTop avoids
                    // stacking a second Home entry when this was reached via
                    // "Switch Nest" (where the original Home is still below Welcome).
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNestCreated = { nestName, inviteCode ->
                    // User created a nest → show invite code screen
                    navController.navigate(
                        Screen.CreateNestSuccess.createRoute(nestName, inviteCode)
                    ) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                // Only offer a back arrow when there's actually a previous
                // screen to return to (i.e. reached via "Switch Nest" from an
                // existing dashboard, not the first-run post-login flow).
                onNavigateBack = if (navController.previousBackStackEntry != null) {
                    { navController.navigateUp() }
                } else {
                    null
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route     = Screen.CreateNestSuccess.route,
            arguments = listOf(
                navArgument("nestName")   { type = NavType.StringType },
                navArgument("inviteCode") { type = NavType.StringType }
            )
        ) {
            // ViewModel reads nestName and inviteCode from SavedStateHandle automatically.
            CreateNestSuccessScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.CreateNestSuccess.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ── Main dashboard (bottom nav: Home / Activity / Notifications / Members) ─
        composable(Screen.Home.route) {
            BottomNavScaffold(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToManageLoans = { nestId ->
                    navController.navigate(Screen.LoanList.createRoute(nestId))
                },
                onNavigateToSwitchNest = {
                    // Keep Home on the back stack so the user can back out of
                    // Welcome without being forced to join/create a nest.
                    navController.navigate(Screen.Welcome.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToNestSettings = { nestId ->
                    navController.navigate(Screen.NestSettings.createRoute(nestId))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }

        // ── Nest management ───────────────────────────────────────────────────
        composable(
            route     = Screen.LoanList.route,
            arguments = listOf(navArgument("nestId") { type = NavType.LongType })
        ) { backStackEntry ->
            val nestId = backStackEntry.arguments?.getLong("nestId") ?: 0L
            LoanListScreen(nestId = nestId)
        }

        composable(
            route     = Screen.NestSettings.route,
            arguments = listOf(navArgument("nestId") { type = NavType.LongType })
        ) { backStackEntry ->
            val nestId = backStackEntry.arguments?.getLong("nestId") ?: 0L
            NestSettingsScreen(nestId = nestId, onNavigateBack = { navController.navigateUp() })
        }
    }
}
