@file:OptIn(ExperimentalMaterial3Api::class)

package com.levelup.gamer.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.levelup.gamer.viewmodel.ProductoVM
import com.levelup.gamer.ui.deps
import com.levelup.gamer.viewmodel.CarritoVM
import com.levelup.gamer.model.Producto
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import kotlinx.coroutines.delay

@Composable
fun CatalogoScreen(onGoCart: () -> Unit, onGoPerfil: () -> Unit) {
    val d = deps()
    val productos by d.productoVM.items.collectAsState()
    var query by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf<String?>(null) }
    var min by remember { mutableStateOf("") }
    var max by remember { mutableStateOf("") }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Catálogo") }, actions = {
            IconButton(onClick = onGoPerfil) { Icon(Icons.Filled.Person, contentDescription = "Perfil") }
            IconButton(onClick = onGoCart) {
                BadgedBox(
                    badge = {
                        if (d.carritoVM.count() > 0) Badge { Text(d.carritoVM.count().toString()) }
                    }
                ) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                }
            }
        })
    }) { padding ->
        Column(Modifier.padding(padding).padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        applyFilters(d.productoVM, cat, min, max, query)
                    },
                    label = { Text("Buscar") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = cat ?: "",
                    onValueChange = {
                        cat = it.ifBlank { null }
                        applyFilters(d.productoVM, cat, min, max, query)
                    },
                    label = { Text("Categoría") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = min,
                    onValueChange = {
                        min = it
                        applyFilters(d.productoVM, cat, min, max, query)
                    },
                    label = { Text("Min") },
                    modifier = Modifier.width(100.dp)
                )
                OutlinedTextField(
                    value = max,
                    onValueChange = {
                        max = it
                        applyFilters(d.productoVM, cat, min, max, query)
                    },
                    label = { Text("Max") },
                    modifier = Modifier.width(100.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            LazyColumn {
                items(productos) { p ->
                    ProductoItem(p, onAdd = { d.carritoVM.add(it) })
                }
            }
        }
    }
}

private fun applyFilters(vm: ProductoVM, cat: String?, min: String, max: String, q: String) {
    vm.aplicarFiltros(cat, min.toIntOrNull(), max.toIntOrNull(), q)
}

@Composable
fun ProductoItem(p: Producto, onAdd: (Producto) -> Unit) {
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current


    var added by remember { mutableStateOf(false) }


    LaunchedEffect(added) {
        if (added) {
            delay(1100)
            added = false
        }
    }

    // Elevación dinámica del Card
    val elevation = if (added) 8.dp else 2.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(elevation)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(p.nombre, style = MaterialTheme.typography.titleMedium)
                Text("$" + "%,d".format(p.precio))
                Text(p.descripcion)
                Spacer(Modifier.height(8.dp))
                Row {
                    Button(
                        onClick = {
                            onAdd(p)

                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)

                            Toast.makeText(
                                context,
                                "${p.nombre} agregado al carrito",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Disparar animación
                            added = true
                        }
                    ) {
                        Icon(Icons.Filled.AddShoppingCart, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Agregar")
                    }
                }
            }


            androidx.compose.animation.AnimatedVisibility(
                visible = added,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text(text = "Agregado", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
