@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.levelup.gamer.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.levelup.gamer.R
import java.io.File


@Composable
fun ProfilePhotoPicker(
    initialPhotoUri: String? = null,
    onPhotoChanged: (String) -> Unit = {}
) {
    val context = LocalContext.current


    var photoUri by remember { mutableStateOf(initialPhotoUri?.let(Uri::parse)) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }


    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok && pendingCameraUri != null) {
            photoUri = pendingCameraUri
            onPhotoChanged(pendingCameraUri.toString())
        }
    }
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            photoUri = uri
            onPhotoChanged(uri.toString())
        }
    }


    val requestSinglePermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Al reintentar, seguimos el flujo en el botón */ }

    fun has(permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun openCamera() {
        if (!has(Manifest.permission.CAMERA)) {
            requestSinglePermission.launch(Manifest.permission.CAMERA)
            return
        }
        val tempUri = createImageUri(context)
        pendingCameraUri = tempUri
        takePicture.launch(tempUri)
    }

    fun openGallery() {
        val readPerm =
            if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES
            else Manifest.permission.READ_EXTERNAL_STORAGE

        if (Build.VERSION.SDK_INT < 29 && !has(readPerm)) {
            requestSinglePermission.launch(readPerm)
            return
        }
        pickImage.launch("image/*")
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(Modifier.size(140.dp), contentAlignment = Alignment.Center) {
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(128.dp).clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(128.dp).clip(CircleShape)
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { openCamera() }) { Text("Cámara") }
            Button(onClick = { openGallery() }) { Text("Galería") }
        }
    }
}

/* ---------- Helpers ---------- */

private fun createImageUri(context: android.content.Context): Uri {
    val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
    val file = File(imagesDir, "avatar_${System.currentTimeMillis()}.jpg")
    val authority = context.packageName + ".fileprovider"
    return FileProvider.getUriForFile(context, authority, file)
}
