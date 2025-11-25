package com.levelup.gamer.repository

import com.levelup.gamer.api.ProductoApi
import com.levelup.gamer.model.Producto
import com.levelup.gamer.model.ProductoDto

class ProductoRepository(private val api: ProductoApi) {

    private var productos: List<Producto> = emptyList()

    suspend fun load() {
        val remote = api.getProductos()
        productos = remote.map { it.toModel() }
    }

    fun all(): List<Producto> = productos

    fun byCodigo(cod: String): Producto? =
        productos.find { it.codigo == cod }

    fun byCategoria(cat: String): List<Producto> =
        productos.filter { it.categoria.equals(cat, true) }

    fun search(query: String): List<Producto> =
        productos.filter { it.nombre.contains(query, true) || it.categoria.contains(query, true) }

    fun filter(cat: String?, min: Int?, max: Int?): List<Producto> =
        productos.filter { p ->
            (cat == null || p.categoria.equals(cat, true)) &&
                    (min == null || p.precio >= min) &&
                    (max == null || p.precio <= max)
        }
}

private fun ProductoDto.toModel() = Producto(
    codigo = codigo,
    categoria = categoria,
    nombre = nombre,
    precio = precio,
    imagen = imagen,
    descripcion = descripcion,
    stock = stock,
    destacado = destacado
)

