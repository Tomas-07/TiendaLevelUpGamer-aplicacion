package com.levelup.gamer.model

data class UsuarioDto(
    val id: Long? = null,
    val nombre: String,
    val email: String,
    val edad: Int,
    val password: String,
    val esDuoc: Boolean = false,
    val puntos: Int = 0,
    val nivel: Int = 1,
    val referidoPor: String? = null
)
