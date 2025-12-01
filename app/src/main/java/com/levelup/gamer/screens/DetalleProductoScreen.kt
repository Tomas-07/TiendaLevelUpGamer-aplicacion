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
import com.levelup.gamer.api.UsuarioApi
import com.levelup.gamer.model.Producto
import com.levelup.gamer.remote.ProductoApiService
import com.levelup.gamer.remote.RetrofitClient
import com.levelup.gamer.repository.CarritoRepository
import com.levelup.gamer.repository.ProductoRepository
import com.levelup.gamer.repository.SessionRepository
import com.levelup.gamer.viewmodel.CarritoVM
import com.levelup.gamer.viewmodel.ProductoVM

private val DetalleGamerGreen = Color(0xFF00FF00)
private val DetalleDarkBackground = Color(0xFF121212)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    id: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // --- DEPENDENCIAS CREADAS LOCALMENTE ---
    val retrofit = RetrofitClient.retrofit
    val productoApi = retrofit.create(ProductoApiService::class.java)
    val carritoApi = retrofit.create(CarritoApi::class.java)
    val usuarioApi = retrofit.create(UsuarioApi::class.java)

    val productoRepo = remember { ProductoRepository(productoApi) }
    val carritoRepo = remember { CarritoRepository(carritoApi) }
    val sessionRepo = remember { SessionRepository(context, usuarioApi) }

    // --- VIEWMODELS ---
    val productoVM: ProductoVM = viewModel(factory = ProductoVM.Factory(productoRepo))
    val carritoVM: CarritoVM = viewModel(factory = CarritoVM.Factory(carritoRepo, productoRepo, sessionRepo))

    // --- ESTADOS DE LA UI ---
    var producto by remember { mutableStateOf<Producto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargamos el producto usando el ViewModel
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
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DetalleGamerGreen)
            )
        },
        containerColor = DetalleDarkBackground
    ) { paddingValues ->

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DetalleGamerGreen)
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
                // Imagen
                Box(
                    modifier = Modifier.fillMaxWidth().height(300.dp).background(Color.Black)
                ) {
                    AsyncImage(
                        model = prod.imagen,
                        contentDescription = prod.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Info
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = prod.nombre, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "$${prod.precio}", style = MaterialTheme.typography.headlineLarge, color = DetalleGamerGreen, fontWeight = FontWeight.ExtraBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = Color.Yellow)
                            Text(text = "4.8", color = Color.White, modifier = Modifier.padding(start = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.Gray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Descripción", style = MaterialTheme.typography.titleMedium, color = DetalleGamerGreen, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = prod.descripcion ?: "Sin descripción.", style = MaterialTheme.typography.bodyLarge, color = Color.LightGray, lineHeight = 24.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Stock: ${prod.stock}", style = MaterialTheme.typography.bodyMedium, color = if (prod.stock > 0) Color.Green else Color.Red)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botón Agregar
                Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    Button(
                        onClick = {
                            carritoVM.add(prod)
                            Toast.makeText(context, "Producto agregado", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DetalleGamerGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AGREGAR AL CARRITO", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}