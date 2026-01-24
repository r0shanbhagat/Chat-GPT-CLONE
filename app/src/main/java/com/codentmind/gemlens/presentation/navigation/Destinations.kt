package com.codentmind.gemlens.presentation.navigation


interface Destinations {
    val route: String
}

object Home : Destinations {
    override val route: String = "Home"
}

object Settings : Destinations {
    override val route = "Settings"
}

//object SetApi : Destinations {
//    override val route = "Set Api"
//}

object About : Destinations {
    override val route = "About"
}