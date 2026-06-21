package com.chama.groupmoneymanager.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Login    : Screen("login")
    object Register : Screen("register")
    object Home     : Screen("home")
    object Welcome  : Screen("welcome")

    // ── Bottom nav tabs (nested inside Home graph) ──────────────────────────
    object HomeTab          : Screen("home_tab")
    object ActivityTab      : Screen("activity_tab")
    object NotificationsTab : Screen("notifications_tab")
    object MembersTab       : Screen("members_tab")

    object LoanList : Screen("loan_list/{nestId}") {
        fun createRoute(nestId: Long) = "loan_list/$nestId"
    }

    object NestSettings : Screen("nest_settings/{nestId}") {
        fun createRoute(nestId: Long) = "nest_settings/$nestId"
    }

    object Settings : Screen("settings")

    /**
     * Shown after a nest is successfully created so the manager can copy the invite code.
     * nestName is URL-encoded to handle spaces and special characters safely.
     */
    object CreateNestSuccess : Screen("create_nest_success/{nestName}/{inviteCode}") {
        fun createRoute(nestName: String, inviteCode: String): String =
            "create_nest_success/${Uri.encode(nestName)}/$inviteCode"
    }
}
