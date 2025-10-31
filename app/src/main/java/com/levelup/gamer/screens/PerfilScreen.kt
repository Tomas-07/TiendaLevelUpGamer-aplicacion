@file:OptIn(ExperimentalMaterial3Api::class)
package com.levelup.gamer.screens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.Logout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.levelup.gamer.ui.deps
import com.levelup.gamer.ui.MainActivity
import com.levelup.gamer.ui.openWhatsApp

@Composable
fun PerfilScreen(onLogout:()->Unit) {
    val d = deps()
    val ctx = LocalContext.current as MainActivity
    val nombre by d.usuarioVM.nombre.collectAsState()
    val email by d.usuarioVM.email.collectAsState()
    val puntos by d.usuarioVM.puntos.collectAsState()
    val nivel by d.usuarioVM.nivel.collectAsState()
    val esDuoc by d.usuarioVM.esDuoc.collectAsState()

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("Perfil", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text("Nombre: $nombre")
        Text("Email: $email")
        Text("Nivel: $nivel")
        Text("Puntos: $puntos")
        if (esDuoc) Text("Beneficio: Descuento de por vida 20%")
        Spacer(Modifier.height(16.dp))
        Button(onClick = { openWhatsApp("56912345678", "Hola, necesito soporte técnico de Level‑Up Gamer", ctx) }) { Icon(Icons.Filled.Support, contentDescription = null); Spacer(Modifier.width(8.dp)) 
            Text("Soporte técnico (WhatsApp)")
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { d.usuarioVM.logout(); onLogout() }) { Icon(Icons.Filled.Logout, contentDescription = null); Spacer(Modifier.width(8.dp)); Text("Cerrar sesión") }
    }
}