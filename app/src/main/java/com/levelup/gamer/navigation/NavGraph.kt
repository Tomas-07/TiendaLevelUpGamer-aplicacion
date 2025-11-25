package com.levelup.gamer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.levelup.gamer.screens.*
import com.levelup.gamer.ui.deps

@Composable
fun AppNav(nav: NavHostController) {

    NavHost(
        navController = nav,
        startDestination = Route.Splash.name
    ) {

        composable(Route.Splash.name) {
            SplashRoute(nav)
        }

        composable(Route.Login.name) {
            LoginScreen(
                onLogin = {
                    nav.navigate(Route.Catalogo.name) {
                        popUpTo(Route.Login.name) { inclusive = true }
                    }
                },
                onGoRegister = { nav.navigate(Route.Register.name) }
            )
        }

        composable(Route.Register.name) {
            RegisterScreen(
                onRegister = { nav.popBackStack() },
                onGoLogin = { nav.popBackStack() }
            )
        }

        composable(Route.Catalogo.name) {
            CatalogoScreen(
                onGoCart = { nav.navigate(Route.Carrito.name) },
                onGoPerfil = { nav.navigate(Route.Perfil.name) },
                onGoDetail = { codigo ->
                    nav.navigate("detalle/$codigo")
                }
            )
        }

        composable("detalle/{codigo}") { backStack ->
            val codigo = backStack.arguments?.getString("codigo") ?: ""
            DetalleProductoScreen(
                codigo = codigo,
                onBack = { nav.popBackStack() }
            )
        }

        composable(Route.Carrito.name) {
            CarritoScreen(onBack = { nav.popBackStack() })
        }

        composable(Route.Perfil.name) {
            PerfilScreen(
                onLogout = {
                    nav.navigate(Route.Login.name) {
                        popUpTo(Route.Catalogo.name) { inclusive = true }
                    }
                }
            )
        }
    }
}
