package com.gaterevision.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gaterevision.app.ui.screens.AddTopicScreen
import com.gaterevision.app.ui.screens.DashboardScreen
import com.gaterevision.app.ui.viewmodel.TopicViewModel

private object Routes {
    const val DASHBOARD = "dashboard"
    const val ADD_TOPIC = "add_topic"
}

@Composable
fun AppNavGraph(viewModel: TopicViewModel) {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                viewModel = viewModel,
                onAddTopic = { navController.navigate(Routes.ADD_TOPIC) }
            )
        }
        composable(Routes.ADD_TOPIC) {
            AddTopicScreen(
                viewModel = viewModel,
                onDone = { navController.popBackStack() }
            )
        }
    }
}
