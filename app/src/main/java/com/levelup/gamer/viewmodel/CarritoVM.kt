package com.levelup.gamer.viewmodel

import androidx.lifecycle.ViewModel
import com.levelup.gamer.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class ItemCarrito(val producto: Producto, val cantidad: Int)

class CarritoVM: ViewModel() {
    private val _items = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val items: StateFlow<List<ItemCarrito>> = _items

    fun add(p: Producto) {
        val list = _items.value.toMutableList()
        val idx = list.indexOfFirst { it.producto.codigo == p.codigo }
        if (idx >= 0) list[idx] = list[idx].copy(cantidad = list[idx].cantidad + 1)
        else list.add(ItemCarrito(p,1))
        _items.value = list
    }
    fun remove(code: String) { _items.value = _items.value.filter { it.producto.codigo != code } }
    fun update(code: String, qty: Int) {
        val list = _items.value.toMutableList()
        val idx = list.indexOfFirst { it.producto.codigo == code }
        if (idx >= 0) {
            if (qty <= 0) list.removeAt(idx) else list[idx] = list[idx].copy(cantidad = qty)
            _items.value = list
        }
    }
    fun clear() { _items.value = emptyList() }
    fun subtotal(): Int = _items.value.sumOf { it.producto.precio * it.cantidad }
    fun count(): Int = _items.value.sumOf { it.cantidad }
}