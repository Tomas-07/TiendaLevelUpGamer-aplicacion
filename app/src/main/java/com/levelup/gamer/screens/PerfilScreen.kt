@file:OptIn(ExperimentalMaterial3Api::class)

package com.levelup.gamer.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import com.levelup.gamer.ui.deps


fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

@Composable
fun PerfilScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val d = deps()
    val usuarioVM = d.usuarioVM

    val context = LocalContext.current
    val activity = context.getActivity()

    val nombre by usuarioVM.nombre.collectAsState(initial = "")
    val email by usuarioVM.email.collectAsState(initial = "")
    val puntos by usuarioVM.puntos.collectAsState(initial = 0)
    val nivel by usuarioVM.nivel.collectAsState(initial = 1)
    val foto by usuarioVM.photoUri.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF00E676), Color(0xFF00C853))
                        )
                    )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(22.dp))

            ProfilePhotoPicker(
                initialPhotoUri = foto,
                onPhotoChanged = { uri -> usuarioVM.setPhoto(uri) }
            )

            Spacer(Modifier.height(10.dp))

            // TARJETA DE INFORMACIÓN
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Column(
                    Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Información Personal", style = MaterialTheme.typography.titleMedium)

                    DataRow("Nombre", nombre)
                    DataRow("Email", email)
                    DataRow("Nivel", "Nivel $nivel")
                    DataRow("Puntos acumulados", "$puntos pts")

                    LinearProgressIndicator(
                        progress = calcProgresoNivel(puntos),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        color = Color(0xFF00C853)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // BOTÓN SOPORTE
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Contactar Soporte", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(8.dp))

            // BOTÓN LOGOUT
            Button(
                onClick = {
                    usuarioVM.logout()
                    Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text("Cerrar sesión", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DataRow(label: String, value: String) {
    Column {
        Text(label, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        Text(value.ifBlank { "—" })
        Divider(modifier = Modifier.padding(vertical = 6.dp))
    }
}

private fun calcProgresoNivel(puntos: Int): Float = when {
    puntos >= 1000 -> 1f
    puntos >= 600 -> (puntos - 600) / 400f
    puntos >= 300 -> (puntos - 300) / 300f
    puntos >= 120 -> (puntos - 120) / 180f
    else -> puntos / 120f
}

fun openWhatsApp(number: String, message: String, activity: ComponentActivity) {
    val uri = Uri.parse("https://wa.me/$number?text=" + Uri.encode(message))
    val intent = Intent(Intent.ACTION_VIEW, uri)
    activity.startActivity(intent)
}

