@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.levelup.gamer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.levelup.gamer.model.Producto
import com.levelup.gamer.ui.deps
import kotlinx.coroutines.launch

@Composable
fun CatalogoScreen(
    onGoCart: () -> Unit,
    onGoPerfil: () -> Unit,
    onGoDetail: (String) -> Unit
) {
    val d = deps()
    val productos by d.productoVM.items.collectAsState()

    LaunchedEffect(Unit) { d.productoVM.cargar() }

    val snackbar = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo") },
                actions = {
                    IconButton(onClick = onGoPerfil) {
                        Icon(Icons.Filled.Person, contentDescription = "Perfil")
                    }

                    IconButton(onClick = onGoCart) {
                        BadgedBox(badge = {
                            val c = d.carritoVM.count()
                            if (c > 0) Badge { Text(c.toString()) }
                        }) {
                            Icon(Icons.Filled.ShoppingCart, "Carrito")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbar) }
    ) { padding ->

        Column(modifier = Modifier.padding(padding).padding(12.dp)) {

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(productos) { p ->
                    ProductoItem(
                        p = p,
                        onAdd = { d.carritoVM.add(it) },
                        onDetail = { onGoDetail(p.codigo) },
                        snackbar = snackbar
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductoItem(
    p: Producto,
    onAdd: (Producto) -> Unit,
    onDetail: () -> Unit,
    snackbar: SnackbarHostState
) {
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    var added by remember { mutableStateOf(false) }
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (added) 1.05f else 1f,
        animationSpec = androidx.compose.animation.core.tween(160),
        label = "scaleAnim"
    )

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {

            Text(p.nombre, style = MaterialTheme.typography.titleMedium)
            Text("$" + "%,d".format(p.precio))

            if (p.descripcion.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(p.descripcion)
            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedButton(
                    onClick = onDetail,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Info, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Detalles")
                }

                Button(
                    onClick = {
                        onAdd(p)
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        added = true

                        scope.launch {
                            snackbar.showSnackbar(
                                "Agregado: ${p.nombre}",
                                withDismissAction = false,
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                ) {
                    Icon(Icons.Filled.AddShoppingCart, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Agregar")
                }
            }
        }
    }

    LaunchedEffect(added) {
        if (added) {
            kotlinx.coroutines.delay(180)
            added = false
        }
    }
}
