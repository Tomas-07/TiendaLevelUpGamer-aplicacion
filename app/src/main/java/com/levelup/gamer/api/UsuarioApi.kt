package com.levelup.gamer.api

import com.levelup.gamer.model.Usuario
import com.levelup.gamer.model.UsuarioDto
import retrofit2.Response
import retrofit2.http.*

interface UsuarioApi {

    // Registro con UsuarioDto (full body)
    @POST("api/usuarios/registrar")
    suspend fun register(@Body dto: UsuarioDto): Response<Usuario>

    // Login: solo email + password
    @POST("api/usuarios/login")
    suspend fun login(@Body body: Map<String, String>): Response<Usuario>

    // Obtener usuario
    @GET("api/usuarios/{id}")
    suspend fun obtener(@Path("id") id: Long): Response<Usuario>

    // Actualizar usuario
    @PUT("api/usuarios/{id}")
    suspend fun update(@Path("id") id: Long, @Body dto: UsuarioDto): Response<Usuario>
}
