package com.levelup.gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.gamer.model.Producto
import com.levelup.gamer.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductoVM(private val repo: ProductoRepository) : ViewModel() {
    private val _items = MutableStateFlow<List<Producto>>(emptyList())
    val items: StateFlow<List<Producto>> = _items

    fun cargar() = viewModelScope.launch {
        try {
            repo.load()       // suspend
            _items.value = repo.all()
        } catch (e: Exception) {
            e.printStackTrace()
            _items.value = emptyList()
        }
    }
}
