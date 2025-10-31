package com.levelup.gamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.levelup.gamer.model.Producto
import com.levelup.gamer.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductoVM(private val repo: ProductoRepository): ViewModel() {
    private val _items = MutableStateFlow<List<Producto>>(emptyList())
    val items: StateFlow<List<Producto>> = _items

    fun cargar() {
        viewModelScope.launch {
            repo.load()
            _items.value = repo.all()
        }
    }

    fun aplicarFiltros(cat: String?, min: Int?, max: Int?, query: String?) {
        val base = repo.filter(if (cat.isNullOrBlank()) null else cat, min, max)
        _items.value = if (query.isNullOrBlank()) base else base.filter {
            it.nombre.contains(query!!, true)
        }
    }
}