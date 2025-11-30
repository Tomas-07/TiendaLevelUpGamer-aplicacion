package com.levelup.gamer.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences // Importante
import com.levelup.gamer.api.UsuarioApi
import com.levelup.gamer.model.Usuario
import com.levelup.gamer.model.UsuarioDto
import io.mockk.MockKAnnotations // Para iniciar mocks sin Rule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf // Importante
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class SessionRepositoryTest {

    // 1. Quitamos la regla que causaba error y usamos init manual
    // @get:Rule val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var usuarioApi: UsuarioApi

    @RelaxedMockK
    private lateinit var context: Context

    @RelaxedMockK
    private lateinit var dataStore: DataStore<Preferences>

    private lateinit var sessionRepository: SessionRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        // 2. CRUCIAL: Simulamos que el DataStore devuelve preferencias vacías al inicio.
        // Sin esto, el repositorio falla al instanciarse porque intenta leer variables.
        every { dataStore.data } returns flowOf(emptyPreferences())

        // 3. Pasamos el dataStore mockeado al constructor
        sessionRepository = SessionRepository(context, usuarioApi, dataStore)
    }

    @Test
    fun `register - cuando la API devuelve éxito - debe retornar true`() = runTest {

        val usuarioDePrueba = Usuario(id = null, nombre = "Test User", email = "test@test.com", edad = 25, password = "password123")
        // No necesitamos el DTO aquí para la respuesta simulada si la API espera Usuario

        // 4. SOLUCIÓN ERROR DE TIPOS:
        // El error decía "expected Response<Usuario>".
        // Así que hacemos que el mock devuelva 'Response.success(usuarioDePrueba)'.
        coEvery { usuarioApi.register(any()) } returns Response.success(usuarioDePrueba)

        val resultado = sessionRepository.register(usuarioDePrueba, "password123")

        assertTrue(resultado)
    }

    @Test
    fun `register - cuando la API devuelve un error - debe retornar false`() = runTest {

        val usuarioDePrueba = Usuario(id = null, nombre = "Test User", email = "test@test.com", edad = 25, password = "password123")

        // Simulamos un error 400
        coEvery { usuarioApi.register(any()) } returns Response.error(400, okhttp3.ResponseBody.create(null, ""))

        val resultado = sessionRepository.register(usuarioDePrueba, "password123")

        assertFalse(resultado)
    }

    @Test
    fun `register - cuando la API lanza una excepción - debe retornar false`() = runTest {

        val usuarioDePrueba = Usuario(id = null, nombre = "Test User", email = "test@test.com", edad = 25, password = "password123")

        coEvery { usuarioApi.register(any()) } throws IOException("Error de red")

        val resultado = sessionRepository.register(usuarioDePrueba, "password123")

        assertFalse(resultado)
    }
}