package com.levelup.gamer.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.levelup.gamer.api.UsuarioApi
import com.levelup.gamer.model.Usuario
import com.levelup.gamer.model.UsuarioDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("levelup_prefs")

class SessionRepository(
    private val context: Context,
    private val usuarioApi: UsuarioApi
) {

    companion object {
        val KEY_ID = longPreferencesKey("user_id")
        val KEY_NAME = stringPreferencesKey("name")
        val KEY_EMAIL = stringPreferencesKey("email")
        val KEY_EDAD = intPreferencesKey("edad")
        val KEY_REFERIDO = stringPreferencesKey("referido")
        val KEY_PUNTOS = intPreferencesKey("puntos")
        val KEY_NIVEL = intPreferencesKey("nivel")
        val KEY_DUOC = booleanPreferencesKey("es_duoc")
        val KEY_PHOTO = stringPreferencesKey("photo")
        val KEY_LOGGED = booleanPreferencesKey("logged")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[KEY_LOGGED] ?: false }
    val nombre: Flow<String> = context.dataStore.data.map { it[KEY_NAME] ?: "" }
    val email: Flow<String> = context.dataStore.data.map { it[KEY_EMAIL] ?: "" }
    val edad: Flow<Int> = context.dataStore.data.map { it[KEY_EDAD] ?: 0 }
    val referidoPor: Flow<String?> = context.dataStore.data.map { it[KEY_REFERIDO] }
    val puntos: Flow<Int> = context.dataStore.data.map { it[KEY_PUNTOS] ?: 0 }
    val nivel: Flow<Int> = context.dataStore.data.map { it[KEY_NIVEL] ?: 1 }
    val esDuoc: Flow<Boolean> = context.dataStore.data.map { it[KEY_DUOC] ?: false }
    val photo: Flow<String> = context.dataStore.data.map { it[KEY_PHOTO] ?: "" }

    suspend fun currentUserId(): Long? =
        context.dataStore.data.first()[KEY_ID]

    // -------- LOGIN BACKEND --------
    suspend fun login(email: String, password: String): Boolean {
        return try {
            val dto = usuarioApi.login(
                UsuarioDto(
                    nombre = "",
                    email = email,
                    edad = 0,
                    password = password
                )
            )
            saveSession(dto)
            true
        } catch (e: Exception) {
            false
        }
    }

    // -------- REGISTER BACKEND --------
    suspend fun register(u: Usuario, password: String): Boolean {
        return try {
            val dto = usuarioApi.register(
                UsuarioDto(
                    nombre = u.nombre,
                    email = u.email,
                    edad = u.edad,
                    referidoPor = u.referidoPor,
                    puntos = u.puntos,
                    nivel = u.nivel,
                    esDuoc = u.esDuoc,
                    password = password
                )
            )
            saveSession(dto) // lo deja logueado al registrar
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addPuntos(delta: Int) {
        val id = currentUserId() ?: return
        val actual = puntos.first() + delta
        val nuevoNivel = calcNivel(actual)

        // actualiza local
        context.dataStore.edit {
            it[KEY_PUNTOS] = actual
            it[KEY_NIVEL] = nuevoNivel
        }

        // sincroniza backend
        try {
            val dto = UsuarioDto(
                id = id,
                nombre = nombre.first(),
                email = email.first(),
                edad = edad.first(),
                referidoPor = referidoPor.first(),
                puntos = actual,
                nivel = nuevoNivel,
                esDuoc = esDuoc.first(),
                password = "" // backend lo ignora si no lo cambias
            )
            usuarioApi.update(id, dto)
        } catch (_: Exception) {}
    }

    suspend fun setPhoto(uri: String) {
        context.dataStore.edit { it[KEY_PHOTO] = uri }
    }

    suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }

    private suspend fun saveSession(dto: UsuarioDto) {
        context.dataStore.edit {
            dto.id?.let { id -> it[KEY_ID] = id }
            it[KEY_NAME] = dto.nombre
            it[KEY_EMAIL] = dto.email
            it[KEY_EDAD] = dto.edad
            dto.referidoPor?.let { r -> it[KEY_REFERIDO] = r }
            it[KEY_PUNTOS] = dto.puntos
            it[KEY_NIVEL] = dto.nivel
            it[KEY_DUOC] = dto.esDuoc
            it[KEY_LOGGED] = true
        }
    }

    private fun calcNivel(p: Int): Int = when {
        p >= 1000 -> 5
        p >= 600 -> 4
        p >= 300 -> 3
        p >= 120 -> 2
        else -> 1
    }
}
