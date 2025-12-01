package com.levelup.gamer.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.levelup.gamer.api.UsuarioApi
import com.levelup.gamer.model.Usuario
import com.levelup.gamer.remote.RetrofitClient
import com.levelup.gamer.repository.SessionRepository
import com.levelup.gamer.viewmodel.RegisterResult
import com.levelup.gamer.viewmodel.UsuarioVM

@Composable
fun RegisterScreen(
    onGoLogin: () -> Unit,
    onRegister: () -> Unit
) {
    val context = LocalContext.current

    val factory = remember {
        val api = RetrofitClient.retrofit.create(UsuarioApi::class.java)
        val repository = SessionRepository(context, api)
        UsuarioVM.Factory(repository)
    }
    val vm: UsuarioVM = viewModel(factory = factory)


    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    // Estados de ERROR (para validaciones visuales en rojo)
    var nombreError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var edadError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }


    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Crear cuenta", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        // 1. NOMBRE
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                if (nombreError != null) nombreError = null
            },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(),
            isError = nombreError != null,
            supportingText = {
                if (nombreError != null) Text(text = nombreError!!, color = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(Modifier.height(8.dp))

        // 2. EMAIL
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (emailError != null) emailError = null
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError != null,
            supportingText = {
                if (emailError != null) Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(Modifier.height(8.dp))

        // 3. EDAD
        OutlinedTextField(
            value = edad,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) {
                    edad = it
                    if (edadError != null) edadError = null
                }
            },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = edadError != null,
            supportingText = {
                if (edadError != null) Text(text = edadError!!, color = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(Modifier.height(8.dp))

        // 4. PASSWORD
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (passwordError != null) passwordError = null
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(Modifier.height(8.dp))

        // 5. CONFIRMAR PASSWORD
        OutlinedTextField(
            value = confirm,
            onValueChange = {
                confirm = it
                if (confirmError != null) confirmError = null
            },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = confirmError != null,
            supportingText = {
                if (confirmError != null) Text(text = confirmError!!, color = MaterialTheme.colorScheme.error)
            }
        )

        Spacer(Modifier.height(24.dp))

        // BOTÓN REGISTRAR
        Button(
            onClick = {
                var isValid = true

                if (nombre.isBlank()) {
                    nombreError = "El nombre es obligatorio"
                    isValid = false
                }

                if (email.isBlank()) {
                    emailError = "El correo es obligatorio"
                    isValid = false
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailError = "Formato de correo inválido"
                    isValid = false
                }

                val edadInt = edad.toIntOrNull()
                if (edadInt == null) {
                    edadError = "Ingresa tu edad"
                    isValid = false
                } else if (edadInt < 18) {
                    edadError = "Debes tener al menos 18 años"
                    isValid = false
                }

                if (password.length < 6) {
                    passwordError = "Mínimo 6 caracteres"
                    isValid = false
                } else if (!password.any { it.isDigit() }) {
                    passwordError = "Debe contener al menos un número"
                    isValid = false
                }

                if (confirm != password) {
                    confirmError = "Las contraseñas no coinciden"
                    isValid = false
                }

                if (!isValid) return@Button

                isLoading = true
                val user = Usuario(
                    id = null,
                    nombre = nombre.trim(),
                    email = email.trim(),
                    edad = edadInt ?: 18,
                    password = password.trim()
                )

                vm.registerUser(
                    usuario = user,
                    password = password,
                    confirmPass = confirm
                ) { result ->
                    isLoading = false
                    when (result) {
                        is RegisterResult.Success -> {
                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                            onRegister()
                        }

                        is RegisterResult.Error -> {
                            Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Crear cuenta")
            }
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = { onGoLogin() }, enabled = !isLoading) {
            Text("¿Ya tienes cuenta? Inicia sesión aquí")
        }
    }
}