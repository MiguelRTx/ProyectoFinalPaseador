package com.example.projectfinalpaseador.ui.theme.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("com/example/projectfinalpaseador/ui/theme/home")

    object Walks : Screen("com/example/projectfinalpaseador/ui/theme/walks")
    object Reviews : Screen("reviews")
    object WalkDetail : Screen("walk_detail")

}