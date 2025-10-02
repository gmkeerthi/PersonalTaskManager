package com.example.personaltaskmanager.ui.nav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.personaltaskmanager.ui.screens.TaskDetailScreen
import com.example.personaltaskmanager.ui.screens.TaskListScreen
import com.example.personaltaskmanager.ui.screens.CreateEditTaskScreen

@Composable
fun NavGraph(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "list"){
        composable("list"){
            TaskListScreen(onNavigateToCreate = { navController.navigate("create") }, onOpen = { id -> navController.navigate("detail/$id") })
        }
        composable("create"){
            CreateEditTaskScreen(onSaved = { navController.popBackStack() })
        }
        composable("detail/{taskId}", arguments = listOf(navArgument("taskId"){ type = NavType.LongType })){ backStackEntry ->
            val id = backStackEntry.arguments?.getLong("taskId") ?: 0L
            TaskDetailScreen(taskId = id, onBack = { navController.popBackStack() })
        }
    }
}