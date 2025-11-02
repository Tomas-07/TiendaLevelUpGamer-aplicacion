package com.levelup.gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.gamer.repository.SessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsuarioVM(private val session: SessionRepository) : ViewModel() {


    val isLoggedIn = session.isLoggedIn.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val nombre   = session.nombre.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val email    = session.email.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val edad     = session.edad.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val esDuoc   = session.esDuoc.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val puntos   = session.puntos.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
    val nivel    = session.nivel.stateIn(viewModelScope, SharingStarted.Eagerly, 1)
    val referido = session.referidoPor.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val photoUri = session.photoUri.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun addPuntos(delta: Int) = viewModelScope.launch { session.addPuntos(delta) }
    fun setPhoto(uri: String) = viewModelScope.launch { session.setPhoto(uri) }
    fun logout()              = viewModelScope.launch { session.logout() }
}
