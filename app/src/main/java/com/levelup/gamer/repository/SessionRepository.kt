package com.levelup.gamer.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.levelup.gamer.api.UsuarioApi
import com.levelup.gamer.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("levelup_prefs")

class SessionRepository(private val context: Context, usuarioApi: UsuarioApi) {

    companion object {
        val KEY_USER_ID = longPreferencesKey("user_id")     // << NUEVO
        val KEY_NAME = stringPreferencesKey("name")
        val KEY_EMAIL = stringPreferencesKey("email")
        val KEY_PASSWORD = stringPreferencesKey("password")
        val KEY_AGE = intPreferencesKey("age")
        val KEY_DUOC = booleanPreferencesKey("duoc")
        val KEY_PUNTOS = intPreferencesKey("puntos")
        val KEY_NIVEL = intPreferencesKey("nivel")
        val KEY_REFERIDO = stringPreferencesKey("referido")
        val KEY_PHOTO = stringPreferencesKey("photo_uri")
    }

    // --- Flujos ---
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[KEY_EMAIL] != null }
    val nombre: Flow<String> = context.dataStore.data.map { it[KEY_NAME] ?: "" }
    val email: Flow<String> = context.dataStore.data.map { it[KEY_EMAIL] ?: "" }
    val edad: Flow<Int> = context.dataStore.data.map { it[KEY_AGE] ?: 0 }
    val esDuoc: Flow<Boolean> = context.dataStore.data.map { it[KEY_DUOC] ?: false }
    val puntos: Flow<Int> = context.dataStore.data.map { it[KEY_PUNTOS] ?: 0 }
    val nivel: Flow<Int> = context.dataStore.data.map { it[KEY_NIVEL] ?: 1 }
    val referidoPor: Flow<String?> = context.dataStore.data.map { it[KEY_REFERIDO] }
    val photo: Flow<String?> = context.dataStore.data.map { it[KEY_PHOTO] }
    val userId: Flow<Long> = context.dataStore.data.map { it[KEY_USER_ID] ?: 1L }   // << NUEVO (ID fijo)

    // --- Registro ---
    suspend fun register(usuario: Usuario, password: String) {
        context.dataStore.edit { p ->
            p[KEY_USER_ID] = 1L
            p[KEY_NAME] = usuario.nombre
            p[KEY_EMAIL] = usuario.email
            p[KEY_PASSWORD] = password
            p[KEY_AGE] = usuario.edad
            p[KEY_DUOC] = usuario.esDuoc
            p[KEY_PUNTOS] = usuario.puntos
            p[KEY_NIVEL] = usuario.nivel
            usuario.referidoPor?.let { p[KEY_REFERIDO] = it }
        }
    }

    // --- Login local ---
    suspend fun login(email: String, password: String): Boolean {
        val prefs = context.dataStore.data.first()
        val savedEmail = prefs[KEY_EMAIL]
        val savedPass = prefs[KEY_PASSWORD]
        return savedEmail == email && savedPass == password
    }

    suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun addPuntos(delta: Int) {
        context.dataStore.edit { p ->
            val cur = p[KEY_PUNTOS] ?: 0
            val total = (cur + delta).coerceAtLeast(0)
            p[KEY_PUNTOS] = total
            p[KEY_NIVEL] = calcNivel(total)
        }
    }

    suspend fun setPhoto(uri: String) {
        context.dataStore.edit { p -> p[KEY_PHOTO] = uri }
    }

    private fun calcNivel(p: Int): Int = when {
        p >= 1000 -> 5
        p >= 600 -> 4
        p >= 300 -> 3
        p >= 120 -> 2
        else -> 1
    }
}
