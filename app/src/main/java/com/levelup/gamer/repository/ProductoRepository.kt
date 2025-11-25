package com.levelup.gamer.repository

import com.levelup.gamer.api.ProductoApi
import com.levelup.gamer.model.Producto
import com.levelup.gamer.model.ProductoDto
import com.levelup.gamer.remote.ProductoApiService
import com.levelup.gamer.remote.RetrofitClient

class ProductoRepository(productoApi: ProductoApi) {
    // Creamos una instancia del servicio de la API usando nuestro Retrofit
    private val apiService = RetrofitClient.retrofit.create(ProductoApiService::class.java)

    private var cachedProductos: List<Producto> = emptyList()

    // El método load ahora hace la llamada a la API
    suspend fun load() {
        try {
            cachedProductos = apiService.getProductos()
        } catch (e: Exception) {
            // Propaga la excepción para que el ViewModel la capture
            throw e
        }
    }

    // El método all devuelve los datos cacheados
    fun all(): List<Producto> {
        return cachedProductos
    }
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


