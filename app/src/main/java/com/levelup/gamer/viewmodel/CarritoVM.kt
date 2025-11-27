package com.levelup.gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.gamer.model.Producto
import com.levelup.gamer.repository.CarritoRepository
import com.levelup.gamer.repository.ProductoRepository
import com.levelup.gamer.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CartItem(
    val producto: Producto,
    val cantidad: Int,
    val itemId: Long
)

class CarritoVM(
    private val carritoRepo: CarritoRepository,
    private val productoRepo: ProductoRepository,
    private val sessionRepo: SessionRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items

    fun cargar() = viewModelScope.launch {
        val userId = sessionRepo.currentUserId() ?: return@launch

        val remoto = carritoRepo.listar(userId)
        val productos = productoRepo.all()

        _items.value = remoto.mapNotNull { dto ->
            val prod = productos.find { it.id == dto.productoId }
            prod?.let { CartItem(prod, dto.cantidad, dto.id!!) }
        }
    }

    fun add(p: Producto) = viewModelScope.launch {
        val userId = sessionRepo.currentUserId() ?: return@launch

        val existing = _items.value.find { it.producto.id == p.id }

        if (existing != null) {
            carritoRepo.actualizarCantidad(existing.itemId, existing.cantidad + 1)
        } else {
            carritoRepo.agregar(userId, p, 1)
        }

        cargar()
    }

    fun dec(p: Producto) = viewModelScope.launch {
        val existing = _items.value.find { it.producto.id == p.id } ?: return@launch

        val newQty = existing.cantidad - 1

        when {
            newQty <= 0 -> carritoRepo.eliminar(existing.itemId)
            else        -> carritoRepo.actualizarCantidad(existing.itemId, newQty)
        }

        cargar()
    }

    fun remove(item: CartItem) = viewModelScope.launch {
        carritoRepo.eliminar(item.itemId)
        cargar()
    }

    fun clear() = viewModelScope.launch {
        val userId = sessionRepo.currentUserId() ?: return@launch
        carritoRepo.vaciar(userId)
        cargar()
    }

    fun count(): Int = _items.value.sumOf { it.cantidad }
    fun subtotal(): Int = _items.value.sumOf { it.producto.precio * it.cantidad }
}
