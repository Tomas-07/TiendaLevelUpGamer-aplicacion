package com.levelup.gamer.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        // CORRECCIÓN FINAL: Añadido el prefijo "/api/" que espera el backend
        .baseUrl("http://98.85.26.7:9090/api/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val productoApi: ProductoApiService = retrofit.create(ProductoApiService::class.java)
}
