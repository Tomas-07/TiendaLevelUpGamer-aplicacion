package com.levelup.gamer.api

import com.levelup.gamer.model.Usuario
// Borramos el import de UsuarioDto porque ya no existirá
import retrofit2.Response
import retrofit2.http.*

interface UsuarioApi {


    @POST("usuarios/registrar")
    suspend fun register(@Body usuario: Usuario): Response<Usuario>

    @POST("usuarios/login")
    suspend fun login(@Body body: Map<String, String>): Response<Usuario>

    @GET("usuarios/{id}")
    suspend fun obtener(@Path("id") id: Long): Response<Usuario>

    // CAMBIO: Aquí también usamos 'Usuario'
    @PUT("usuarios/{id}")
    suspend fun update(@Path("id") id: Long, @Body usuario: Usuario): Response<Usuario>
}