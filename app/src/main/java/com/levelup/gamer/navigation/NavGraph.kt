package com.levelup.gamer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.levelup.gamer.screens.*

@Composable
fun AppNav(nav: NavHostController) {

    NavHost(navController = nav, startDestination = Route.Login.name) {

        composable(Route.Login.name) {
            LoginScreen(
                onLogin = {
                    nav.navigate(Route.Catalogo.name) {
                        popUpTo(Route.Login.name) { inclusive = true }
                        launchSingleTop = true
                    }},
                onGoRegister = { nav.navigate(Route.Register.name) }
            )
        }

        composable(Route.Register.name) {
            RegisterScreen(onRegister = { nav.popBackStack() })
        }

        composable(Route.Catalogo.name) {
            CatalogoScreen(
                onGoCart = { nav.navigate(Route.Carrito.name) },
                onGoPerfil = { nav.navigate(Route.Perfil.name) }
            )
        }

        composable(Route.Carrito.name) {
            CarritoScreen(onBack = { nav.popBackStack() })
        }

        composable(Route.Perfil.name) {
            PerfilScreen(
                onBack = { nav.navigate(Route.Catalogo.name) { launchSingleTop = true } },
                onLogout = {
                    nav.navigate(Route.Login.name) {
                        popUpTo(Route.Catalogo.name) { inclusive = true }
                    }
                }
            )
        }

    }
}
