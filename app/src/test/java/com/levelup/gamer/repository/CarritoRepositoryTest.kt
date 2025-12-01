package com.levelup.gamer.repository

import com.levelup.gamer.api.CarritoApi
import com.levelup.gamer.model.CarritoRequest
import com.levelup.gamer.model.CarritoResponse
import com.levelup.gamer.model.Producto
import io.mockk.MockKAnnotations
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
import retrofit2.Response

@ExperimentalCoroutinesApi
class CarritoRepositoryTest {

    @RelaxedMockK
    private lateinit var carritoApi: CarritoApi

    private lateinit var carritoRepository: CarritoRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        carritoRepository = CarritoRepository(carritoApi)
    }

    @Test
    fun `listar - debe retornar lista de CarritoResponse (IDs)`() = runTest {
       
        val listaFalsa = listOf(
            CarritoResponse(id = 1, usuarioId = 1L, productoId = 101L, cantidad = 2),
            CarritoResponse(id = 2, usuarioId = 1L, productoId = 102L, cantidad = 5)
        )


        coEvery { carritoApi.getCarrito(1L) } returns listaFalsa

        val resultado = carritoRepository.listar(1L)


        assertEquals(2, resultado.size)
        assertEquals(101L, resultado[0].productoId)
    }

    @Test
    fun `agregar - debe convertir Producto a CarritoRequest (IDs) y enviarlo`() = runTest {

        val productoDePrueba = Producto(101L, "P001", "Test", "Test", 100, "", "", 10, false, 0f, 0)
        val usuarioId = 1L
        val cantidad = 3


        val slot = slot<CarritoRequest>()


        coEvery { carritoApi.add(capture(slot)) } returns Response.success(Any())


        carritoRepository.agregar(usuarioId, productoDePrueba, cantidad)


        coVerify(exactly = 1) { carritoApi.add(any()) }


        assertEquals(productoDePrueba.id, slot.captured.productoId)
        assertEquals(usuarioId, slot.captured.usuarioId)
        assertEquals(cantidad, slot.captured.cantidad)
    }

    @Test
    fun `eliminar - debe llamar a delete`() = runTest {
        coEvery { carritoApi.delete(42L) } returns Response.success(Unit)
        carritoRepository.eliminar(42L)
        coVerify(exactly = 1) { carritoApi.delete(42L) }
    }

    @Test
    fun `actualizarCantidad - debe llamar a updateQuantity`() = runTest {

        coEvery { carritoApi.updateQuantity(10L, 5) } returns Response.success(Any())

        carritoRepository.actualizarCantidad(10L, 5)

        coVerify { carritoApi.updateQuantity(10L, 5) }
    }

    @Test
    fun `vaciar - debe llamar a clearCart`() = runTest {
        coEvery { carritoApi.clearCart(1L) } returns Response.success(Unit)

        carritoRepository.vaciar(1L)

        coVerify { carritoApi.clearCart(1L) }
    }
}