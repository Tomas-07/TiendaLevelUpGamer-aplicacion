package com.levelup.gamer.repository

import android.util.Log
import com.levelup.gamer.api.CarritoApi
import com.levelup.gamer.model.CarritoRequest
import com.levelup.gamer.model.CarritoResponse
import com.levelup.gamer.model.Producto

class CarritoRepository(private val api: CarritoApi) {


    suspend fun listar(usuarioId: Long): List<CarritoResponse> {
        return try {
            api.getCarrito(usuarioId)
        } catch (e: Exception) {
            Log.e("CARRITO", "Error al listar", e)
            emptyList()
        }
    }

    suspend fun agregar(usuarioId: Long, producto: Producto, cantidad: Int): Boolean {
        return try {
            val request = CarritoRequest(
                usuarioId = usuarioId,
                productoId = producto.id!!,
                cantidad = cantidad
            )
            val response = api.add(request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("CARRITO", "Error al agregar", e)
            false
        }
    }


    suspend fun eliminar(id: Long): Boolean {
        return try { api.delete(id).isSuccessful } catch (e: Exception) { false }
    }
    suspend fun actualizarCantidad(itemId: Long, nueva: Int): Boolean {
        return try { api.updateQuantity(itemId, nueva).isSuccessful } catch (e: Exception) { false }
    }
    suspend fun vaciar(userId: Long): Boolean {
        return try { api.clearCart(userId).isSuccessful } catch (e: Exception) { false }
    }
}