package com.levelup.gamer.model


data class CarritoRequest(
    val usuarioId: Long,
    val productoId: Long,
    val cantidad: Int
)