// app/src/main/java/com/levelup/gamer/remote/RetrofitClient.kt

package com.levelup.gamer.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 1. Configura el interceptor de logging
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Muestra toda la información de la petición/respuesta
    }

    // 2. Crea el cliente OkHttp y añade el interceptor
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // 3. Crea la instancia de Retrofit
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:9090/api/") // <-- ¡¡esto despues se cambia con el aws con el video del profe fernando
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
