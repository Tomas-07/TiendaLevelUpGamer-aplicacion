package com.levelup.gamer.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.levelup.gamer.viewmodel.CarritoVM
import com.levelup.gamer.viewmodel.ProductoVM
import kotlinx.coroutines.launch

// Usamos 'private' para que estos colores solo existan en este archivo
// y no choquen con los de CatalogoScreen
private val GamerGreen = Color(0xFF00FF00)
private val DarkBackground = Color(0xFF121212)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    id: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // 1. Configuración de dependencias
    val retrofit = RetrofitClient.retrofit
    val productoApi = retrofit.create(ProductoApi::class.java)
    val carritoApi = retrofit.create(CarritoApi::class.java)
    val usuarioApi = retrofit.create(UsuarioApi::class.java)

    val productoRepo = remember { ProductoRepository(productoApi) }
    val carritoRepo = remember { CarritoRepository(carritoApi) }
    val sessionRepo = remember { SessionRepository(context, usuarioApi) }

    // 2. ViewModels con Factory
    val productoVM: ProductoVM = viewModel(
        factory = ProductoVM.Factory(productoRepo)
    )
    val carritoVM: CarritoVM = viewModel(
        factory = CarritoVM.Factory(carritoRepo, productoRepo, sessionRepo)
    )

    // 3. Estados
    var producto by remember { mutableStateOf<Producto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar producto al entrar
    LaunchedEffect(id) {
        isLoading = true
        producto = productoVM.getProductoById(id)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle", color = Color.Black, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GamerGreen)
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GamerGreen)
            }
        } else if (producto == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Producto no encontrado", color = Color.White)
            }
        } else {
            val prod = producto!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- IMAGEN ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = prod.imagen,
                        contentDescription = prod.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // --- INFO DEL PRODUCTO ---
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = prod.nombre,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Precio y Rating
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$${prod.precio}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = GamerGreen,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow)
                            Text(text = "4.8", color = Color.White, modifier = Modifier.padding(start = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.Gray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium,
                        color = GamerGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = prod.descripcion ?: "Sin descripción disponible.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.LightGray,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Stock disponible: ${prod.stock}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (prod.stock > 0) Color.Green else Color.Red
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // --- BOTÓN AGREGAR AL CARRITO ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Button(
                        onClick = {
                            carritoVM.add(prod)
                            Toast.makeText(context, "Producto agregado", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GamerGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AGREGAR AL CARRITO",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}