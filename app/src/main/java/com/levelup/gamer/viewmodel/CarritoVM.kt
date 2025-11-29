package com.levelup.gamer.viewmodel

import android.util.Log
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

    init {
        cargar()
    }

    fun cargar() = viewModelScope.launch {
        try {
            val userId = sessionRepo.currentUserId()
            if (userId == null) {
                Log.e("CarritoVM", "Usuario no logueado")
                return@launch
            }

            // 1. Cargar items del carrito (remoto)
            val remoto = carritoRepo.listar(userId)

            // 2. Cargar catálogo completo de productos
            val productos = productoRepo.all()

            // 3. Cruzar información (Match)
            val listaCombinada = remoto.mapNotNull { dto ->
                // Buscamos el producto que coincida con el ID del item del carrito
                // Usamos toLong() para asegurar que la comparación sea correcta
                val prod = productos.find { it.id == dto.productoId }

                if (prod != null) {
                    CartItem(prod, dto.cantidad, dto.id!!)
                } else {
                    null // Si no encuentra el producto, lo ignora
                }
            }

            _items.value = listaCombinada
            Log.d("CarritoVM", "Carrito cargado: ${listaCombinada.size} elementos")

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CarritoVM", "Error cargando carrito: ${e.message}")
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
            cargar() // Recargar lista
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dec(p: Producto) = viewModelScope.launch {
        val existing = _items.value.find { it.producto.id == p.id } ?: return@launch
        try {
            val newQty = existing.cantidad - 1
            if (newQty <= 0) {
                carritoRepo.eliminar(existing.itemId)
            } else {
                carritoRepo.actualizarCantidad(existing.itemId, newQty)
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

    // Factory
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