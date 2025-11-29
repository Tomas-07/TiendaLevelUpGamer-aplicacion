package com.levelup.gamer.api

import com.levelup.gamer.model.CarritoItemDto
import retrofit2.http.*

interface CarritoApi {

    // CORREGIDO: Quitamos el "api/" inicial porque ya está en la Base URL

    @GET("carrito/{usuarioId}")
    suspend fun getCarrito(@Path("usuarioId") userId: Long): List<CarritoItemDto>

    @POST("carrito/add")
    suspend fun add(@Body item: CarritoItemDto): CarritoItemDto

    @PUT("carrito/update/{id}")
    suspend fun updateCantidad(
        @Path("id") id: Long,
        @Body item: CarritoItemDto
    ): CarritoItemDto

    @DELETE("carrito/delete/{id}")
    suspend fun delete(@Path("id") id: Long)

    @DELETE("carrito/vaciar/{usuarioId}")
    suspend fun vaciar(@Path("usuarioId") userId: Long)
}