package com.levelup.gamer.model

data class UsuarioDto(
    val id: Long? = null,
    val nombre: String,
    val email: String,
    val edad: Int,
    val referidoPor: String? = null,
    val puntos: Int = 0,
    val nivel: Int = 1,
    val esDuoc: Boolean = false,
    val password: String
)
