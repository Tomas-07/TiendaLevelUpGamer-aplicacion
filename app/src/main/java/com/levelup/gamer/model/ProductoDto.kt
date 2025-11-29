package com.levelup.gamer.model

data class ProductoDto(
    val id: Long,
    val codigo: String,
    val categoria: String,
    val nombre: String,
    val precio: Int,
    val imagen: String = "",
    val descripcion: String = "",
    val stock: Int = 0,
    val destacado: Boolean = false,
    var rating: Float = 0f,
    var reviews: Int = 0
)
