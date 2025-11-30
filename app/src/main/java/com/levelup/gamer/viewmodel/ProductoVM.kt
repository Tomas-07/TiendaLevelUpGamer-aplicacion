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
        // Carga inicial de todos los productos
        viewModelScope.launch {
            try {
                // Usamos el nuevo método del repositorio
                _productos.value = repo.getAllProductos()
            } catch (e: Exception) {
                // Manejar el error, por ejemplo, logueándolo
                println("Error al cargar productos: ${e.message}")
            }
        }
    }

    /**
     * Busca un producto por su ID en la lista ya cargada.
     * Si no está cargada, la obtiene del repositorio primero.
     */
    suspend fun getProductoById(id: Long): Producto? {
        // Si la lista de productos aún no se ha cargado, la cargamos primero
        if (_productos.value.isEmpty()) {
            try {
                _productos.value = repo.getAllProductos()
            } catch (e: Exception) {
                println("Error al cargar productos para búsqueda por ID: ${e.message}")
                return null // Retorna null si hay un error al cargar
            }
        }
        // Busca el producto en la lista ya cargada en memoria
        return _productos.value.find { it.id == id }
    }


    // Factory para crear la instancia del ViewModel con dependencias
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