package com.levelup.gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.levelup.gamer.model.Usuario
import com.levelup.gamer.repository.SessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class RegisterResult {
    data class Success(val message: String = "Registro exitoso") : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}

class UsuarioVM(private val session: SessionRepository) : ViewModel() {

    // Estados de la sesión (Observables por la UI)
    val isLoggedIn = session.isLoggedIn.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val nombre   = session.nombre.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val email    = session.email.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val edad     = session.edad.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val esDuoc   = session.esDuoc.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val puntos   = session.puntos.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val nivel    = session.nivel.stateIn(viewModelScope, SharingStarted.Eagerly, 1)
    val referido = session.referidoPor.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val photoUri = session.photo.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // --- VALIDACIONES DE NEGOCIO ---

    private fun validarNombre(nombre: String): String? =
        if (nombre.isBlank()) "El nombre no puede estar vacío" else null

    private fun validarEmail(email: String): String? {
        if (email.isBlank()) return "El correo no puede estar vacío"
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "Correo electrónico inválido"
        return null
    }

    private fun validarPassword(pass: String): String? {
        if (pass.length < 6) return "La contraseña debe tener al menos 6 caracteres"
        if (!pass.any { it.isDigit() }) return "La contraseña debe contener al menos un número"
        return null
    }

    private fun validarConfirmacion(pass: String, confirm: String): String? =
        if (pass != confirm) "Las contraseñas no coinciden" else null

    private fun validarEdad(edad: Int): String? =
        if (edad < 18) "Debes ser mayor de 18 años para registrarte" else null

    // --- FUNCIONES PRINCIPALES ---

    fun registerUser(
        usuario: Usuario,
        password: String,
        confirmPass: String,
        callback: (RegisterResult) -> Unit
    ) {
        viewModelScope.launch {
            // 1. Validaciones
            validarNombre(usuario.nombre)?.let { return@launch callback(RegisterResult.Error(it)) }
            validarEmail(usuario.email)?.let { return@launch callback(RegisterResult.Error(it)) }
            validarEdad(usuario.edad)?.let { return@launch callback(RegisterResult.Error(it)) }
            validarPassword(password)?.let { return@launch callback(RegisterResult.Error(it)) }
            validarConfirmacion(password, confirmPass)?.let { return@launch callback(RegisterResult.Error(it)) }

            // 2. Lógica Duoc
            val esDuoc = usuario.email.trim().lowercase().endsWith("@duoc.cl")
            val usuarioFinal = usuario.copy(esDuoc = esDuoc)

            // 3. Llamada al Repo
            val ok = session.register(usuarioFinal, password)

            if (!ok) {
                callback(RegisterResult.Error("Error al registrar. El correo podría ya existir."))
                return@launch
            }

            callback(RegisterResult.Success())
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) =
        viewModelScope.launch {
            val ok = session.login(email, password)
            onResult(ok)
        }

    fun addPuntos(delta: Int) = viewModelScope.launch { session.addPuntos(delta) }

    fun setPhoto(uri: String) = viewModelScope.launch { session.setPhoto(uri) }

    fun logout() = viewModelScope.launch { session.logout() }


    fun eliminarCuenta(onSuccess: () -> Unit) = viewModelScope.launch {
        val exito = session.eliminarCuenta()
        if (exito) {
            onSuccess()
        }
    }

    // --- FACTORY ---
    class Factory(private val sessionRepository: SessionRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            if (modelClass.isAssignableFrom(UsuarioVM::class.java)) {
                return UsuarioVM(sessionRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}