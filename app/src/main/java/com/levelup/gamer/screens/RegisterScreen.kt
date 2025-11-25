@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.levelup.gamer.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.levelup.gamer.model.Usuario
import com.levelup.gamer.ui.deps

@Composable
fun RegisterScreen(
    onRegister: () -> Unit = {},
    onGoLogin: () -> Unit = {}
) {
    val d = deps()
    val context = androidx.compose.ui.platform.LocalContext.current  // ✅ obtener contexto aquí

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }
    var touched by remember { mutableStateOf(false) }
    var showPass by remember { mutableStateOf(false) }

    val nombreError = if (touched && nombre.isBlank()) "Campo obligatorio" else null
    val emailError = when {
        !touched -> null
        email.isBlank() -> "Campo obligatorio"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email inválido"
        else -> null
    }
    val edadError = when {
        !touched -> null
        edad.isBlank() -> "Campo obligatorio"
        edad.toIntOrNull() == null -> "Debe ser un número"
        (edad.toIntOrNull() ?: 0) < 10 -> "Debe tener al menos 10 años"
        else -> null
    }
    val passError = when {
        !touched -> null
        pass.isBlank() -> "Campo obligatorio"
        pass.length < 6 -> "Mínimo 6 caracteres"
        !pass.any { it.isDigit() } -> "Debe incluir un número"
        else -> null
    }
    val pass2Error = when {
        !touched -> null
        pass2.isBlank() -> "Campo obligatorio"
        pass2 != pass -> "Las contraseñas no coinciden"
        else -> null
    }

    val formValid = listOf(nombreError, emailError, edadError, passError, pass2Error).all { it == null }

    Scaffold(topBar = { TopAppBar(title = { Text("Crear cuenta") }) }) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it; touched = true },
                label = { Text("Nombre completo") },
                isError = nombreError != null,
                supportingText = { if (nombreError != null) Text(nombreError, color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email, onValueChange = { email = it; touched = true },
                label = { Text("Email") },
                isError = emailError != null,
                supportingText = { if (emailError != null) Text(emailError, color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = edad, onValueChange = { edad = it; touched = true },
                label = { Text("Edad") },
                isError = edadError != null,
                supportingText = { if (edadError != null) Text(edadError, color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pass, onValueChange = { pass = it; touched = true },
                label = { Text("Contraseña") },
                isError = passError != null,
                supportingText = { if (passError != null) Text(passError, color = MaterialTheme.colorScheme.error) },
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { TextButton(onClick = { showPass = !showPass }) { Text(if (showPass) "Ocultar" else "Ver") } },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pass2, onValueChange = { pass2 = it; touched = true },
                label = { Text("Repite la contraseña") },
                isError = pass2Error != null,
                supportingText = { if (pass2Error != null) Text(pass2Error, color = MaterialTheme.colorScheme.error) },
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    touched = true
                    if (!formValid) return@Button

                    val usuario = Usuario(
                        nombre = nombre,
                        email = email,
                        edad = edad.toInt(),
                        esDuoc = false,
                        puntos = 0,
                        nivel = 1,
                        password = pass,
                        referidoPor = null
                    )

                    d.usuarioVM.register(usuario, pass) {
                        Toast.makeText(context, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()
                        onRegister()
                    }
                },
                enabled = formValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }


            TextButton(onClick = onGoLogin, modifier = Modifier.fillMaxWidth()) {
                Text("¿Ya tienes cuenta? Iniciar sesión")
            }
        }
    }
}
