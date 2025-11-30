package com.levelup.gamer.repository

import com.levelup.gamer.model.Producto
import com.levelup.gamer.remote.RetrofitClient

class ProductoRepository {

    // Usamos el cliente de Retrofit centralizado para obtener la instancia del servicio
    private val productoApiService = RetrofitClient.productoApi

    /**
     * Obtiene todos los productos desde el servidor.
     * Llama a la función getProductos() definida en ProductoApiService.
     */
    suspend fun getAllProductos(): List<Producto> {
        return productoApiService.getProductos()
    }

    // Aquí puedes añadir más funciones del repositorio si las necesitas
    // Por ejemplo, para obtener un producto por ID, etc.
    // suspend fun getProductoById(id: Long): Producto {
    //     return productoApiService.getProducto(id) // (Necesitarías añadir getProducto a tu ApiService)
    // }
}
