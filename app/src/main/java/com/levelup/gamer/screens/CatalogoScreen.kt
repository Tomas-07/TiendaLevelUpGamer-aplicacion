@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.levelup.gamer.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.levelup.gamer.model.Producto
import com.levelup.gamer.ui.deps
import kotlinx.coroutines.launch

@Composable
fun CatalogoScreen(
    onGoCart: () -> Unit,
    onGoPerfil: () -> Unit,
    onGoDetail: (Long) -> Unit
) {
    val d = deps()
    val productos by d.productoVM.items.collectAsState()

    LaunchedEffect(Unit) { d.productoVM.cargar() }

    val snackbar = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo Gamer", color = Color.White) },
                actions = {
                    IconButton(onClick = onGoPerfil) {
                        Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = Color.White)
                    }

                    IconButton(onClick = onGoCart) {
                        BadgedBox(
                            badge = {
                                val c = d.carritoVM.count()
                                if (c > 0) Badge { Text(c.toString()) }
                            }
                        ) {
                            Icon(Icons.Filled.ShoppingCart, "Carrito", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF00E676), Color(0xFF00C853))
                    )
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbar) }
    ) { padding ->

        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1C1C1C),
                            Color.Black
                        )
                    )
                )
                .padding(14.dp)
        ) {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(productos) { p ->
                    ProductoCard(
                        p = p,
                        onAdd = { d.carritoVM.add(it) },
                        onDetail = { onGoDetail(p.id) },
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
    val haptics = LocalHapticFeedback.current
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.02f else 1f,
        animationSpec = tween(160), label = "cardScale"
    )

    val glowColor by animateColorAsState(
        targetValue = if (p.destacado) Color(0xFF00E676) else Color.Transparent,
        animationSpec = tween(400), label = "glow"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        elevation = CardDefaults.cardElevation(10.dp),
        border = if (p.destacado) BorderStroke(2.dp, glowColor) else null
    ) {

        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

            AsyncImage(
                model = p.imagen,
                contentDescription = p.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            )

            Text(
                p.nombre,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                "$" + "%,d".format(p.precio),
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF00E676),
                fontWeight = FontWeight.ExtraBold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedButton(
                    onClick = onDetail,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Text("Detalles", color = Color.White)
                }

                Button(
                    onClick = {
                        pressed = true
                        onAdd(p)
                        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)

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
