

package com.levelup.gamer.remote

import com.levelup.gamer.model.Producto
import retrofit2.http.GET

interface ProductoApiService {
    @GET("productos")
    suspend fun getProductos(): List<Producto>
}
