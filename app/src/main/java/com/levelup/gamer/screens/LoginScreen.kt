package com.levelup.gamer.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.levelup.gamer.repository.SessionRepository
import com.levelup.gamer.viewmodel.UsuarioVM

@Composable
fun LoginScreen(
    onGoRegister: () -> Unit,
    onGoCart: () -> Unit,
    onLogin: () -> Unit // Callback para cuando el login es exitoso
) {
    val context = LocalContext.current

    // 1. Configuramos la inyección de dependencias manual
    val factory = remember {
        // Creamos el repositorio pasando solo el Contexto
        val repository = SessionRepository(context)
        // Creamos la Factory del ViewModel
        UsuarioVM.Factory(repository)
    }

    // 2. Obtenemos el ViewModel usando la factory
    val vm: UsuarioVM = viewModel(factory = factory)

    // Estados locales para los campos de texto
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Iniciar sesión", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        // Campo para el correo electrónico
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo para la contraseña
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para iniciar sesión
        Button(
            onClick = {
                vm.login(email.value, password.value) { ok ->
                    if (ok) {
                        // Si el login es correcto, ejecutamos el callback
                        onLogin()
                    } else {
                        Toast.makeText(
                            context,
                            "Correo o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        Spacer(Modifier.height(16.dp))

        // Botón para ir a la pantalla de registro
        TextButton(onClick = { onGoRegister() }) {
            Text("¿No tienes cuenta? Regístrate aquí")
        }
    }
}