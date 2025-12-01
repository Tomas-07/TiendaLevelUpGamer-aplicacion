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

// Modelo para la UI (Este sí tiene el Producto completo)
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
                return@launch
            }

            // 1. Obtenemos los IDs del servidor (CarritoResponse)
            val listaRemota = carritoRepo.listar(userId)

            // 2. Obtenemos el catálogo de productos
            val todosLosProductos = productoRepo.getAllProductos()

            // 3. CRUZAMOS LA INFORMACIÓN (Match)
            val listaFinal = listaRemota.mapNotNull { itemRemoto ->
                // Buscamos el producto real usando el ID que mandó el servidor
                val productoReal = todosLosProductos.find { it.id == itemRemoto.productoId }

                if (productoReal != null) {
                    CartItem(
                        producto = productoReal,
                        cantidad = itemRemoto.cantidad,
                        itemId = itemRemoto.id
                    )
                } else {
                    null // Si el producto no existe, lo ignoramos
                }
            }

            _items.value = listaFinal
            Log.d("CarritoVM", "Carrito cargado: ${listaFinal.size} productos")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun add(p: Producto) = viewModelScope.launch {
        val userId = sessionRepo.currentUserId() ?: return@launch
        try {
            // Buscamos si ya está en la lista visual
            val existing = _items.value.find { it.producto.id == p.id }

            if (existing != null) {
                carritoRepo.actualizarCantidad(existing.itemId, existing.cantidad + 1)
            } else {
                carritoRepo.agregar(userId, p, 1)
            }
            // Recargamos todo para ver los cambios
            cargar()
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