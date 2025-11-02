package com.levelup.gamer.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.levelup.gamer.model.Producto
import com.levelup.gamer.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

private val Context.dataStore by preferencesDataStore("levelup_prefs")

class ProductoRepository(private val context: Context) {
    private var productos: List<Producto> = emptyList()

    suspend fun load() {
        if (productos.isNotEmpty()) return
        val json = context.assets.open("products.json").bufferedReader().use { it.readText() }
        val arr = JSONArray(json)
        val list = mutableListOf<Producto>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(
                Producto(
                    codigo = o.getString("codigo"),
                    categoria = o.getString("categoria"),
                    nombre = o.getString("nombre"),
                    precio = o.getInt("precio"),
                    imagen = o.optString("imagen",""),
                    descripcion = o.optString("descripcion",""),
                    stock = o.optInt("stock",0),
                    destacado = o.optBoolean("destacado", false)
                )
            )
        }
        productos = list
    }

    fun all(): List<Producto> = productos

    fun byCategoria(cat: String): List<Producto> = productos.filter { it.categoria.equals(cat, true) }

    fun search(query: String): List<Producto> =
        productos.filter { it.nombre.contains(query, true) || it.categoria.contains(query, true) }

    fun filter(cat: String?, min: Int?, max: Int?): List<Producto> =
        productos.filter { p ->
            (cat == null || p.categoria.equals(cat, true)) &&
            (min == null || p.precio >= min) &&
            (max == null || p.precio <= max)
        }
}
class SessionRepository(private val context: Context) {
    companion object {
        val KEY_NAME = stringPreferencesKey("name")
        val KEY_EMAIL = stringPreferencesKey("email")
        val KEY_AGE = intPreferencesKey("age")
        val KEY_DUOC = booleanPreferencesKey("duoc")
        val KEY_PUNTOS = intPreferencesKey("puntos")
        val KEY_NIVEL = intPreferencesKey("nivel")
        val KEY_REFERIDO = stringPreferencesKey("referido")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[KEY_EMAIL] != null }
    val nombre: Flow<String> = context.dataStore.data.map { it[KEY_NAME] ?: "" }
    val email: Flow<String> = context.dataStore.data.map { it[KEY_EMAIL] ?: "" }
    val edad: Flow<Int> = context.dataStore.data.map { it[KEY_AGE] ?: 0 }
    val esDuoc: Flow<Boolean> = context.dataStore.data.map { it[KEY_DUOC] ?: false }
    val puntos: Flow<Int> = context.dataStore.data.map { it[KEY_PUNTOS] ?: 0 }
    val nivel: Flow<Int> = context.dataStore.data.map { it[KEY_NIVEL] ?: 1 }
    val referidoPor: Flow<String?> = context.dataStore.data.map { it[KEY_REFERIDO] }

    suspend fun login(user: Usuario) {
        context.dataStore.edit { p ->
            p[KEY_NAME] = user.nombre
            p[KEY_EMAIL] = user.email
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

class ReviewRepository(private val context: Context) {
    private val KEY_PREFIX = "review_"
    private val KEY_STARS = "_stars"
    private val KEY_COUNT = "_count"
    private val Context.ds by preferencesDataStore("reviews")

    fun ratingFlow(code: String): Flow<Float> = context.ds.data.map { p ->
        p[floatPreferencesKey(KEY_PREFIX + code + KEY_STARS)] ?: 0f
    }

    fun countFlow(code: String): Flow<Int> = context.ds.data.map { p ->
        p[intPreferencesKey(KEY_PREFIX + code + KEY_COUNT)] ?: 0
    }

    suspend fun addReview(code: String, stars: Float) {
        context.ds.edit { p ->
            val kS = floatPreferencesKey(KEY_PREFIX + code + KEY_STARS)
            val kC = intPreferencesKey(KEY_PREFIX + code + KEY_COUNT)
            val curS = p[kS] ?: 0f
            val curC = p[kC] ?: 0

            val newC = curC + 1
            val newS = (curS * curC + stars) / newC
            p[kS] = newS
            p[kC] = newC
        }
    }
}

