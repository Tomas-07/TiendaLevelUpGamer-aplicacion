package com.levelup.gamer.api

import com.levelup.gamer.model.CarritoItemDto
import retrofit2.http.*

interface CarritoApi {

    @GET("api/carrito/usuario/{usuarioId}")
    suspend fun getCarrito(@Path("usuarioId") usuarioId: Long): List<CarritoItemDto>

    @POST("api/carrito")
    suspend fun add(@Body item: CarritoItemDto): CarritoItemDto

    @PUT("api/carrito/{id}")
    suspend fun updateCantidad(@Path("id") id: Long, @Body item: CarritoItemDto): CarritoItemDto

    @DELETE("api/carrito/{id}")
    suspend fun delete(@Path("id") id: Long)

    @DELETE("api/carrito/usuario/{usuarioId}")
    suspend fun vaciar(@Path("usuarioId") usuarioId: Long)
}
