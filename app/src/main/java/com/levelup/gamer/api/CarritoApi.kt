package com.levelup.gamer.api

import com.levelup.gamer.model.CarritoItemDto
import retrofit2.http.*

interface CarritoApi {

    @GET("api/carrito/{usuarioId}")
    suspend fun getCarrito(@Path("usuarioId") userId: Long): List<CarritoItemDto>

    @POST("api/carrito/add")
    suspend fun add(@Body item: CarritoItemDto): CarritoItemDto

    @PUT("api/carrito/update/{id}")
    suspend fun updateCantidad(
        @Path("id") id: Long,
        @Body item: CarritoItemDto
    ): CarritoItemDto

    @DELETE("api/carrito/delete/{id}")
    suspend fun delete(@Path("id") id: Long)

    @DELETE("api/carrito/vaciar/{usuarioId}")
    suspend fun vaciar(@Path("usuarioId") userId: Long)
}
