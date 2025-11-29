package com.levelup.gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.levelup.gamer.model.Producto
import com.levelup.gamer.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductoVM(private val productoRepository: ProductoRepository) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> get() = _productos

    init {
        loadProductos()
    }

    private fun loadProductos() {
        viewModelScope.launch {
            try {
                val productosList = productoRepository.all()

                // CORRECCIÓN: Filtramos por ID para asegurar que no haya repetidos
                val listaUnica = productosList.distinctBy { it.id }

                _productos.value = listaUnica
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getProductoById(id: Long): Producto? {
        return try {
            productoRepository.get(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    class Factory(private val productoRepository: ProductoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            if (modelClass.isAssignableFrom(ProductoVM::class.java)) {
                return ProductoVM(productoRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}