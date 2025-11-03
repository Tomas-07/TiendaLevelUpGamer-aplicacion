package com.levelup.gamer.viewmodel

import androidx.lifecycle.ViewModel
import com.levelup.gamer.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class CartItem(val producto: Producto, val cantidad: Int)

class CarritoVM : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items

    fun add(p: Producto) {
        val cur = _items.value.toMutableList()
        val i = cur.indexOfFirst { it.producto.codigo == p.codigo }
        if (i >= 0) cur[i] = cur[i].copy(cantidad = cur[i].cantidad + 1)
        else cur.add(CartItem(p, 1))
        _items.value = cur
    }

    fun dec(p: Producto) {
        val cur = _items.value.toMutableList()
        val i = cur.indexOfFirst { it.producto.codigo == p.codigo }
        if (i >= 0) {
            val nueva = cur[i].cantidad - 1
            if (nueva <= 0) cur.removeAt(i) else cur[i] = cur[i].copy(cantidad = nueva)
            _items.value = cur
        }
    }

    fun remove(p: Producto) {
        _items.value = _items.value.filterNot { it.producto.codigo == p.codigo }
    }

    fun clear() { _items.value = emptyList() }

    fun count(): Int = _items.value.sumOf { it.cantidad }

    fun subtotal(): Int = _items.value.sumOf { it.producto.precio * it.cantidad }
}
