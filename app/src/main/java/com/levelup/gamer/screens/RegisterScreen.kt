@file:OptIn(ExperimentalMaterial3Api::class)
package com.levelup.gamer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.levelup.gamer.ui.deps

@Composable
fun RegisterScreen(onRegistered: ()->Unit) {
    val d = deps()
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var edad by remember { mutableStateOf(TextFieldValue("")) }
    var referido by remember { mutableStateOf(TextFieldValue("")) }
    var err by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) Button@{
        Text("Crear cuenta (solo 18+)", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(edad, { edad = it }, label = { Text("Edad") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(referido, { referido = it }, label = { Text("Código de referido (opcional)") }, modifier = Modifier.fillMaxWidth())
        err?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(12.dp))
        val correo = email.text.trim()
        if (correo.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            err = "Correo inválido. Ej: nombre@dominio.com"
            return@Button
        }
        Button(onClick = {
            val e = edad.text.toIntOrNull() ?: 0
            if (e < 18) { err = "Debes ser mayor de 18 años."; return@Button }
            d.usuarioVM.login(nombre.text, email.text, e, referido.text.ifBlank { null })
            onRegistered()
        }, modifier = Modifier.fillMaxWidth()) { Text("Registrar") }
    }
}