@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.levelup.gamer.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Support
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import com.levelup.gamer.ui.MainActivity

@Composable
fun PerfilScreen(onLogout: () -> Unit = {}) {
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Perfil") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "👤 Nombre: Usuario Gamer", style = MaterialTheme.typography.titleMedium)
            Text(text = "🎮 Nivel: 5", style = MaterialTheme.typography.bodyLarge)
            Text(text = "⭐ Puntos LevelUp: 1200", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(20.dp))

            // ✅ Botón con recurso nativo (vibración + mensaje visible)
            Button(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    Toast.makeText(context, "Abriendo soporte por WhatsApp…", Toast.LENGTH_SHORT).show()
                    openWhatsApp("56912345678", "Hola, necesito soporte", context)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Support, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Soporte técnico (WhatsApp)")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ✅ Botón de cerrar sesión (usa el callback del NavGraph)
            OutlinedButton(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    Toast.makeText(context, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
                    onLogout() // Llama al callback del NavGraph
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar sesión")
            }
        }
    }
}

// ✅ Función auxiliar para abrir WhatsApp
fun openWhatsApp(phone: String, message: String, context: android.content.Context) {
    try {
        val uri = Uri.parse("https://wa.me/$phone?text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
    }
}
