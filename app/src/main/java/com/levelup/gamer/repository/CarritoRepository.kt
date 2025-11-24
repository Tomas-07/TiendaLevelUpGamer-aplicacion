package com.levelup.gamer.repository

import com.levelup.gamer.api.CarritoApi
import com.levelup.gamer.model.CarritoItemDto
import com.levelup.gamer.model.Producto
import com.levelup.gamer.utils.codigoToId

class CarritoRepository(private val api: CarritoApi) {

    suspend fun listar(usuarioId: Long) = api.getCarrito(usuarioId)

    suspend fun agregar(usuarioId: Long, producto: Producto, cantidad: Int) {
        val id = codigoToId(producto.codigo)

        api.add(
            CarritoItemDto(
                usuarioId = usuarioId,
                productoId = id,
                cantidad = cantidad
            )
        )
    }


    suspend fun eliminar(itemId: Long) = api.delete(itemId)

    suspend fun vaciar(usuarioId: Long) = api.vaciar(usuarioId)
}
