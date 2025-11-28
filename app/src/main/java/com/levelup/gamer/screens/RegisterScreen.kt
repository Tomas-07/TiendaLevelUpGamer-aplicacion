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
import com.levelup.gamer.viewmodel.UsuarioVM
import com.levelup.gamer.viewmodel.RegisterResult
import com.levelup.gamer.model.Usuario

@Composable
fun RegisterScreen(
    onGoLogin: () -> Unit,
    onRegister: () -> Unit // Callback para cuando el registro es exitoso
) {
    // Inicializamos el ViewModel
    val vm: UsuarioVM = viewModel()
    val context = LocalContext.current

    // Estados locales
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Crear cuenta", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        // Campo para el nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Campo para el correo electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Campo para la edad
        OutlinedTextField(
            value = edad,
            onValueChange = { edad = it },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Campo para la contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // Campo para confirmar la contraseña
        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Botón para registrar
        Button(
            onClick = {
                val user = Usuario(
                    id = null, // Asumiendo que el ID se genera en Backend o DB
                    nombre = nombre,
                    email = email,
                    edad = edad.toIntOrNull() ?: -1,
                    password = password
                )

                // Llamamos a registerUser del ViewModel
                vm.registerUser(
                    usuario = user,
                    password = password,
                    confirmPass = confirm
                ) { result ->
                    when (result) {
                        is RegisterResult.Success -> {
                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                            onRegister() // Ejecutamos el callback para volver/navegar
                        }
                        is RegisterResult.Error -> {
                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear cuenta")
        }

        Spacer(Modifier.height(12.dp))

        // Botón para ir a la pantalla de Login
        TextButton(onClick = { onGoLogin() }) {
            Text("¿Ya tienes cuenta? Inicia sesión aquí")
        }
    }
}