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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    onPhotoChanged: (String) -> Unit
) {
    val ctx = LocalContext.current

    var photoUri by remember { mutableStateOf(initialPhotoUri?.let(Uri::parse)) }
    var pendingUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && pendingUri != null) {
                photoUri = pendingUri
                onPhotoChanged(pendingUri.toString())
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                photoUri = uri
                onPhotoChanged(uri.toString())
            }
        }

    val permLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

    fun hasPermission(perm: String) =
        ContextCompat.checkSelfPermission(ctx, perm) == PackageManager.PERMISSION_GRANTED

    fun createImageUri(): Uri {
        val dir = File(ctx.cacheDir, "avatar").apply { mkdirs() }
        val file = File(dir, "avatar_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(ctx, ctx.packageName + ".fileprovider", file)
    }

    fun openCamera() {
        if (!hasPermission(Manifest.permission.CAMERA)) {
            permLauncher.launch(Manifest.permission.CAMERA)
            return
        }
        val uri = createImageUri()
        pendingUri = uri
        cameraLauncher.launch(uri)
    }

    fun openGallery() {
        val perm =
            if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES
            else Manifest.permission.READ_EXTERNAL_STORAGE

        if (!hasPermission(perm)) {
            permLauncher.launch(perm)
            return
        }
        galleryLauncher.launch("image/*")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        } else {
            Image(
                painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { openCamera() }) { Text("Cámara") }
            Button(onClick = { openGallery() }) { Text("Galería") }
        }
    }
}
