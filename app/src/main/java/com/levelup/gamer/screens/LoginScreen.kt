@file:OptIn(ExperimentalMaterial3Api::class)
package com.levelup.gamer.screens

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.levelup.gamer.ui.deps

@Composable
fun LoginScreen(onLogged: () -> Unit, onGoRegister: () -> Unit) {
    val d = deps()

    var email by remember { mutableStateOf(TextFieldValue("")) }
    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var showEmailError by remember { mutableStateOf(false) }
    var showNombreError by remember { mutableStateOf(false) }

    val correo = email.text.trim()
    val nombreOk = nombre.text.trim()
    val isEmailValid = correo.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    val isNombreValid = nombreOk.isNotEmpty()
    val isFormValid = isEmailValid && isNombreValid

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
            onValueChange = {
                email = it
                if (showEmailError) showEmailError = false
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = showEmailError || (correo.isNotEmpty() && !isEmailValid),
            supportingText = {
                when {
                    showEmailError || (correo.isNotEmpty() && !isEmailValid) ->
                        Text("Correo inválido (ej: nombre@dominio.com)")
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                if (showNombreError) showNombreError = false
            },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = showNombreError || (nombreOk.isEmpty() && nombre.text.isNotEmpty()),
            supportingText = {
                when {
                    showNombreError || (nombreOk.isEmpty() && nombre.text.isNotEmpty()) ->
                        Text("El nombre no puede estar vacío")
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (!isEmailValid) showEmailError = true
                if (!isNombreValid) showNombreError = true
                if (!isFormValid) return@Button

                d.usuarioVM.login(
                    nombreOk,
                    correo,
                    99,
                    null
                )
                onLogged()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormValid
        ) {
            Text("Ingresar")
        }

        TextButton(onClick = onGoRegister) {
            Text("Crear cuenta (18+)")
        }
    }
}