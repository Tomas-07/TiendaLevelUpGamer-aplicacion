package com.levelup.gamer.model

data class CarritoItemDto(
    val id: Long? = null,
    val usuarioId: Long,
    val productoId: Long,
    val cantidad: Int
)
