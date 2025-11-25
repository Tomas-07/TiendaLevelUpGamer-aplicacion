package com.levelup.gamer.api

import com.levelup.gamer.model.ProductoDto
import retrofit2.http.*

interface ProductoApi {
    @GET("api/productos")
    suspend fun getProductos(): List<ProductoDto>

    @GET("api/productos/{id}")
    suspend fun getProducto(@Path("id") id: Long): ProductoDto

    @POST("api/productos")
    suspend fun create(@Body p: ProductoDto): ProductoDto

    @PUT("api/productos/{id}")
    suspend fun update(@Path("id") id: Long, @Body p: ProductoDto): ProductoDto

    @DELETE("api/productos/{id}")
    suspend fun delete(@Path("id") id: Long)
}
