package com.levelup.gamer.repository

import com.levelup.gamer.api.CarritoApi
import com.levelup.gamer.model.CarritoItemDto
import com.levelup.gamer.model.Producto
import io.mockk.MockKAnnotations // Import necesario para iniciar MockK
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CarritoRepositoryTest {

    // 1. ELIMINAMOS LA REGLA QUE DABA ERROR
    // @get:Rule
    // val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var carritoApi: CarritoApi

    private lateinit var carritoRepository: CarritoRepository

    @Before
    fun setUp() {
        // 2. INICIAMOS MOCKK MANUALMENTE AQUÍ
        MockKAnnotations.init(this)

        carritoRepository = CarritoRepository(carritoApi)
    }

    @Test
    fun `listar - cuando la API devuelve una lista - debe retornar la misma lista`() = runTest {

        val listaFalsa = listOf(
            CarritoItemDto(1, 1L, 101L, 2),
            CarritoItemDto(2, 1L, 102L, 5)
        )
        coEvery { carritoApi.getCarrito(1L) } returns listaFalsa

        val resultado = carritoRepository.listar(1L)

        assertEquals(2, resultado.size)
        assertEquals(listaFalsa, resultado)
    }

    @Test
    fun `agregar - debe llamar a la API con el DTO correcto`() = runTest {

        val productoDePrueba = Producto(101L, "P001", "Test", "Test Prod", 100, "", "", 10, false, 0f, 0)
        val usuarioId = 1L
        val cantidad = 3
        val slot = slot<CarritoItemDto>()

        // 3. CORRECCIÓN DEL ERROR "TYPE MISMATCH":
        // El error decía que la API espera devolver un CarritoItemDto, pero tú devolvías Unit.
        // Aquí le decimos que devuelva el mismo objeto que capturó (o uno nuevo).
        coEvery { carritoApi.add(capture(slot)) } answers { slot.captured }

        carritoRepository.agregar(usuarioId, productoDePrueba, cantidad)

        coVerify(exactly = 1) { carritoApi.add(any()) }

        assertEquals(usuarioId, slot.captured.usuarioId)
        assertEquals(productoDePrueba.id, slot.captured.productoId)
        assertEquals(cantidad, slot.captured.cantidad)
    }

    @Test
    fun `eliminar - debe llamar a la API con el ID correcto`() = runTest {

        val itemIdParaBorrar = 42L

        // coJustRun se usa cuando la función retorna Unit (Void).
        // Si aquí no te da error, significa que delete() sí retorna Unit.
        coJustRun { carritoApi.delete(itemIdParaBorrar) }

        carritoRepository.eliminar(itemIdParaBorrar)

        coVerify(exactly = 1) { carritoApi.delete(itemIdParaBorrar) }
    }
}