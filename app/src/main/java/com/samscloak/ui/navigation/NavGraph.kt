package com.samscloak.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.samscloak.ui.screens.DashboardScreen
import com.samscloak.ui.screens.LoginScreen
import com.samscloak.ui.screens.RegisterScreen
import com.samscloak.ui.viewmodel.AuthViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object JobDetail : Screen("job_detail/{jobId}")
    object AddJob : Screen("add_job")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // Check if user is already logged in
    val startDestination = if (authViewModel.isLoggedIn()) {
        Screen.Dashboard.route
    } else {
        Screen.Login.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
        
        composable(Screen.JobDetail.route) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId")?.toIntOrNull() ?: return@composable
            // TODO: Implement JobDetailScreen
            // JobDetailScreen(jobId = jobId)
        }
        
        composable(Screen.AddJob.route) {
            // TODO: Implement AddJobScreen
            // AddJobScreen()
        }
        
        composable(Screen.Profile.route) {
            // TODO: Implement ProfileScreen
            // ProfileScreen()
        }
        
        composable(Screen.Settings.route) {
            // TODO: Implement SettingsScreen
            // SettingsScreen()
        }
    }
}
