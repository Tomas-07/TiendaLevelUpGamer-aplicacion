package com.levelup.gamer.remote

import com.levelup.gamer.model.GameItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface FreeToGameApi {
    // Pedimos juegos de PC tipo "Shooter" para que la lista no sea gigante
    @GET("games?category=shooter")
    suspend fun getGames(): List<GameItem>
}

object GameClient {
    private const val BASE_URL = "https://www.freetogame.com/api/"

    val api: FreeToGameApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FreeToGameApi::class.java)
    }
}