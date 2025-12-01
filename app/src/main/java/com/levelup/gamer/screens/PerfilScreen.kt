package com.levelup.gamer.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.levelup.gamer.model.GameItem
import com.levelup.gamer.remote.GameClient
import com.levelup.gamer.ui.deps


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val d = deps()
    val usuarioVM = d.usuarioVM


    val context = LocalContext.current

    val nombre by usuarioVM.nombre.collectAsState(initial = "")
    val email by usuarioVM.email.collectAsState(initial = "")
    val puntos by usuarioVM.puntos.collectAsState(initial = 0)
    val nivel by usuarioVM.nivel.collectAsState(initial = 1)
    val foto by usuarioVM.photoUri.collectAsState(initial = null)

    var juegoRecomendado by remember { mutableStateOf<GameItem?>(null) }
    var cargandoJuego by remember { mutableStateOf(true) }

    // Estado para Alerta
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val juegos = GameClient.api.getGames()
            juegoRecomendado = juegos.shuffled().firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cargandoJuego = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(
                    Brush.horizontalGradient(listOf(Color(0xFF00E676), Color(0xFF00C853)))
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(22.dp))

            ProfilePhotoPicker(
                initialPhotoUri = foto?.let { Uri.parse(it) },
                onPhotoChanged = { uri -> usuarioVM.setPhoto(uri.toString()) }
            )

            Spacer(Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Información Personal", style = MaterialTheme.typography.titleMedium)
                    DataRow("Nombre", nombre)
                    DataRow("Email", email)
                    DataRow("Nivel", "Nivel $nivel")
                    DataRow("Puntos acumulados", "$puntos pts")
                    LinearProgressIndicator(
                        progress = calcProgresoNivel(puntos),
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        color = Color(0xFF00C853)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("🎮 Recomendado para ti", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            if (cargandoJuego) {
                CircularProgressIndicator(modifier = Modifier.size(30.dp))
            } else if (juegoRecomendado != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = juegoRecomendado!!.thumbnail, contentDescription = null,
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(juegoRecomendado!!.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Text(juegoRecomendado!!.genre, style = MaterialTheme.typography.bodySmall, color = Color(0xFF00E676))
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(onClick = { openWhatsAppSeguro(context, "+56940525668", "Hola, necesito ayuda.") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(14.dp)) {
                Text("Contactar Soporte")
            }
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { usuarioVM.logout(); onLogout() },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Cerrar sesión")
            }
            Spacer(Modifier.height(8.dp))

            // BOTÓN ROJO CORREGIDO
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("ELIMINAR CUENTA", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(Modifier.height(24.dp))
        }


        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("¿Eliminar Cuenta?") },
                text = { Text("Esta acción es irreversible. Se borrarán todos tus datos.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false

                            usuarioVM.eliminarCuenta {

                                onLogout()
                            }
                        }
                    ) {
                        Text("SÍ, BORRAR", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
fun DataRow(label: String, value: String) {
    Column {
        Text(label, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        Text(value.ifBlank { "—" })
        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
    }
}

private fun calcProgresoNivel(puntos: Int): Float = when {
    puntos >= 1000 -> 1f
    else -> puntos / 1000f
}

fun openWhatsAppSeguro(context: Context, number: String, message: String) {
    try {
        val uri = Uri.parse("https://wa.me/$number?text=" + Uri.encode(message))
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Error al abrir WhatsApp", Toast.LENGTH_SHORT).show()
    }
}