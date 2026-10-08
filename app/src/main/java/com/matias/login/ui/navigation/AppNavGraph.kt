package com.matias.login.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.matias.login.ui.iot.IoTControlScreen
import com.matias.login.ui.iot.IoTViewModel
import com.matias.login.ui.login.LoginScreen
import com.matias.login.ui.login.LoginViewModel
import com.matias.login.ui.register.RegisterScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object IoTControl : Screen("iot_control")
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val iotViewModel: IoTViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.IoTControl.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = loginViewModel,
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.IoTControl.route) {
            IoTControlScreen(
                viewModel = iotViewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.IoTControl.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
