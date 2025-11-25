package com.levelup.gamer.model

data class ProductoDto(
    val id: Long? = null,
    val codigo: String,
    val categoria: String,
    val nombre: String,
    val precio: Int,
    val imagen: String,
    val descripcion: String,
    val stock: Int,
    val destacado: Boolean
)
