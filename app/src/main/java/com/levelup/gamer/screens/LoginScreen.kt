@file:OptIn(ExperimentalMaterial3Api::class)

package com.levelup.gamer.screens

import android.widget.Toast
import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.levelup.gamer.ui.deps

@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onGoRegister: () -> Unit
) {
    val d = deps()
    val context = LocalContext.current

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var showPass by remember { mutableStateOf(false) }

    var showEmailError by remember { mutableStateOf(false) }
    var showPassError by remember { mutableStateOf(false) }

    val correo = email.text.trim()
    val pass = password.text.trim()

    val isEmailValid = correo.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    val isPassValid = pass.length >= 4

    val isFormValid = isEmailValid && isPassValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Level-Up Gamer", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(20.dp))


        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (showEmailError) showEmailError = false
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = showEmailError || (correo.isNotEmpty() && !isEmailValid),
            supportingText = {
                if (showEmailError || (correo.isNotEmpty() && !isEmailValid)) {
                    Text("Correo inválido (ej: nombre@dominio.com)")
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (showPassError) showPassError = false
            },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            isError = showPassError || (pass.isNotEmpty() && !isPassValid),
            supportingText = {
                if (showPassError || (pass.isNotEmpty() && !isPassValid)) {
                    Text("La contraseña debe tener al menos 4 caracteres")
                }
            },
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { showPass = !showPass }) {
                    Text(if (showPass) "Ocultar" else "Ver")
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))


        Button(
            onClick = {
                if (!isEmailValid) showEmailError = true
                if (!isPassValid) showPassError = true
                if (!isFormValid) return@Button

                d.usuarioVM.login(
                    email = correo,
                    password = pass
                ) { ok ->
                    if (ok) {
                        onLogin()
                    } else {
                        Toast.makeText(context, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid
        ) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onGoRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear cuenta (18+)")
        }
    }
}
