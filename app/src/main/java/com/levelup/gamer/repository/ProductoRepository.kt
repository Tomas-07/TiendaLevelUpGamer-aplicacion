package com.levelup.gamer.repository

import com.levelup.gamer.model.Producto
import com.levelup.gamer.remote.ProductoApiService


class ProductoRepository(
    private val productoApiService: ProductoApiService
) {

    suspend fun getAllProductos(): List<Producto> {

        return productoApiService.getProductos()
    }
}
