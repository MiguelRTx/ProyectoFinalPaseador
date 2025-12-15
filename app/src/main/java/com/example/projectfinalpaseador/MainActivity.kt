package com.example.projectfinalpaseador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projectfinalpaseador.ui.theme.navigation.Screen
import com.example.projectfinalpaseador.ui.theme.ProjectFinalPaseadorTheme
import com.example.projectfinalpaseador.ui.theme.auth.LoginScreen
import com.example.projectfinalpaseador.ui.theme.auth.RegisterScreen
import com.example.projectfinalpaseador.ui.theme.home.HomeScreen
import com.example.projectfinalpaseador.ui.theme.auth.AuthViewModel
import com.example.projectfinalpaseador.ui.theme.walks.WalksScreen
import com.example.projectfinalpaseador.ui.theme.walks.WalksViewModel
import com.example.projectfinalpaseador.ui.theme.walks.WalkDetailScreen
import com.example.projectfinalpaseador.ui.theme.walks.WalkDetailViewModel
import com.example.projectfinalpaseador.ui.theme.reviews.ReviewsScreen
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.activity.compose.rememberLauncherForActivityResult
import com.example.projectfinalpaseador.data.model.Walk
import android.Manifest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectFinalPaseadorTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()

                    var selectedWalk by remember { mutableStateOf<Walk?>(null) }

                    val locationPermissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions()
                    ) { }

                    LaunchedEffect(Unit) {
                        val permissions = mutableListOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                        }

                        locationPermissionLauncher.launch(permissions.toTypedArray())
                    }

                    val startDest = if (authViewModel.isUserLoggedIn()) Screen.Home.route else Screen.Login.route

                    NavHost(navController = navController, startDestination = startDest) {
                        composable(Screen.Login.route) { LoginScreen(navController, authViewModel) }
                        composable(Screen.Register.route) { RegisterScreen(navController, authViewModel) }
                        composable(Screen.Home.route) { HomeScreen(navController = navController, authViewModel = authViewModel) }

                        composable(Screen.Walks.route) {
                            val walksViewModel: WalksViewModel = viewModel()
                            WalksScreen(
                                navController = navController,
                                viewModel = walksViewModel,
                                onWalkClick = { walk: Walk ->
                                    selectedWalk = walk
                                    navController.navigate(Screen.WalkDetail.route)
                                }
                            )
                        }

                        composable(Screen.WalkDetail.route) {
                            selectedWalk?.let { walk ->
                                val walkDetailViewModel: WalkDetailViewModel = viewModel()
                                WalkDetailScreen(navController, walk, walkDetailViewModel)
                            }
                        }

                        composable(Screen.Reviews.route) { ReviewsScreen(navController) }
                    }
                }
            }
        }
    }
}
