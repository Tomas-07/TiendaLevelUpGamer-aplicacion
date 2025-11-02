package com.levelup.gamer.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.levelup.gamer.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("session_prefs")

class SessionRepository(private val context: Context) {

    companion object {
        val KEY_EMAIL = stringPreferencesKey("email")
        val KEY_NAME = stringPreferencesKey("name")
        val KEY_AGE = intPreferencesKey("age")
        val KEY_DUOC = booleanPreferencesKey("duoc")
        val KEY_PUNTOS = intPreferencesKey("puntos")
        val KEY_NIVEL = intPreferencesKey("nivel")
        val KEY_REFERIDO = stringPreferencesKey("referido")
    }


    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[KEY_EMAIL] != null }
    val email: Flow<String> = context.dataStore.data.map { it[KEY_EMAIL] ?: "" }
    val name: Flow<String> = context.dataStore.data.map { it[KEY_NAME] ?: "" }
    val age: Flow<Int> = context.dataStore.data.map { it[KEY_AGE] ?: 0 }
    val esDuoc: Flow<Boolean> = context.dataStore.data.map { it[KEY_DUOC] ?: false }
    val puntos: Flow<Int> = context.dataStore.data.map { it[KEY_PUNTOS] ?: 0 }
    val nivel: Flow<Int> = context.dataStore.data.map { it[KEY_NIVEL] ?: 1 }
    val referidoPor: Flow<String?> = context.dataStore.data.map { it[KEY_REFERIDO] }

    suspend fun login(user: Usuario) {
        context.dataStore.edit { p ->
            p[KEY_EMAIL] = user.email
            p[KEY_NAME] = user.nombre
            p[KEY_AGE] = user.edad
            p[KEY_DUOC] = user.esDuoc
            p[KEY_PUNTOS] = user.puntos
            p[KEY_NIVEL] = user.nivel
            user.referidoPor?.let { p[KEY_REFERIDO] = it }
        }
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

    private fun calcNivel(p: Int): Int = when {
        p >= 1000 -> 5
        p >= 600 -> 4
        p >= 300 -> 3
        p >= 120 -> 2
        else -> 1
    }
}
