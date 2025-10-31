@file:OptIn(ExperimentalMaterial3Api::class)
package com.levelup.gamer.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.levelup.gamer.ui.deps
import androidx.compose.material3.ExperimentalMaterial3Api   // <-- Import necesario

@Composable
fun CarritoScreen(onBack: () -> Unit) {
    val d = deps()
    val items by d.carritoVM.items.collectAsState()
    val esDuoc by d.usuarioVM.esDuoc.collectAsState()
    val subtotal = d.carritoVM.subtotal()
    val desc = if (esDuoc) (subtotal * 0.20).toInt() else 0
    val total = subtotal - desc


    val haptics = LocalHapticFeedback.current

    Scaffold(topBar = { TopAppBar(title = { Text("Carrito") }) }) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            LazyColumn(Modifier.weight(1f, fill = false)) {
                items(items) { itc ->
                    ListItem(
                        headlineContent = { Text(itc.producto.nombre) },
                        supportingContent = {
                            Text("x${itc.cantidad} — $" + "%,d".format(itc.producto.precio * itc.cantidad))
                        },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { d.carritoVM.update(itc.producto.codigo, itc.cantidad - 1) }) {
                                    Icon(Icons.Filled.Remove, contentDescription = "Menos")
                                }
                                IconButton(onClick = { d.carritoVM.update(itc.producto.codigo, itc.cantidad + 1) }) {
                                    Icon(Icons.Filled.Add, contentDescription = "Más")
                                }
                                IconButton(onClick = { d.carritoVM.remove(itc.producto.codigo) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Quitar")
                                }
                            }
                        }
                    )
                    Divider()
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Subtotal: $" + "%,d".format(subtotal))
            if (esDuoc) Text("Descuento DUOC 20%: -$" + "%,d".format(desc))
            Text("Total: $" + "%,d".format(total))
            Spacer(Modifier.height(12.dp))
            Row {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                    Text("Seguir comprando")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        d.usuarioVM.addPuntos((total / 10000).coerceAtLeast(1) * 10)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Pagar")
                }
            }
        }
    }
}
