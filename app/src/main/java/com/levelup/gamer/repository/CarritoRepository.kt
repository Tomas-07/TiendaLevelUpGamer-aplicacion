package com.levelup.gamer.repository

import com.levelup.gamer.api.CarritoApi
import com.levelup.gamer.model.CarritoItemDto
import com.levelup.gamer.model.Producto

class CarritoRepository(private val api: CarritoApi) {

    suspend fun listar(usuarioId: Long) =
        api.getCarrito(usuarioId)

    suspend fun agregar(usuarioId: Long, producto: Producto, cantidad: Int) {
        // Creamos el DTO para enviar al backend
        val itemDto = CarritoItemDto(
            id = null, // Es nuevo, el ID lo genera la BD
            usuarioId = usuarioId,
            productoId = producto.id,
            cantidad = cantidad
        )
        api.add(itemDto)
    }

    suspend fun actualizarCantidad(itemId: Long, cantidad: Int) {
        // Para actualizar, usualmente solo importa el ID y la nueva cantidad
        val itemDto = CarritoItemDto(
            id = itemId,
            usuarioId = null,
            productoId = null,
            cantidad = cantidad
        )
        api.updateCantidad(itemId, itemDto)
    }

    suspend fun eliminar(itemId: Long) =
        api.delete(itemId)

    suspend fun vaciar(usuarioId: Long) =
        api.vaciar(usuarioId)
}