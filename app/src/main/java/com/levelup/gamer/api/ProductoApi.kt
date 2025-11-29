package com.levelup.gamer.api

import com.levelup.gamer.model.Producto
import retrofit2.http.*

interface ProductoApi {

    // CORREGIDO: Quitamos el "api/" porque ya está en el RetrofitClient
    @GET("productos")
    suspend fun listar(): List<Producto>

    @GET("productos/{id}")
    suspend fun obtener(@Path("id") id: Long): Producto

    @POST("productos")
    suspend fun crear(@Body p: Producto): Producto

    @PUT("productos/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body p: Producto): Producto

    @DELETE("productos/{id}")
    suspend fun eliminar(@Path("id") id: Long)
}