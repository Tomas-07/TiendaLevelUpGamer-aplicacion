package com.levelup.gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.gamer.model.Usuario
import com.levelup.gamer.repository.SessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsuarioVM(private val session: SessionRepository) : ViewModel() {

    val isLogged: StateFlow<Boolean> =
        session.isLoggedIn.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val nombre: StateFlow<String> =
        session.nombre.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val email: StateFlow<String> =
        session.email.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val edad: StateFlow<Int> =
        session.edad.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val puntos: StateFlow<Int> =
        session.puntos.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val nivel: StateFlow<Int> =
        session.nivel.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
    val esDuoc: StateFlow<Boolean> =
        session.esDuoc.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun login(nombre: String, email: String, edad: Int, referido: String?) {

        val correo = email.lowercase()
        val duoc = correo.endsWith("@duoc.cl") || correo.endsWith("@duocuc.cl")

        viewModelScope.launch {
            session.login(
                Usuario(
                    nombre = nombre,
                    email = email,
                    edad = edad,
                    referidoPor = referido,
                    puntos = if (referido?.isNotBlank() == true) 50 else 0,
                    nivel = 1,
                    esDuoc = duoc
                )
            )
        }
    }

    fun logout() {
        viewModelScope.launch { session.logout() }
    }

    fun addPuntos(p: Int) {
        viewModelScope.launch { session.addPuntos(p) }
    }
}
