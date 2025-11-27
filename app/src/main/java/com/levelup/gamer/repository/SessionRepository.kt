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
    private val api: UsuarioApi
) {

    companion object {
        val KEY_USER_ID = longPreferencesKey("user_id")
        val KEY_NAME = stringPreferencesKey("name")
        val KEY_EMAIL = stringPreferencesKey("email")
        val KEY_AGE = intPreferencesKey("age")
        val KEY_DUOC = booleanPreferencesKey("duoc")
        val KEY_PUNTOS = intPreferencesKey("puntos")
        val KEY_NIVEL = intPreferencesKey("nivel")
        val KEY_REFERIDO = stringPreferencesKey("referido")
        val KEY_PHOTO = stringPreferencesKey("photo_uri")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[KEY_EMAIL] != null }
    val nombre: Flow<String> = context.dataStore.data.map { it[KEY_NAME] ?: "" }
    val email: Flow<String> = context.dataStore.data.map { it[KEY_EMAIL] ?: "" }
    val edad: Flow<Int> = context.dataStore.data.map { it[KEY_AGE] ?: 0 }
    val esDuoc: Flow<Boolean> = context.dataStore.data.map { it[KEY_DUOC] ?: false }
    val puntos: Flow<Int> = context.dataStore.data.map { it[KEY_PUNTOS] ?: 0 }
    val nivel: Flow<Int> = context.dataStore.data.map { it[KEY_NIVEL] ?: 1 }
    val referidoPor: Flow<String?> = context.dataStore.data.map { it[KEY_REFERIDO] }
    val photo: Flow<String?> = context.dataStore.data.map { it[KEY_PHOTO] }

    // -------------------------------
    // LOGIN REAL
    // -------------------------------
    suspend fun login(email: String, password: String): Boolean {
        return try {
            val body = mapOf("email" to email, "password" to password)
            val response = api.login(body)

            if (!response.isSuccessful || response.body() == null) return false

            saveUserInPrefs(response.body()!!)
            true

        } catch (e: Exception) {
            false
        }
    }

    // -------------------------------
    // REGISTER REAL
    // -------------------------------
    suspend fun register(user: Usuario, password: String): Boolean {
        return try {
            val dto = UsuarioDto(
                id = null,
                nombre = user.nombre,
                email = user.email,
                edad = user.edad,
                password = password,
                esDuoc = user.esDuoc,
                puntos = user.puntos,
                nivel = user.nivel,
                referidoPor = user.referidoPor
            )

            val response = api.register(dto)

            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    // -------------------------------
    // GUARDAR USUARIO DEL BACKEND
    // -------------------------------
    private suspend fun saveUserInPrefs(user: Usuario) {
        context.dataStore.edit { p ->
            p[KEY_USER_ID] = user.id ?: 0L
            p[KEY_NAME] = user.nombre
            p[KEY_EMAIL] = user.email
            p[KEY_AGE] = user.edad
            p[KEY_DUOC] = user.esDuoc
            p[KEY_PUNTOS] = user.puntos
            p[KEY_NIVEL] = user.nivel

            user.referidoPor?.let { p[KEY_REFERIDO] = it }
        }
    }

    suspend fun currentUserId(): Long? {
        val prefs = context.dataStore.data.first()
        return prefs[KEY_USER_ID]
    }

    suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun addPuntos(delta: Int) {
        context.dataStore.edit { p ->
            val actual = p[KEY_PUNTOS] ?: 0
            val total = (actual + delta).coerceAtLeast(0)
            p[KEY_PUNTOS] = total
            p[KEY_NIVEL] = calcNivel(total)
        }
    }

    suspend fun setPhoto(uri: String) {
        context.dataStore.edit { p ->
            p[KEY_PHOTO] = uri
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
