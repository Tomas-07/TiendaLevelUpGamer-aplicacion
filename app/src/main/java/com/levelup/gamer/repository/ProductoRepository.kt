package com.levelup.gamer.repository

import com.levelup.gamer.api.ProductoApi
import com.levelup.gamer.model.Producto

class ProductoRepository(
    private val api: ProductoApi
) {

    suspend fun all(): List<Producto> = api.listar()

    suspend fun get(id: Long): Producto = api.obtener(id)

    suspend fun crear(p: Producto): Producto = api.crear(p)

    suspend fun actualizar(id: Long, p: Producto): Producto = api.actualizar(id, p)

    suspend fun eliminar(id: Long) = api.eliminar(id)
}
