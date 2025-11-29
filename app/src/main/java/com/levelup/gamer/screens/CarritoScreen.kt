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

// COLORES PRIVADOS PARA ESTA PANTALLA
private val CarritoGamerGreen = Color(0xFF00FF00)
private val CarritoDarkBackground = Color(0xFF121212)
private val CarritoCardBackground = Color(0xFF1E1E1E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // 1. INYECCIÓN DE DEPENDENCIAS (Evita el crash)
    val retrofit = RetrofitClient.retrofit
    val carritoApi = retrofit.create(CarritoApi::class.java)
    val productoApi = retrofit.create(ProductoApi::class.java)
    val usuarioApi = retrofit.create(UsuarioApi::class.java)

    val carritoRepo = remember { CarritoRepository(carritoApi) }
    val productoRepo = remember { ProductoRepository(productoApi) }
    val sessionRepo = remember { SessionRepository(context, usuarioApi) }

    // 2. VIEWMODELS CON FACTORY
    val carritoVM: CarritoVM = viewModel(
        factory = CarritoVM.Factory(carritoRepo, productoRepo, sessionRepo)
    )
    val usuarioVM: UsuarioVM = viewModel(
        factory = UsuarioVM.Factory(sessionRepo)
    )

    // --- IMPORTANTE: Forzar carga al entrar ---
    LaunchedEffect(Unit) {
        carritoVM.cargar()
    }

    // 3. ESTADOS
    val cartItems by carritoVM.items.collectAsState()
    val esDuoc by usuarioVM.esDuoc.collectAsState()

    // CÁLCULOS
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CarritoGamerGreen),
                actions = {
                    if (esDuoc) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text("DUOC -20%", color = Color.Black, fontWeight = FontWeight.Bold) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color.Yellow),
                            border = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = CarritoDarkBackground
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
                            colors = ButtonDefaults.buttonColors(containerColor = CarritoGamerGreen, contentColor = Color.Black)
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
                            // Lógica de compra
                            val puntos = (total / 1000) // 1 punto por cada $1000
                            usuarioVM.addPuntos(puntos)
                            carritoVM.clear() // Vaciar carrito
                            scope.launch {
                                snackbarHostState.showSnackbar("¡Compra exitosa! Ganaste $puntos puntos.")
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("No hay nada que pagar")
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
        colors = CardDefaults.cardColors(containerColor = CarritoCardBackground),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen pequeña
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

            // Info del producto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.producto.nombre,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    fontSize = 16.sp
                )
                Text(
                    text = precioFmt(item.producto.precio),
                    color = CarritoGamerGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Controles de Cantidad (+ -)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDec) {
                    Text("-", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "${item.cantidad}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                IconButton(onClick = onAdd) {
                    Text("+", color = CarritoGamerGreen, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Botón Eliminar
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.8f))
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
        colors = CardDefaults.cardColors(containerColor = CarritoCardBackground),
        border = BorderStroke(1.dp, CarritoGamerGreen), // Borde verde para destacar el total
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Subtotal
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("Subtotal", color = Color.LightGray)
                Text(precioFmt(subtotal), color = Color.White)
            }

            // Descuento (solo si aplica)
            if (descuento > 0) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Descuento DUOC", color = CarritoGamerGreen)
                    Text("-${precioFmt(descuento)}", color = CarritoGamerGreen)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Gray.copy(alpha = 0.3f))

            // Total
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text("TOTAL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(precioFmt(total), color = CarritoGamerGreen, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de acción
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
                    colors = ButtonDefaults.buttonColors(containerColor = CarritoGamerGreen, contentColor = Color.Black)
                ) {
                    Text("PAGAR", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


fun precioFmt(precio: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    return format.format(precio)
}