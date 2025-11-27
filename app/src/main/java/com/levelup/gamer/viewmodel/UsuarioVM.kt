package com.levelup.gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val isLoggedIn = session.isLoggedIn.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val nombre   = session.nombre.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val email    = session.email.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val edad     = session.edad.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val esDuoc   = session.esDuoc.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val puntos   = session.puntos.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val nivel    = session.nivel.stateIn(viewModelScope, SharingStarted.Eagerly, 1)
    val referido = session.referidoPor.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val photoUri = session.photo.stateIn(viewModelScope, SharingStarted.Eagerly, null)


    // --------------------------------------------------------------------
    // -------------------------   VALIDACIONES   --------------------------
    // --------------------------------------------------------------------

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
        if (edad <= 0) "La edad debe ser mayor a 0" else null


    // --------------------------------------------------------------------
    // -------------------------   REGISTRO FINAL   ------------------------
    // --------------------------------------------------------------------
    fun registerUser(
        usuario: Usuario,
        password: String,
        confirmPass: String,
        callback: (RegisterResult) -> Unit
    ) {
        viewModelScope.launch {

            // 1) Validaciones de datos
            validarNombre(usuario.nombre)?.let { return@launch callback(RegisterResult.Error(it)) }
            validarEmail(usuario.email)?.let { return@launch callback(RegisterResult.Error(it)) }
            validarEdad(usuario.edad)?.let { return@launch callback(RegisterResult.Error(it)) }
            validarPassword(password)?.let { return@launch callback(RegisterResult.Error(it)) }
            validarConfirmacion(password, confirmPass)?.let { return@launch callback(RegisterResult.Error(it)) }

            // 2) ¿Es correo DUOC?
            val esDuoc = usuario.email.trim().lowercase().endsWith("@duoc.cl")

            // Marcamos si es DUOC antes de registrar
            val usuarioFinal = usuario.copy(esDuoc = esDuoc)

            // 3) Enviar registro al backend
            val ok = session.register(usuarioFinal, password)

            if (!ok) {
                callback(RegisterResult.Error("Error al registrar. El correo podría ya existir."))
                return@launch
            }

            callback(RegisterResult.Success())
        }
    }


    // --------------------------------------------------------------------
    // -----------------------------   LOGIN   -----------------------------
    // --------------------------------------------------------------------
    fun login(email: String, password: String, onResult: (Boolean) -> Unit) =
        viewModelScope.launch {
            val ok = session.login(email, password)
            onResult(ok)
        }


    // --------------------------------------------------------------------
    // -----------------------------   OTROS   -----------------------------
    // --------------------------------------------------------------------
    fun addPuntos(delta: Int) = viewModelScope.launch { session.addPuntos(delta) }

    fun setPhoto(uri: String) = viewModelScope.launch { session.setPhoto(uri) }

    fun logout() = viewModelScope.launch { session.logout() }
}
