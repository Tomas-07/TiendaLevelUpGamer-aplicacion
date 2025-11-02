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

