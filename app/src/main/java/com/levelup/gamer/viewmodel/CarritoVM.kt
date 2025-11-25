package com.levelup.gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.gamer.model.Producto
import com.levelup.gamer.repository.CarritoRepository
import com.levelup.gamer.repository.ProductoRepository
import com.levelup.gamer.repository.SessionRepository
import com.levelup.gamer.utils.codigoToId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CartItem(val producto: Producto, val cantidad: Int, val itemId: Long)

class CarritoVM(
    private val carritoRepo: CarritoRepository,
    private val productoRepo: ProductoRepository,
    private val sessionRepo: SessionRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items

    fun cargar() = viewModelScope.launch {
        val userId = sessionRepo.currentUserId() ?: return@launch
        val remote = carritoRepo.listar(userId)

        // asegúrate de tener productos cargados antes
        val productos = productoRepo.all()

        _items.value = remote.mapNotNull { dto ->
            val prod = productos.find { codigoToId(it.codigo) == dto.productoId }
            prod?.let { CartItem(it, dto.cantidad, dto.id!!) }
        }

    }

    fun add(p: Producto) = viewModelScope.launch {
        val userId = sessionRepo.currentUserId() ?: return@launch

        carritoRepo.agregar(userId, p, 1)

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
