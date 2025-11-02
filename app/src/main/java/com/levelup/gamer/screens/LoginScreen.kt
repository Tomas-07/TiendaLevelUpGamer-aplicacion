@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.levelup.gamer.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(onLogin: () -> Unit = {}, onGoRegister: () -> Unit = {}) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var emailTouched by remember { mutableStateOf(false) }
    var passTouched by remember { mutableStateOf(false) }
    var showPass by remember { mutableStateOf(false) }

    val emailError = when {
        !emailTouched -> null
        email.isBlank() -> "Campo obligatorio"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email inválido"
        else -> null
    }
    val passError = when {
        !passTouched -> null
        pass.isBlank() -> "Campo obligatorio"
        pass.length < 6 -> "Mínimo 6 caracteres"
        else -> null
    }
    val formValid = emailError == null && passError == null && email.isNotBlank() && pass.isNotBlank()

    Scaffold(topBar = { TopAppBar(title = { Text("Iniciar sesión") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = email, onValueChange = { email = it; emailTouched = true },
                label = { Text("Email") }, isError = emailError != null,
                supportingText = { if (emailError != null) Text(emailError) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pass, onValueChange = { pass = it; passTouched = true },
                label = { Text("Contraseña") }, isError = passError != null,
                supportingText = { if (passError != null) Text(passError) },
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { TextButton(onClick = { showPass = !showPass }) { Text(if (showPass) "Ocultar" else "Ver") } },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    emailTouched = true; passTouched = true
                    if (formValid) { Toast.makeText(context, "Sesión iniciada", Toast.LENGTH_SHORT).show(); onLogin() }
                    else { Toast.makeText(context, "Revisa los campos", Toast.LENGTH_SHORT).show() }
                },
                enabled = formValid, modifier = Modifier.fillMaxWidth()
            ) { Text("Ingresar") }

            TextButton(onClick = onGoRegister, modifier = Modifier.fillMaxWidth()) { Text("¿No tienes cuenta? Crear una") }
        }
    }
}
