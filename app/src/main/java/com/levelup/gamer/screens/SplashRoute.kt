@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.levelup.gamer.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.levelup.gamer.navigation.Route
import com.levelup.gamer.ui.deps
import kotlinx.coroutines.delay

@Composable
fun SplashRoute(nav: NavHostController) {
    val d = deps()
    val logged by d.usuarioVM.isLoggedIn.collectAsState(initial = false)

    LaunchedEffect(logged) {

        delay(600)

        val go = if (logged) Route.Catalogo.name else Route.Login.name


        nav.navigate(go) {
            popUpTo(Route.Splash.name) { inclusive = true }
            launchSingleTop = true
        }


    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
