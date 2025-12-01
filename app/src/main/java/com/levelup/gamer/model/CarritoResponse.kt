package com.levelup.gamer.model


data class CarritoResponse(
    val id: Long,
    val usuarioId: Long,
    val productoId: Long,
    val cantidad: Int
)