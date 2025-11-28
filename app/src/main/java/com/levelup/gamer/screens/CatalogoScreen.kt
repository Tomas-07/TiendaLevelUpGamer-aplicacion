package com.levelup.gamer.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.levelup.gamer.model.Producto
import com.levelup.gamer.ui.deps
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    onGoCart: () -> Unit,
    onGoPerfil: () -> Unit,
    onGoDetail: (Long) -> Unit
) {
    val d = deps()  // Inyectamos dependencias

    // Estado para almacenar productos cargados desde el ViewModel
    val productos by d.productoVM.productos.collectAsState()

    // Cargar productos desde el backend
    LaunchedEffect(Unit) {
        d.productoVM.cargar()
    }

    // Estado para mostrar mensajes de snackbar
    val snackbar = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo Gamer", color = Color.White) },
                actions = {
                    // Botón de perfil
                    IconButton(onClick = onGoPerfil) {
                        Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = Color.White)
                    }

                    // Botón de carrito con badge de cantidad de productos
                    IconButton(onClick = onGoCart) {
                        BadgedBox(
                            badge = {
                                val c = d.carritoVM.count()  // Obtenemos cantidad de productos en carrito
                                if (c > 0) Badge { Text(c.toString()) }
                            }
                        ) {
                            Icon(Icons.Filled.ShoppingCart, "Carrito", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(
                    Brush.horizontalGradient(listOf(Color(0xFF00E676), Color(0xFF00C853)))
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbar) }
    ) { padding ->

        // Contenido de la pantalla con un fondo degradado
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1C1C1C), Color.Black)
                    )
                )
                .padding(14.dp)
        ) {
            // Lista de productos usando LazyColumn
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(productos) { p ->
                    ProductoCard(
                        p = p,
                        onAdd = { d.carritoVM.add(it) }, // Agregar al carrito
                        onDetail = { onGoDetail(p.id) }, // Ver detalles del producto
                        snackbar = snackbar
                    )
                }
            }
        }
    }
}

@Composable
fun ProductoCard(
    p: Producto,
    onAdd: (Producto) -> Unit,
    onDetail: () -> Unit,
    snackbar: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.02f else 1f,
        animationSpec = tween(160)
    )

    // Crear un Card con animación y color dependiendo de si es destacado
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Imagen del producto
            AsyncImage(
                model = p.imagen,
                contentDescription = p.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            )

            // Nombre del producto
            Text(
                p.nombre,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            // Precio del producto
            Text(
                "$" + "%,d".format(p.precio),
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF00E676),
                fontWeight = FontWeight.ExtraBold
            )

            // Botones de acción
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ver detalles del producto
                OutlinedButton(
                    onClick = onDetail,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Text("Detalles", color = Color.White)
                }

                // Agregar al carrito
                Button(
                    onClick = {
                        pressed = true
                        onAdd(p) // Agregar al carrito
                        scope.launch {
                            snackbar.showSnackbar("Agregado: ${p.nombre}")
                        }
                        pressed = false
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E676),
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Filled.AddShoppingCart, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Agregar")
                }
            }
        }
    }
}
