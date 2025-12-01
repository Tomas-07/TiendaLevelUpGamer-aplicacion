package com.levelup.gamer.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun ProfilePhotoPicker(
    initialPhotoUri: Uri?,
    onPhotoChanged: (Uri) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // --- LAUNCHERS PARA ABRIR CÁMARA Y GALERÍA ---

    // Launcher para la galería (Foto ya existente)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onPhotoChanged(it) }
    }

    // Launcher para la cámara (Foto nueva)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            onPhotoChanged(tempPhotoUri!!)
        }
    }

    // Launcher para pedir permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Si da permiso, creamos el archivo temporal y abrimos la cámara
            tempPhotoUri = createImageFile(context)
            cameraLauncher.launch(tempPhotoUri!!)
        } else {
            Toast.makeText(context, "Se necesita permiso para usar la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    Box(contentAlignment = Alignment.BottomEnd) {
        // --- FOTO DE PERFIL (EL CÍRCULO GRANDE) ---
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable { showDialog = true },
            contentAlignment = Alignment.Center
        ) {
            if (initialPhotoUri != null) {

                Image(
                    painter = rememberAsyncImagePainter(initialPhotoUri),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(70.dp),
                    tint = Color.White
                )
            }
        }

        // --- ICONO DE CÁMARA PEQUEÑO ---
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF00C853))
                .border(2.dp, Color.White, CircleShape)
                .clickable { showDialog = true }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Cambiar foto", tint = Color.White)
        }
    }

    // --- DIÁLOGO DE OPCIONES ---
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Cambiar Foto de Perfil") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            showDialog = false
                            galleryLauncher.launch("image/*") // Abre la galería
                        }
                    ) {
                        Text("Elegir de la Galería")
                    }
                    TextButton(
                        onClick = {
                            showDialog = false
                            // Revisa si tenemos permiso de cámara
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                tempPhotoUri = createImageFile(context)
                                cameraLauncher.launch(tempPhotoUri!!)
                            } else {
                                // Si no, lo pide
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    ) {
                        Text("Tomar Foto con la Cámara")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// --- FUNCIÓN AUXILIAR PARA CREAR ARCHIVOS TEMPORALES ---
private fun createImageFile(context: Context): Uri {
    val storageDir: File? = context.getExternalFilesDir(null)
    val file = File.createTempFile(
        "JPEG_${System.currentTimeMillis()}_",
        ".jpg",
        storageDir
    )

    // Usamos FileProvider para que sea seguro en Android modernos
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}