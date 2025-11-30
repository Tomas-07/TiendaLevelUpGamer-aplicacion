// app/src/main/java/com/levelup/gamer/remote/RetrofitClient.kt

package com.levelup.gamer.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Configura el interceptor de logging para depuración
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Crea el cliente OkHttp y añade el interceptor
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Crea la instancia de Retrofit
    val retrofit: Retrofit = Retrofit.Builder()
        // ¡IMPORTANTE! Esta es la IP pública de tu servidor AWS
        .baseUrl("http://98.85.26.7:3000/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Expone la API Service para ser usada en el resto de la app
    val productoApi: ProductoApiService = retrofit.create(ProductoApiService::class.java)
}
