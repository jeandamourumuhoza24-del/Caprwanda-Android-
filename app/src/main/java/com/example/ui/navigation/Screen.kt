package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object VideoImport : Screen("import")
    object TimelineEditor : Screen("timeline_editor/{projectId}") {
        fun createRoute(projectId: Long) = "timeline_editor/$projectId"
    }
    object Export : Screen("export/{projectId}") {
        fun createRoute(projectId: Long) = "export/$projectId"
    }
    object Settings : Screen("settings")
}
