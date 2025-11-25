// app/src/main/java/com/levelup/gamer/remote/ProductoApiService.kt

package com.levelup.gamer.remote

import com.levelup.gamer.model.Producto
import retrofit2.http.GET

interface ProductoApiService {
    @GET("productos") // <-- CAMBIA "productos" por la ruta real de tu endpoint
    suspend fun getProductos(): List<Producto>
}
