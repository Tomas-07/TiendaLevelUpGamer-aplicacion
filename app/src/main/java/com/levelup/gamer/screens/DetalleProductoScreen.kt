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

@Composable
fun DetalleProductoScreen(
    codigo: String,
    onBack: () -> Unit
) {
    val d = deps()
    val productos by d.productoVM.items.collectAsState()

    val producto = productos.firstOrNull { it.codigo == codigo }

    if (producto == null) {
        Text("Producto no encontrado")
        return
    }


    var stock by remember { mutableStateOf(producto.stock) }

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
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {


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


            Text(
                producto.nombre,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(10.dp))


            Text(
                producto.descripcion,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))


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


            Text(
                text = "Stock disponible: $stock",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (stock > 0) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.error
            )

            Spacer(Modifier.height(25.dp))


            Button(
                onClick = {
                    d.carritoVM.add(producto)
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
