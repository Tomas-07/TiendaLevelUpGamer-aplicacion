@file:OptIn(ExperimentalMaterial3Api::class)

package com.levelup.gamer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.levelup.gamer.viewmodel.CartItem
import com.levelup.gamer.model.Producto
import com.levelup.gamer.ui.deps
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CarritoScreen(onBack: () -> Unit) {

    val d = deps()

    val cartItems by d.carritoVM.items.collectAsState()
    val esDuoc by d.usuarioVM.esDuoc.collectAsState(initial = false)

    val subtotal = cartItems.sumOf { it.producto.precio * it.cantidad }
    val descuento = if (esDuoc) (subtotal * 0.20).toInt() else 0
    val total = subtotal - descuento

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (esDuoc) {
                        AssistChip(
                            onClick = {},
                            label = { Text("DUOC -20%") }
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (cartItems.isEmpty()) {
                EmptyCartCard(onBack)
            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(cartItems) { item ->
                        CartRow(
                            item = item,
                            onAdd = { d.carritoVM.add(item.producto) },
                            onDec = { d.carritoVM.dec(item.producto) },
                            onRemove = { d.carritoVM.remove(item) }
                        )
                    }
                }

                SummaryCard(
                    subtotal = subtotal,
                    descuento = descuento,
                    total = total,
                    duoc = esDuoc,
                    onPagar = {

                        if (total <= 0) {
                            scope.launch {
                                snackbar.showSnackbar("No hay nada que pagar")
                            }
                            return@SummaryCard
                        }

                        val puntosGanados = (total / 100).coerceAtLeast(0)

                        scope.launch {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)

                            d.usuarioVM.addPuntos(puntosGanados)

                            d.carritoVM.clear()

                            snackbar.showSnackbar("Compra realizada +$puntosGanados pts")
                        }
                    },
                    onVaciar = { d.carritoVM.clear() }
                )
            }
        }
    }
}

@Composable
private fun EmptyCartCard(onBack: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Tu carrito está vacío", style = MaterialTheme.typography.titleMedium)
            Text("Agrega productos desde el catálogo.")
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onBack) { Text("Volver al catálogo") }
        }
    }
}

@Composable
private fun CartRow(
    item: CartItem,
    onAdd: (Producto) -> Unit,
    onDec: (Producto) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = item.producto.imagen,
                contentDescription = item.producto.nombre,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    item.producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(precioFmt(item.producto.precio), style = MaterialTheme.typography.bodyMedium)
                Text("Cantidad: ${item.cantidad}", style = MaterialTheme.typography.labelMedium)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { onDec(item.producto) },
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { Text("-") }

                Text("${item.cantidad}", style = MaterialTheme.typography.titleMedium)

                Button(
                    onClick = { onAdd(item.producto) },
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { Text("+") }

                IconButton(onClick = onRemove) {
                    Icon(Icons.Filled.Delete, contentDescription = "Quitar")
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    subtotal: Int,
    descuento: Int,
    total: Int,
    duoc: Boolean,
    onPagar: () -> Unit,
    onVaciar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Subtotal")
                Text(precioFmt(subtotal))
            }

            if (duoc) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Descuento DUOC (20%)")
                    Text("-" + precioFmt(descuento))
                }
                Divider()
            }

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.Bold)
                Text(precioFmt(total), fontWeight = FontWeight.Bold)
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(onClick = onVaciar, modifier = Modifier.weight(1f)) {
                    Text("Vaciar")
                }
                Button(onClick = onPagar, modifier = Modifier.weight(1f)) {
                    Text("Pagar")
                }
            }
        }
    }
}

private fun precioFmt(v: Int): String =
    NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(v)
