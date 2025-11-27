@file:OptIn(ExperimentalMaterial3Api::class)

package com.levelup.gamer.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.levelup.gamer.R
import com.levelup.gamer.ui.deps
import com.levelup.gamer.ui.openWhatsApp

// Obtener actividad real
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
    val ctx = LocalContext.current

    // Datos usuario
    val nombre by usuarioVM.nombre.collectAsState("")
    val email by usuarioVM.email.collectAsState("")
    val puntos by usuarioVM.puntos.collectAsState(0)
    val nivel by usuarioVM.nivel.collectAsState(1)
    val foto by usuarioVM.photoUri.collectAsState(initial = null)

    // Animación del borde
    val infinite = rememberInfiniteTransition()
    val borderAnim by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // FOTO con animación gamer
            Box(contentAlignment = Alignment.Center) {

                val borderBrush = Brush.sweepGradient(
                    listOf(
                        Color(0xFF00E5FF),
                        Color(0xFF00FF95),
                        Color(0xFF00E5FF)
                    )
                )

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .border(
                            BorderStroke(3.dp, borderBrush),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                ) {
                    ProfilePhotoPicker(
                        initialPhotoUri = foto,
                        onPhotoChanged = { usuarioVM.setPhoto(it) }
                    )
                }
            }

            // CARD DATOS USUARIO
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Nombre", fontWeight = FontWeight.Bold)
                    Text(nombre)

                    Divider()

                    Text("Email", fontWeight = FontWeight.Bold)
                    Text(email)

                    Divider()

                    Text("Nivel", fontWeight = FontWeight.Bold)
                    Text("Nivel $nivel")

                    LinearProgressIndicator(
                        progress = calcProgresoNivel(puntos),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Puntos: $puntos pts")
                }
            }

            Button(
                onClick = {
                    val act = ctx.getActivity()
                    if (act != null) {
                        openWhatsApp(
                            "+56940525668",
                            "Hola, necesito ayuda con mi cuenta LevelUp.",
                            act as ComponentActivity
                        )
                    } else Toast.makeText(ctx, "Error", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Contactar soporte (WhatsApp)")
            }

            Button(
                onClick = {
                    usuarioVM.logout()
                    Toast.makeText(ctx, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}

private fun calcProgresoNivel(p: Int): Float =
    when {
        p >= 1000 -> 1f
        p >= 600 -> (p - 600) / 400f
        p >= 300 -> (p - 300) / 300f
        p >= 120 -> (p - 120) / 180f
        else -> p / 120f
    }
