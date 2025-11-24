@file:OptIn(ExperimentalMaterial3Api::class)

package com.levelup.gamer.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.levelup.gamer.ui.deps
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onGoRegister: () -> Unit
) {
    val d = deps()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val emailValid = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val passwordValid = password.isNotBlank()

    val formValid = emailValid && passwordValid

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Level-Up Gamer", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = !emailValid && email.isNotBlank(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            isError = password.isNotBlank() && !passwordValid,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (!formValid) return@Button
                scope.launch {
                    val ok = d.usuarioVM.login(email, password)
                    if (ok) {
                        onLogin()
                    } else {
                        Toast.makeText(context, "Correo o contraseña inválidos", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = formValid
        ) {
            Text("Ingresar")
        }

        TextButton(onClick = onGoRegister) {
            Text("Crear cuenta (solo mayores de 18 años)")
        }
    }
}
