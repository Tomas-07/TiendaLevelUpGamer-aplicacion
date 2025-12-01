package com.levelup.gamer.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.levelup.gamer.model.Producto

import com.levelup.gamer.ui.deps

private val CatalogoGreen = Color(0xFF00FF00)
private val CatalogoDark = Color(0xFF121212)
private val CatalogoCard = Color(0xFF1E1E1E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    onGoCart: () -> Unit,
    onGoPerfil: () -> Unit,
    onGoDetail: (Long) -> Unit
) {
    val context = LocalContext.current


    val deps = deps()
    val productoVM = deps.productoVM
    val carritoVM = deps.carritoVM

    val productos by productoVM.productos.collectAsState()
    val cartItems by carritoVM.items.collectAsState()
    val itemCount = cartItems.sumOf { it.cantidad }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo Gamer", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CatalogoGreen),
                actions = {
                    IconButton(onClick = onGoPerfil) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color.Black)
                    }
                    IconButton(onClick = onGoCart) {
                        BadgedBox(
                            badge = { if (itemCount > 0) Badge { Text("$itemCount") } }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = Color.Black)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onGoCart,
                containerColor = CatalogoGreen,
                contentColor = Color.Black,
                icon = {
                    BadgedBox(
                        badge = { if (itemCount > 0) Badge { Text("$itemCount") } }
                    ) {
                        Icon(Icons.Default.ShoppingCart, "Ver Carrito")
                    }
                },
                text = { Text("Ver Carrito", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = CatalogoDark
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (productos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CatalogoGreen)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(
                        items = productos,
                        key = { it.id }
                    ) { producto ->
                        ProductoCardGamer(
                            producto = producto,
                            onDetailClick = { onGoDetail(producto.id) },
                            onAddClick = {
                                carritoVM.add(producto)
                                Toast.makeText(context, "¡${producto.nombre} agregado!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoCardGamer(
    producto: Producto,
    onDetailClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDetailClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CatalogoCard),
        border = BorderStroke(2.dp, CatalogoGreen),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = producto.imagen,
                    contentDescription = producto.nombre,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "$${producto.precio}",
                style = MaterialTheme.typography.headlineSmall,
                color = CatalogoGreen,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onDetailClick,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, Color.Gray),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Info, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Detalles")
                }

                Button(
                    onClick = onAddClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = CatalogoGreen, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.AddShoppingCart, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Agregar")
                }
            }
        }
    }
}