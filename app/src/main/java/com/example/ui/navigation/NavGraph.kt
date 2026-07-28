package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.CapRwandaApplication
import com.example.ui.screens.editor.TimelineEditorScreen
import com.example.ui.screens.editor.TimelineViewModel
import com.example.ui.screens.export.ExportScreen
import com.example.ui.screens.export.ExportViewModel
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.home.HomeViewModel
import com.example.ui.screens.import.VideoImportScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.settings.SettingsViewModel

@Composable
fun CapRwandaNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    val context = LocalContext.current
    val app = context.applicationContext as CapRwandaApplication
    val repository = app.repository

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            val homeVm: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
            HomeScreen(
                viewModel = homeVm,
                onNavigateToImport = { navController.navigate(Screen.VideoImport.route) },
                onNavigateToEditor = { projectId -> navController.navigate(Screen.TimelineEditor.createRoute(projectId)) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.VideoImport.route) {
            val homeVm: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
            VideoImportScreen(
                onProjectCreated = { projId -> navController.navigate(Screen.TimelineEditor.createRoute(projId)) },
                onBackClick = { navController.popBackStack() },
                onCreateProject = { title, aspect, items ->
                    homeVm.createProject(title, aspect, items) { newProjId ->
                        navController.navigate(Screen.TimelineEditor.createRoute(newProjId))
                    }
                }
            )
        }

        composable(
            route = Screen.TimelineEditor.route,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
            val editorVm: TimelineViewModel = viewModel(
                factory = TimelineViewModel.Factory(repository, projectId)
            )
            TimelineEditorScreen(
                viewModel = editorVm,
                onNavigateToExport = { projId -> navController.navigate(Screen.Export.createRoute(projId)) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Export.route,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
            val exportVm: ExportViewModel = viewModel(
                factory = ExportViewModel.Factory(repository, projectId)
            )
            ExportScreen(
                viewModel = exportVm,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            val settingsVm: SettingsViewModel = viewModel()
            SettingsScreen(
                viewModel = settingsVm,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
