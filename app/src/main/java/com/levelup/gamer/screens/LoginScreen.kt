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
import com.levelup.gamer.api.UsuarioApi
import com.levelup.gamer.remote.RetrofitClient
import com.levelup.gamer.repository.SessionRepository
import com.levelup.gamer.viewmodel.UsuarioVM

@Composable
fun LoginScreen(
    onGoRegister: () -> Unit,
    onLogin: () -> Unit,
    onGoCart: () -> Unit
) {
    val context = LocalContext.current

    val factory = remember {

        val api = RetrofitClient.retrofit.create(UsuarioApi::class.java)

        val repository = SessionRepository(context, api)

        UsuarioVM.Factory(repository)
    }


    val vm: UsuarioVM = viewModel(factory = factory)


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo para la contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para iniciar sesión
        Button(
            onClick = {
                vm.login(email, password) { ok ->
                    if (ok) {

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


        TextButton(onClick = { onGoRegister() }) {
            Text("¿No tienes cuenta? Regístrate aquí")
        }
    }
}
