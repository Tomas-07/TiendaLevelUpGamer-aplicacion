package com.levelup.gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.levelup.gamer.model.Producto
import com.levelup.gamer.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductoVM(
    private val repo: ProductoRepository
) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos = _productos.asStateFlow()

    init {

        viewModelScope.launch {
            try {

                _productos.value = repo.getAllProductos()
            } catch (e: Exception) {

                println("Error al cargar productos: ${e.message}")
            }
        }
    }


    suspend fun getProductoById(id: Long): Producto? {

        if (_productos.value.isEmpty()) {
            try {
                _productos.value = repo.getAllProductos()
            } catch (e: Exception) {
                println("Error al cargar productos para búsqueda por ID: ${e.message}")
                return null
            }
        }

        return _productos.value.find { it.id == id }
    }



    class Factory(private val repo: ProductoRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProductoVM::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProductoVM(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}