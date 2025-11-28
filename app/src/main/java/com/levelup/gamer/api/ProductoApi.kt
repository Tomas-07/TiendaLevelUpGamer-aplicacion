package com.levelup.gamer.api

import com.levelup.gamer.model.Producto
import retrofit2.http.*

interface ProductoApi {

    @GET("api/productos")
    suspend fun listar(): List<Producto>

    @GET("api/productos/{id}")
    suspend fun obtener(@Path("id") id: Long): Producto

    @POST("api/productos")
    suspend fun crear(@Body p: Producto): Producto

    @PUT("api/productos/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body p: Producto): Producto

    @DELETE("api/productos/{id}")
    suspend fun eliminar(@Path("id") id: Long)
}
