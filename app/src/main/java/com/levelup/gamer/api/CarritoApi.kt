package com.levelup.gamer.api

import com.levelup.gamer.model.CarritoRequest
import com.levelup.gamer.model.CarritoResponse
import retrofit2.Response
import retrofit2.http.*

interface CarritoApi {


    @GET("carrito/{userId}")
    suspend fun getCarrito(@Path("userId") userId: Long): List<CarritoResponse>

    @POST("carrito/add")
    suspend fun add(@Body request: CarritoRequest): Response<Any>

    @DELETE("carrito/delete/{id}")
    suspend fun delete(@Path("id") id: Long): Response<Unit>


    @PUT("carrito/update/{id}")
    suspend fun updateQuantity(@Path("id") id: Long, @Body cantidad: Int): Response<Any>


    @DELETE("carrito/vaciar/{userId}")
    suspend fun clearCart(@Path("userId") userId: Long): Response<Unit>
}