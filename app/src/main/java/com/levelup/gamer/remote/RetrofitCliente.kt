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

        .baseUrl("http://13.222.9.63:9090/api/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val productoApi: ProductoApiService = retrofit.create(ProductoApiService::class.java)
}
