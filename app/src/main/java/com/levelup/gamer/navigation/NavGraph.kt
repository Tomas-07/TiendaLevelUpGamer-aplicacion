package com.levelup.gamer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.levelup.gamer.screens.CarritoScreen
import com.levelup.gamer.screens.CatalogoScreen
import com.levelup.gamer.screens.DetalleProductoScreen
import com.levelup.gamer.screens.LoginScreen
import com.levelup.gamer.screens.PerfilScreen
import com.levelup.gamer.screens.RegisterScreen
import com.levelup.gamer.screens.SplashRoute


@Composable
fun AppNav(nav: NavHostController) {

    NavHost(
        navController = nav,

        startDestination = Route.Splash.name
    ) {
        // Pantalla Splash
        composable(Route.Splash.name) {
            SplashRoute(nav)
        }

        // Pantalla Login
        composable(Route.Login.name) {
            LoginScreen(
                // CORREGIDO: Se usa .name en las llamadas a navigate.
                onGoCart = { nav.navigate(Route.Carrito.name) },
                onGoRegister = { nav.navigate(Route.Register.name) },
                onLogin = {
                    nav.navigate(Route.Catalogo.name) {
                        // Se utiliza .name para la ruta de popUpTo.
                        popUpTo(Route.Login.name) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla Register
        composable(Route.Register.name) {
            RegisterScreen(
                // CORREGIDO: Se usa .name en las llamadas a navigate.
                onGoLogin = { nav.navigate(Route.Login.name) },
                onRegister = { nav.popBackStack() }
            )
        }

        // Pantalla Catalogo
        composable(Route.Catalogo.name) {
            CatalogoScreen(
                // CORREGIDO: Se usa .name en las llamadas a navigate.
                onGoCart = { nav.navigate(Route.Carrito.name) },
                onGoPerfil = { nav.navigate(Route.Perfil.name) },
                // La ruta de detalle se pasa con el argumento
                onGoDetail = { id -> nav.navigate("detalle/$id") }
            )
        }

        // Pantalla Detalle (con argumento de ruta)
        composable("detalle/{id}") { backStack ->
            val id = backStack.arguments?.getString("id")?.toLongOrNull() ?: 0L
            DetalleProductoScreen(
                id = id,
                onBack = { nav.popBackStack() }
            )
        }

        // Pantalla Carrito
        composable(Route.Carrito.name) {
            CarritoScreen(onBack = { nav.popBackStack() })
        }

        // Pantalla Perfil
        composable(Route.Perfil.name) {
            PerfilScreen(
                onLogout = {
                    nav.navigate(Route.Login.name) {
                        // Se utiliza .name para la ruta de popUpTo.
                        popUpTo(Route.Catalogo.name) { inclusive = true }
                    }
                }
            )
        }
    }
}