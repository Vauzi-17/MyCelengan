package com.mycelengan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mycelengan.pages.*

@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {

    val navController =
        rememberNavController()

    val profileViewModel:
            ProfileViewModel =
        viewModel()

    val userData by
    authViewModel
        .userData
        .observeAsState()

    val usernameNow =
        userData
            ?.get(
                "username"
            ) as? String
            ?: ""

    val photoNow =
        userData
            ?.get(
                "photoUrl"
            ) as? String
            ?: "A"

    NavHost(

        navController =
            navController,

        startDestination =
            "welcome"

    ) {

        composable(
            "welcome"
        ) {

            WelcomePage(
                modifier,
                navController,
                authViewModel
            )
        }

        composable(
            "login"
        ) {

            LoginPage(
                modifier,
                navController,
                authViewModel
            )
        }

        composable(
            "signup"
        ) {

            SignupPage(
                modifier,
                navController,
                authViewModel
            )
        }

        composable(
            "home"
        ) {

            HomePage(
                modifier =
                    modifier,

                navController =
                    navController,

                authViewModel =
                    authViewModel,

                darkMode =
                    darkMode,

                onDarkModeChange =
                    onDarkModeChange
            )
        }

        composable(
            "pengaturan"
        ) {

            PengaturanPage(
                modifier,
                navController,
                authViewModel,
                darkMode,
                onDarkModeChange
            )
        }

        composable(
            "akun"
        ) {

            AccountSettingsScreen(

                modifier =
                    modifier,

                navController =
                    navController,

                authViewModel =
                    authViewModel,

                profileViewModel =
                    profileViewModel
            )
        }

        composable(
            "targetDetail/{id}"
        ) {

            val id =
                it.arguments
                    ?.getString(
                        "id"
                    )
                    ?: ""

            TargetDetailPage(

                targetId =
                    id,

                navController =
                    navController,

                authViewModel
            )
        }
    }
}