package com.levelup.gamer.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.levelup.gamer.api.CarritoApi
import com.levelup.gamer.api.ProductoApi
import com.levelup.gamer.api.UsuarioApi
import com.levelup.gamer.model.Producto
import com.levelup.gamer.remote.RetrofitClient
import com.levelup.gamer.repository.CarritoRepository
import com.levelup.gamer.repository.ProductoRepository
import com.levelup.gamer.repository.SessionRepository
import com.levelup.gamer.viewmodel.CartItem
import com.levelup.gamer.viewmodel.CarritoVM
import com.levelup.gamer.viewmodel.UsuarioVM
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 1. INYECCIÓN DE DEPENDENCIAS (Igual que en Catálogo)
    val retrofit = RetrofitClient.retrofit
    val carritoApi = retrofit.create(CarritoApi::class.java)
    val productoApi = retrofit.create(ProductoApi::class.java)
    val usuarioApi = retrofit.create(UsuarioApi::class.java)

    val carritoRepo = remember { CarritoRepository(carritoApi) }
    val productoRepo = remember { ProductoRepository(productoApi) }
    val sessionRepo = remember { SessionRepository(context, usuarioApi) }

    // 2. ViewModels con Factory
    val carritoVM: CarritoVM = viewModel(
        factory = CarritoVM.Factory(carritoRepo, productoRepo, sessionRepo)
    )
    val usuarioVM: UsuarioVM = viewModel(
        factory = UsuarioVM.Factory(sessionRepo)
    )

    // 3. Estados
    val cartItems by carritoVM.items.collectAsState()
    val esDuoc by usuarioVM.esDuoc.collectAsState()

    // Cálculos
    val subtotal = cartItems.sumOf { it.producto.precio * it.cantidad }
    val descuento = if (esDuoc) (subtotal * 0.20).toInt() else 0
    val total = subtotal - descuento

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GamerGreen),
                actions = {
                    if (esDuoc) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text("DUOC -20%", color = Color.Black, fontWeight = FontWeight.Bold) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color.Yellow)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = DarkBackground
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if (cartItems.isEmpty()) {
                // Estado Vacío
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tu carrito está vacío", style = MaterialTheme.typography.headlineSmall, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = GamerGreen, contentColor = Color.Black)
                        ) {
                            Text("Ir al Catálogo")
                        }
                    }
                }
            } else {
                // Lista de Productos
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemRow(
                            item = item,
                            onAdd = { carritoVM.add(item.producto) },
                            onDec = { carritoVM.dec(item.producto) },
                            onRemove = { carritoVM.remove(item) }
                        )
                    }
                }

                // Resumen de Pago
                CartSummary(
                    subtotal = subtotal,
                    descuento = descuento,
                    total = total,
                    onPagar = {
                        if (total > 0) {
                            val puntos = (total / 1000) // Ejemplo: 1 punto por cada 1000 pesos
                            usuarioVM.addPuntos(puntos)
                            carritoVM.clear()
                            scope.launch {
                                snackbarHostState.showSnackbar("¡Compra exitosa! Ganaste $puntos puntos.")
                            }
                        }
                    },
                    onVaciar = { carritoVM.clear() }
                )
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onAdd: () -> Unit,
    onDec: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen
            AsyncImage(
                model = item.producto.imagen,
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.producto.nombre, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = precioFmt(item.producto.precio), color = GamerGreen, fontWeight = FontWeight.SemiBold)
            }

            // Controles (+ -)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDec) {
                    Text("-", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = "${item.cantidad}", color = Color.White, fontWeight = FontWeight.Bold)
                IconButton(onClick = onAdd) {
                    Text("+", color = GamerGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Eliminar
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun CartSummary(
    subtotal: Int,
    descuento: Int,
    total: Int,
    onPagar: () -> Unit,
    onVaciar: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, GamerGreen)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Subtotal", color = Color.LightGray)
                Text(precioFmt(subtotal), color = Color.White)
            }
            if (descuento > 0) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Descuento", color = GamerGreen)
                    Text("-${precioFmt(descuento)}", color = GamerGreen)
                }
            }
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.3f))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("TOTAL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(precioFmt(total), color = GamerGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onVaciar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                ) {
                    Text("Vaciar")
                }
                Button(
                    onClick = onPagar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GamerGreen, contentColor = Color.Black)
                ) {
                    Text("PAGAR", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Función auxiliar para formato de precio
fun precioFmt(precio: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    return format.format(precio)
}