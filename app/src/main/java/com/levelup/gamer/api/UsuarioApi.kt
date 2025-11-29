package com.levelup.gamer.api

import com.levelup.gamer.model.Usuario
import com.levelup.gamer.model.UsuarioDto
import retrofit2.Response
import retrofit2.http.*

interface UsuarioApi {

    // Registro con UsuarioDto (full body)
    // CORREGIDO: Quitamos el "api/" del inicio
    @POST("usuarios/registrar")
    suspend fun register(@Body dto: UsuarioDto): Response<Usuario>

    // Login: solo email + password
    // CORREGIDO: Quitamos el "api/" del inicio
    @POST("usuarios/login")
    suspend fun login(@Body body: Map<String, String>): Response<Usuario>

    // Obtener usuario
    // CORREGIDO: Quitamos el "api/" del inicio
    @GET("usuarios/{id}")
    suspend fun obtener(@Path("id") id: Long): Response<Usuario>

    // Actualizar usuario
    // CORREGIDO: Quitamos el "api/" del inicio
    @PUT("usuarios/{id}")
    suspend fun update(@Path("id") id: Long, @Body dto: UsuarioDto): Response<Usuario>
}