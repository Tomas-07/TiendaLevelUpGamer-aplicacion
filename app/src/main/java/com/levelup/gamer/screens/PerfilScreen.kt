@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.levelup.gamer.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import coil.compose.AsyncImage
import com.levelup.gamer.ui.deps
import com.levelup.gamer.ui.openWhatsApp

fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

@Composable
fun PerfilScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val d = deps()
    val usuarioVM = d.usuarioVM
    val context = LocalContext.current
    val activity = context.getActivity()

    val nombre by usuarioVM.nombre.collectAsState("")
    val email by usuarioVM.email.collectAsState("")
    val puntos by usuarioVM.puntos.collectAsState(0)
    val nivel by usuarioVM.nivel.collectAsState(1)
    val foto by usuarioVM.photoUri.collectAsState(null)
    val edad by usuarioVM.edad.collectAsState(0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // FOTO (o default)
            AsyncImage(
                model = foto ?: "https://i.imgur.com/UMKxYkE.png",
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(130.dp)
            )

            Card(
                Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {

                    Text("Nombre", fontWeight = FontWeight.Bold)
                    Text(nombre)
                    Spacer(Modifier.height(10.dp))

                    Text("Email", fontWeight = FontWeight.Bold)
                    Text(email)
                    Spacer(Modifier.height(10.dp))

                    Text("Edad", fontWeight = FontWeight.Bold)
                    Text("$edad años")
                    Spacer(Modifier.height(10.dp))

                    Text("Nivel $nivel", fontWeight = FontWeight.Bold)
                    LinearProgressIndicator(
                        progress = calcProgresoNivel(puntos),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Puntos: $puntos")
                }
            }

            Button(
                onClick = {
                    if (activity != null) {
                        openWhatsApp(
                            number = "+56940525668",
                            message = "Hola, necesito ayuda con mi cuenta LevelUp.",
                            activity = activity as ComponentActivity
                        )
                    } else {
                        Toast.makeText(context, "Error al abrir WhatsApp", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Contactar Soporte (WhatsApp)")
            }

            Button(
                onClick = {
                    usuarioVM.logout()
                    Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
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
