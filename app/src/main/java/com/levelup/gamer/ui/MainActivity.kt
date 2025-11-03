package com.levelup.gamer.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.levelup.gamer.navigation.AppNav
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
                    val nav = rememberNavController()
                    val ctx = LocalContext.current

                    val repo = remember(ctx) { ProductoRepository(ctx) }
                    val session = remember(ctx) { SessionRepository(ctx) }

                    val productoVM = remember { ProductoVM(repo) }
                    val carritoVM = remember { CarritoVM() }
                    val usuarioVM = remember { UsuarioVM(session) }

                    ProvideDeps(Deps(productoVM, carritoVM, usuarioVM)) {
                        AppNav(nav)
                    }
                }
            }
        }
    }
}

data class Deps(val productoVM: ProductoVM, val carritoVM: CarritoVM, val usuarioVM: UsuarioVM)
private val LocalDeps = androidx.compose.runtime.staticCompositionLocalOf<Deps> { error("Deps not provided") }

@Composable
fun deps() = LocalDeps.current

@Composable
fun ProvideDeps(d: Deps, content: @Composable ()->Unit) {
    CompositionLocalProvider(LocalDeps provides d) { content() }
}

fun openWhatsApp(number: String, message: String, activity: ComponentActivity) {
    val uri = Uri.parse("https://wa.me/$number?text=" + Uri.encode(message))
    val i = Intent(Intent.ACTION_VIEW, uri)
    activity.startActivity(i)
}
