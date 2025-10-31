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
fun LoginScreen(onLogged: ()->Unit, onGoRegister:()->Unit) {
    val d = deps()
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Level‑Up Gamer", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            d.usuarioVM.login(nombre.text.ifBlank {"Gamer"}, email.text.ifBlank {"gamer@levelup.cl"}, 99, null)
            onLogged()
        }, modifier = Modifier.fillMaxWidth()) { Text("Ingresar") }
        TextButton(onClick = onGoRegister) { Text("Crear cuenta (18+)") }
    }
}