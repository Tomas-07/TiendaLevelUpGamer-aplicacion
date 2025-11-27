@file:OptIn(ExperimentalMaterial3Api::class)

package com.levelup.gamer.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.levelup.gamer.model.Usuario
import com.levelup.gamer.ui.deps
import com.levelup.gamer.viewmodel.RegisterResult

@Composable
fun RegisterScreen(
    onRegister: () -> Unit,
    onGoLogin: () -> Unit
) {
    val d = deps()
    val usuarioVM = d.usuarioVM
    val ctx = LocalContext.current

    // -----------------------
    // CAMPOS
    // -----------------------
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

    var showPass by remember { mutableStateOf(false) }
    var showPass2 by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }


    // VALIDACIONES

    val nombreError = nombre.isBlank()
    val emailError = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val edadInt = edad.toIntOrNull() ?: -1
    val edadError = edad.isBlank() || edadInt < 18
    val passError = password.length < 6
    val pass2Error = confirmPass != password

    val formValid = !nombreError && !emailError && !edadError && !passError && !pass2Error

    Scaffold(
        topBar = { TopAppBar(title = { Text("Crear Cuenta") }) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // NOMBRE
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                isError = nombreError,
                supportingText = {
                    if (nombreError) Text("Campo obligatorio", color = Color.Red)
                },
                modifier = Modifier.fillMaxWidth()
            )

            // EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                isError = emailError,
                supportingText = {
                    if (emailError) Text("Correo inválido", color = Color.Red)
                },
                modifier = Modifier.fillMaxWidth()
            )

            // EDAD
            OutlinedTextField(
                value = edad,
                onValueChange = { edad = it.filter { c -> c.isDigit() } },
                label = { Text("Edad") },
                isError = edadError,
                supportingText = {
                    if (edadError) Text("Debes ser mayor de 18 años", color = Color.Red)
                },
                modifier = Modifier.fillMaxWidth()
            )

            // PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                isError = passError,
                supportingText = {
                    if (passError) Text("Mínimo 6 caracteres", color = Color.Red)
                },
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showPass = !showPass }) {
                        Text(if (showPass) "Ocultar" else "Ver")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // CONFIRMAR PASSWORD
            OutlinedTextField(
                value = confirmPass,
                onValueChange = { confirmPass = it },
                label = { Text("Repetir contraseña") },
                isError = pass2Error,
                supportingText = {
                    if (pass2Error) Text("Las contraseñas no coinciden", color = Color.Red)
                },
                visualTransformation = if (showPass2) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showPass2 = !showPass2 }) {
                        Text(if (showPass2) "Ocultar" else "Ver")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = formValid && !loading,
                onClick = {
                    if (!formValid) return@Button

                    loading = true

                    val usuario = Usuario(
                        nombre = nombre.trim(),
                        email = email.trim(),
                        edad = edadInt,
                        esDuoc = email.endsWith("@duoc.cl"),
                        puntos = 0,
                        nivel = 1,
                        referidoPor = null,
                        password = password
                    )

                    usuarioVM.registerUser(
                        usuario = usuario,
                        password = password,
                        confirmPass = confirmPass
                    ) { result ->

                        loading = false

                        when (result) {
                            is RegisterResult.Error -> {
                                Toast.makeText(ctx, result.message, Toast.LENGTH_LONG).show()
                            }

                            is RegisterResult.Success -> {
                                Toast.makeText(ctx, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()
                                onRegister()
                            }
                        }
                    }
                }
            ) {
                Text(if (loading) "Creando..." else "Registrarse")
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onGoLogin
            ) { Text("Ya tengo cuenta") }
        }
    }
}
