package com.levelup.gamer.model

data class CarritoItem(
    val id: Long,
    val usuarioId: Long? = null,
    val producto: Producto,
    val cantidad: Int
)