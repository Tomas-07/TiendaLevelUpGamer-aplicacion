package com.levelup.gamer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.levelup.gamer.api.CarritoApi
import com.levelup.gamer.navigation.AppNav
import com.levelup.gamer.remote.RetrofitClient // Importamos el nuevo cliente
import com.levelup.gamer.repository.CarritoRepository
import com.levelup.gamer.repository.ProductoRepository
import com.levelup.gamer.repository.SessionRepository
import com.levelup.gamer.ui.theme.LevelUpTheme
import com.levelup.gamer.viewmodel.CarritoVM
import com.levelup.gamer.viewmodel.ProductoVM
import com.levelup.gamer.viewmodel.UsuarioVM

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LevelUpTheme {
                Surface {

                    // NAV CONTROLLER
                    val nav = rememberNavController()

                    // CONTEXTO
                    val ctx = LocalContext.current

                    // APIs (Retrofit) - Usando el nuevo RetrofitClient centralizado
                    val retrofit = RetrofitClient.retrofit
                    val carritoApi  = retrofit.create(CarritoApi::class.java)

                    // REPOSITORIOS
                    val productoRepo = remember { ProductoRepository() }
                    val sessionRepo  = remember { SessionRepository(ctx) } // CORREGIDO
                    val carritoRepo  = remember { CarritoRepository(carritoApi) }

                    // VIEWMODELS
                    val productoVM = remember { ProductoVM(productoRepo) }
                    val usuarioVM  = remember { UsuarioVM(sessionRepo) }
                    val carritoVM  = remember { CarritoVM(carritoRepo, productoRepo, sessionRepo) }

                    // DEPENDENCIAS GLOBALES
                    ProvideDeps(
                        Deps(
                            productoVM = productoVM,
                            carritoVM  = carritoVM,
                            usuarioVM  = usuarioVM
                        )
                    ) {
                        AppNav(nav)
                    }
                }
            }
        }
    }
}

data class Deps(
    val productoVM: ProductoVM,
    val carritoVM: CarritoVM,
    val usuarioVM: UsuarioVM
)

private val LocalDeps = androidx.compose.runtime.staticCompositionLocalOf<Deps> {
    error("Deps not provided")
}

@Composable
fun deps() = LocalDeps.current

@Composable
fun ProvideDeps(d: Deps, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalDeps provides d) {
        content()
    }
}
