package com.levelup.gamer.repository

import android.content.Context
import android.util.Log // Importante
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.levelup.gamer.api.UsuarioApi
import com.levelup.gamer.model.Usuario
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockkStatic // Importante
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class SessionRepositoryTest {

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

        // 1. SOLUCIÓN AL ERROR ROJO: Mockeamos la clase Log estática
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        // 2. Simulamos DataStore vacío para que no explote al iniciar
        every { dataStore.data } returns flowOf(emptyPreferences())

        sessionRepository = SessionRepository(context, usuarioApi, dataStore)
    }

    @Test
    fun `register - cuando la API devuelve éxito - debe retornar true`() = runTest {
        // Asegúrate de que tu modelo Usuario tenga estos campos o valores por defecto
        val usuarioDePrueba = Usuario(
            id = null,
            nombre = "Test User",
            email = "test@test.com",
            edad = 25,
            password = "password123"
            // Si te pide más campos (puntos, nivel), agrégalos aquí o ponles valor por defecto en el Modelo
        )

        // Simulamos éxito
        coEvery { usuarioApi.register(any()) } returns Response.success(usuarioDePrueba)

        val resultado = sessionRepository.register(usuarioDePrueba, "password123")

        assertTrue(resultado)
    }

    @Test
    fun `register - cuando la API devuelve un error - debe retornar false`() = runTest {
        val usuarioDePrueba = Usuario(
            id = null,
            nombre = "Test User",
            email = "test@test.com",
            edad = 25,
            password = "password123"
        )

        // Simulamos error 400
        coEvery { usuarioApi.register(any()) } returns Response.error(400, okhttp3.ResponseBody.create(null, ""))

        val resultado = sessionRepository.register(usuarioDePrueba, "password123")

        assertFalse(resultado)
    }

    @Test
    fun `register - cuando la API lanza una excepción - debe retornar false`() = runTest {
        val usuarioDePrueba = Usuario(
            id = null,
            nombre = "Test User",
            email = "test@test.com",
            edad = 25,
            password = "password123"
        )

        // Simulamos caída de red
        coEvery { usuarioApi.register(any()) } throws IOException("Error de red")

        val resultado = sessionRepository.register(usuarioDePrueba, "password123")

        assertFalse(resultado)
    }
}