package com.levelup.gamer.repository

import com.levelup.gamer.api.ProductoApi
import com.levelup.gamer.model.Producto
import com.levelup.gamer.model.ProductoDto

class ProductoRepository(
    private val api: ProductoApi
) {
    private var productos: List<Producto> = emptyList()

    suspend fun load() {
        try {
            val remote = api.getProductos()
            productos = remote.map { it.toModel() }
        } catch (e: Exception) {
            productos = emptyList()
            throw e
        }
    }

    fun all(): List<Producto> = productos

    fun byCodigo(codigo: String): Producto? =
        productos.firstOrNull { it.codigo == codigo }
}

private fun ProductoDto.toModel() = Producto(
    id = id ?: 0L,
    codigo = codigo,
    categoria = categoria,
    nombre = nombre,
    precio = precio,
    imagen = imagen,
    descripcion = descripcion,
    stock = stock,
    destacado = destacado
)
