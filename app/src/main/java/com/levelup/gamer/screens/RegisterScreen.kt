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
fun RegisterScreen(onRegister: () -> Unit = {}, onGoLogin: () -> Unit = {}) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var nombreTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passTouched by remember { mutableStateOf(false) }
    var confirmTouched by remember { mutableStateOf(false) }
    var showPass by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    val nombreError = when {
        !nombreTouched -> null
        nombre.isBlank() -> "Campo obligatorio"
        nombre.length < 2 -> "Muy corto"
        else -> null
    }
    val emailError = when {
        !emailTouched -> null
        email.isBlank() -> "Campo obligatorio"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email inválido"
        else -> null
    }
    fun passOk(p: String) = p.length >= 6 && p.any { it.isDigit() }
    val passError = when {
        !passTouched -> null
        pass.isBlank() -> "Campo obligatorio"
        !passOk(pass) -> "Mínimo 6 y 1 número"
        else -> null
    }
    val confirmError = when {
        !confirmTouched -> null
        confirm.isBlank() -> "Campo obligatorio"
        confirm != pass -> "No coincide con la contraseña"
        else -> null
    }
    val formValid = listOf(nombreError, emailError, passError, confirmError).all { it == null } &&
            nombre.isNotBlank() && email.isNotBlank() && pass.isNotBlank() && confirm.isNotBlank()

    Scaffold(topBar = { TopAppBar(title = { Text("Crear cuenta") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it; nombreTouched = true },
                label = { Text("Nombre") }, isError = nombreError != null,
                supportingText = { if (nombreError != null) Text(nombreError) },
                modifier = Modifier.fillMaxWidth()
            )
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
            OutlinedTextField(
                value = confirm, onValueChange = { confirm = it; confirmTouched = true },
                label = { Text("Confirmar contraseña") }, isError = confirmError != null,
                supportingText = { if (confirmError != null) Text(confirmError) },
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { TextButton(onClick = { showConfirm = !showConfirm }) { Text(if (showConfirm) "Ocultar" else "Ver") } },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    nombreTouched = true; emailTouched = true; passTouched = true; confirmTouched = true
                    if (formValid) { Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show(); onRegister() }
                    else { Toast.makeText(context, "Revisa los campos", Toast.LENGTH_SHORT).show() }
                },
                enabled = formValid, modifier = Modifier.fillMaxWidth()
            ) { Text("Crear cuenta") }

            TextButton(onClick = onGoLogin, modifier = Modifier.fillMaxWidth()) { Text("¿Ya tienes cuenta? Iniciar sesión") }
        }
    }
}
