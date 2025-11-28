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
        startDestination = Route.Splash.name // Asegúrate de tener tu objeto/enum Route definido
    ) {
        // Pantalla Splash
        composable(Route.Splash.name) {
            SplashRoute(nav)
        }

        // Pantalla Login
        composable(Route.Login.name) {
            LoginScreen(
                onGoCart = { nav.navigate(Route.Carrito.name) },
                onGoRegister = { nav.navigate(Route.Register.name) },
                onLogin = {
                    // Al loguearse exitosamente, vamos al catálogo y borramos el login del historial
                    nav.navigate(Route.Catalogo.name) {
                        popUpTo(Route.Login.name) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla Register
        composable(Route.Register.name) {
            RegisterScreen(
                onGoLogin = { nav.navigate(Route.Login.name) },
                onRegister = {
                    // Al registrarse exitosamente, volvemos atrás (al login o donde corresponda)
                    nav.popBackStack()
                }
            )
        }

        // Pantalla Catalogo (productos)
        composable(Route.Catalogo.name) {
            CatalogoScreen(
                onGoCart = { nav.navigate(Route.Carrito.name) },
                onGoPerfil = { nav.navigate(Route.Perfil.name) },
                onGoDetail = { id ->
                    nav.navigate("detalle/$id")
                }
            )
        }

        // Pantalla DetalleProducto (con ID)
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
                        popUpTo(Route.Catalogo.name) { inclusive = true }
                    }
                }
            )
        }
    }
}