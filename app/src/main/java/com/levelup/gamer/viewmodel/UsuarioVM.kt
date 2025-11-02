package com.levelup.gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.gamer.model.Usuario
import com.levelup.gamer.repository.SessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsuarioVM(private val repo: SessionRepository) : ViewModel() {

    val isLoggedIn = repo.isLoggedIn.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val email = repo.email.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val nombre = repo.name.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val esDuoc = repo.esDuoc.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val puntos = repo.puntos.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val nivel  = repo.nivel.stateIn(viewModelScope, SharingStarted.Eagerly, 1)

    fun login(usuario: Usuario) = viewModelScope.launch { repo.login(usuario) }
    fun logout() = viewModelScope.launch { repo.logout() }
    fun addPuntos(delta: Int) = viewModelScope.launch { repo.addPuntos(delta) }
}
