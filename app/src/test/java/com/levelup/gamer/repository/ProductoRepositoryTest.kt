package com.levelup.gamer.repository

import com.levelup.gamer.model.Producto
import com.levelup.gamer.remote.ProductoApiService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ProductoRepositoryTest {



    @RelaxedMockK
    private lateinit var productoApiService: ProductoApiService

    private lateinit var repository: ProductoRepository

    @Before
    fun setUp() {
        // 2. Iniciamos MockK manualmente
        MockKAnnotations.init(this)

        repository = ProductoRepository(productoApiService)
    }

    @Test
    fun `cuando getAllProductos es llamado, debe retornar la lista de productos de la API`() = runTest {
        // Datos de prueba
        val listaDeProductosFalsos = listOf(
            Producto(1, "P001", "Consola", "PS5", 500000, "", "", 10, true, 4.5f, 100),
            Producto(2, "P002", "Juego", "FIFA 25", 70000, "", "", 50, false, 4.0f, 200)
        )

        // Comportamiento del Mock
        coEvery { productoApiService.getProductos() } returns listaDeProductosFalsos

        // Llamada al repositorio (ya no necesitamos runBlocking dentro porque usamos runTest)
        val resultado = repository.getAllProductos()

        // Verificaciones
        assertEquals(2, resultado.size)
        assertEquals("PS5", resultado[0].nombre)
        assertEquals(listaDeProductosFalsos, resultado)
    }
}