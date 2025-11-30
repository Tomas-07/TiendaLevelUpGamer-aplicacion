package com.levelup.gamer.repository

import com.levelup.gamer.api.CarritoApi
import com.levelup.gamer.model.CarritoItemDto
import com.levelup.gamer.model.Producto

class CarritoRepository(private val api: CarritoApi) {

    suspend fun listar(usuarioId: Long) =
        api.getCarrito(usuarioId)

    suspend fun agregar(usuarioId: Long, producto: Producto, cantidad: Int) {

        val itemDto = CarritoItemDto(
            id = null,
            usuarioId = usuarioId,
            productoId = producto.id,
            cantidad = cantidad
        )
        api.add(itemDto)
    }

    suspend fun actualizarCantidad(itemId: Long, cantidad: Int) {

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