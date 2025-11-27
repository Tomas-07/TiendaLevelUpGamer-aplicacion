package com.levelup.gamer.repository

import com.levelup.gamer.api.CarritoApi
import com.levelup.gamer.model.CarritoItemDto
import com.levelup.gamer.model.Producto

class CarritoRepository(private val api: CarritoApi) {

    suspend fun listar(usuarioId: Long) =
        api.getCarrito(usuarioId)

    suspend fun agregar(usuarioId: Long, producto: Producto, cantidad: Int) {

        api.add(
            CarritoItemDto(
                id = null,
                usuarioId = usuarioId,
                productoId = producto.id,      // ← ID REAL DEL PRODUCTO
                cantidad = cantidad
            )
        )
    }

    suspend fun actualizarCantidad(itemId: Long, cantidad: Int) {
        api.updateCantidad(
            id = itemId,
            item = CarritoItemDto(
                id = itemId,
                usuarioId = null,             // el backend NO usa esto en update
                productoId = null,            // idem
                cantidad = cantidad
            )
        )
    }

    suspend fun eliminar(itemId: Long) =
        api.delete(itemId)

    suspend fun vaciar(usuarioId: Long) =
        api.vaciar(usuarioId)
}
