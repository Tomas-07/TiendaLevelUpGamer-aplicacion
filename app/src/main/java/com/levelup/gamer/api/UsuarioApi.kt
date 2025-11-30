package com.levelup.gamer.api

import com.levelup.gamer.model.Usuario
import com.levelup.gamer.model.UsuarioDto
import retrofit2.Response
import retrofit2.http.*

interface UsuarioApi {


    @POST("usuarios/registrar")
    suspend fun register(@Body dto: UsuarioDto): Response<Usuario>


    @POST("usuarios/login")
    suspend fun login(@Body body: Map<String, String>): Response<Usuario>


    @GET("usuarios/{id}")
    suspend fun obtener(@Path("id") id: Long): Response<Usuario>


    @PUT("usuarios/{id}")
    suspend fun update(@Path("id") id: Long, @Body dto: UsuarioDto): Response<Usuario>
}