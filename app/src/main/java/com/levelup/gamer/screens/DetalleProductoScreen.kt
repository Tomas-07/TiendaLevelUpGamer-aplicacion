@file:OptIn(ExperimentalMaterial3Api::class)

package com.levelup.gamer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.levelup.gamer.ui.deps
import kotlinx.coroutines.launch

@Composable
fun DetalleProductoScreen(
    id: String,          // ← RECIBE EL ID COMO STRING
    onBack: () -> Unit
) {
    val d = deps()
    val productos by d.productoVM.items.collectAsState()

    // Convertimos ID a Long
    val productId = id.toLong()

    // Buscamos por ID (NO por código)
    val producto = productos.firstOrNull { it.id == productId }

    if (producto == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Producto no encontrado") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).padding(20.dp)) {
                Text("No se encontró el producto con ID $id")
            }
        }
        return
    }

    var stock by remember { mutableStateOf(producto.stock) }
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(producto.nombre) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // IMAGEN
            AsyncImage(
                model = producto.imagen,
                contentDescription = producto.nombre,
                modifier = Modifier
                    .height(260.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(20.dp))

            // TÍTULO
            Text(
                producto.nombre,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(10.dp))

            // DESCRIPCIÓN
            Text(
                producto.descripcion,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            // PRECIO
            Text(
                "Precio:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "$" + "%,d".format(producto.precio),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            // STOCK
            Text(
                text = "Stock disponible: $stock",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (stock > 0) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.error
            )

            Spacer(Modifier.height(25.dp))

            // BOTÓN AGREGAR AL CARRITO
            Button(
                onClick = {
                    d.carritoVM.add(producto)

                    scope.launch {
                        snackbar.showSnackbar("Agregado: ${producto.nombre}")
                    }

                    stock = producto.stock
                },
                enabled = stock > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text(
                    if (stock > 0) "Agregar al carrito"
                    else "Sin stock",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
