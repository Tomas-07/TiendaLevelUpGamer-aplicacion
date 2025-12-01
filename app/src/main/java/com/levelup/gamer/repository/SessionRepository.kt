package com.levelup.gamer.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.levelup.gamer.api.UsuarioApi
import com.levelup.gamer.model.Usuario
// Adios UsuarioDto import
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal val Context.dataStore by preferencesDataStore("levelup_prefs")

class SessionRepository(
    private val context: Context,
    private val api: UsuarioApi,
    private val dataStore: DataStore<Preferences> = context.dataStore
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

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { it[KEY_EMAIL] != null }
    val nombre: Flow<String> = dataStore.data.map { it[KEY_NAME] ?: "" }
    val email: Flow<String> = dataStore.data.map { it[KEY_EMAIL] ?: "" }
    val edad: Flow<Int> = dataStore.data.map { it[KEY_AGE] ?: 0 }
    val esDuoc: Flow<Boolean> = dataStore.data.map { it[KEY_DUOC] ?: false }
    val puntos: Flow<Int> = dataStore.data.map { it[KEY_PUNTOS] ?: 0 }
    val nivel: Flow<Int> = dataStore.data.map { it[KEY_NIVEL] ?: 1 }
    val referidoPor: Flow<String?> = dataStore.data.map { it[KEY_REFERIDO] }
    val photo: Flow<String?> = dataStore.data.map { it[KEY_PHOTO] }

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val body = mapOf("email" to email, "password" to password)
            val response = api.login(body)

            if (!response.isSuccessful || response.body() == null) {
                Log.e("API_LOGIN", "Login fallido: ${response.code()}")
                return false
            }

            saveUserInPrefs(response.body()!!)
            true
        } catch (e: Exception) {
            Log.e("API_LOGIN", "Error en login", e)
            false
        }
    }

    suspend fun register(user: Usuario, password: String): Boolean {
        return try {

            val usuarioParaEnviar = user.copy(password = password)

            Log.d("API_REGISTER", "Enviando datos: $usuarioParaEnviar")


            val response = api.register(usuarioParaEnviar)

            if (!response.isSuccessful) {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Log.e("API_REGISTER", "Error del servidor (${response.code()}): $errorMsg")
                return false
            }

            true
        } catch (e: Exception) {
            Log.e("API_REGISTER", "Error de conexión o código", e)
            false
        }
    }

    private suspend fun saveUserInPrefs(user: Usuario) {
        dataStore.edit { p ->
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

    suspend fun currentUserId(): Long? = dataStore.data.first()[KEY_USER_ID]

    suspend fun logout() {
        dataStore.edit { it.clear() }
    }

    suspend fun addPuntos(delta: Int) {
        dataStore.edit { p ->
            val actual = p[KEY_PUNTOS] ?: 0
            val total = (actual + delta).coerceAtLeast(0)
            p[KEY_PUNTOS] = total
            p[KEY_NIVEL] = calcNivel(total)
        }
    }

    suspend fun setPhoto(uri: String) {
        dataStore.edit { p -> p[KEY_PHOTO] = uri }
    }

    private fun calcNivel(p: Int): Int = when {
        p >= 1000 -> 5
        p >= 600 -> 4
        p >= 300 -> 3
        p >= 120 -> 2
        else -> 1
    }
}