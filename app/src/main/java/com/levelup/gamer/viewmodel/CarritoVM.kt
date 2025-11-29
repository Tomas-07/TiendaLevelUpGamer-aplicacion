package com.levelup.gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
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

        try {
            val remoto = carritoRepo.listar(userId)
            val productos = productoRepo.all()

            _items.value = remoto.mapNotNull { dto ->
                val prod = productos.find { it.id == dto.productoId }
                prod?.let { CartItem(prod, dto.cantidad, dto.id!!) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun add(p: Producto) = viewModelScope.launch {
        val userId = sessionRepo.currentUserId() ?: return@launch

        try {
            val existing = _items.value.find { it.producto.id == p.id }

            if (existing != null) {
                carritoRepo.actualizarCantidad(existing.itemId, existing.cantidad + 1)
            } else {
                carritoRepo.agregar(userId, p, 1)
            }
            cargar()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dec(p: Producto) = viewModelScope.launch {
        val existing = _items.value.find { it.producto.id == p.id } ?: return@launch

        try {
            val newQty = existing.cantidad - 1
            when {
                newQty <= 0 -> carritoRepo.eliminar(existing.itemId)
                else        -> carritoRepo.actualizarCantidad(existing.itemId, newQty)
            }
            cargar()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun remove(item: CartItem) = viewModelScope.launch {
        try {
            carritoRepo.eliminar(item.itemId)
            cargar()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clear() = viewModelScope.launch {
        val userId = sessionRepo.currentUserId() ?: return@launch
        try {
            carritoRepo.vaciar(userId)
            cargar()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun count(): Int = _items.value.sumOf { it.cantidad }
    fun subtotal(): Int = _items.value.sumOf { it.producto.precio * it.cantidad }

    // FACTORY PARA INYECCIÓN DE DEPENDENCIAS
    class Factory(
        private val carritoRepo: CarritoRepository,
        private val productoRepo: ProductoRepository,
        private val sessionRepo: SessionRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            if (modelClass.isAssignableFrom(CarritoVM::class.java)) {
                return CarritoVM(carritoRepo, productoRepo, sessionRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}