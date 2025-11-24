package com.levelup.gamer.api

import com.levelup.gamer.model.UsuarioDto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UsuarioApi {
    @POST("api/usuarios/register")
    suspend fun register(@Body u: UsuarioDto): UsuarioDto

    @POST("api/usuarios/login")
    suspend fun login(@Body u: UsuarioDto): UsuarioDto

    @PUT("api/usuarios/{id}")
    suspend fun update(@Path("id") id: Long, @Body u: UsuarioDto): UsuarioDto
}
