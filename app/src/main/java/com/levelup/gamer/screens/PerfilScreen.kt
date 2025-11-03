@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.levelup.gamer.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.levelup.gamer.ui.deps

@Composable
fun PerfilScreen(onLogout: () -> Unit = {}) {
    val d = deps()
    val usuarioVM = d.usuarioVM
    val context = LocalContext.current

    val nombre by usuarioVM.nombre.collectAsState(initial = "")
    val email by usuarioVM.email.collectAsState(initial = "")
    val puntos by usuarioVM.puntos.collectAsState(initial = 0)
    val nivel by usuarioVM.nivel.collectAsState(initial = 1)
    val foto by usuarioVM.photoUri.collectAsState(initial = null)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi Perfil") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 📸 Foto persistente
            ProfilePhotoPicker(
                initialPhotoUri = foto,
                onPhotoChanged = { uri -> usuarioVM.setPhoto(uri) }
            )

            Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Nombre", fontWeight = FontWeight.SemiBold)
                    Text(if (nombre.isNotBlank()) nombre else "—")
                    Divider()
                    Text("Email", fontWeight = FontWeight.SemiBold)
                    Text(if (email.isNotBlank()) email else "—")
                    Divider()
                    Text("Nivel", fontWeight = FontWeight.SemiBold)
                    Text("Nivel $nivel")
                    LinearProgressIndicator(
                        progress = calcProgresoNivel(puntos),
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Puntos: $puntos pts", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Button(
                onClick = {
                    usuarioVM.logout()
                    Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.errorContainer)
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}

private fun calcProgresoNivel(puntos: Int): Float = when {
    puntos >= 1000 -> 1f
    puntos >= 600 -> (puntos - 600) / 400f
    puntos >= 300 -> (puntos - 300) / 300f
    puntos >= 120 -> (puntos - 120) / 180f
    else -> puntos / 120f
}
