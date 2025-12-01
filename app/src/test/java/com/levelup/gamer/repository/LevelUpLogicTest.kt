package com.levelup.gamer

import com.levelup.gamer.model.Usuario
import org.junit.Test
import org.junit.Assert.*


class LevelUpLogicTest {

    // 1. PRUEBA DEL MODELO DE DATOS
    @Test
    fun `crear usuario asigna valores correctamente`() {
        val user = Usuario(
            id = 1,
            nombre = "God Boy",
            email = "test@duoc.cl",
            edad = 25,
            puntos = 1500,
            nivel = 5,
            esDuoc = true,
            password = null
        )

        assertEquals("God Boy", user.nombre)
        assertEquals(1500, user.puntos)
        assertTrue(user.esDuoc)
    }

    // 2. PRUEBA DE LA LÓGICA DE NIVELES (La misma lógica que usas en tu Repo)
    @Test
    fun `calculo de nivel basado en puntos es correcto`() {


        assertEquals(5, calcularNivel(1200))
        assertEquals(5, calcularNivel(1000))
        assertEquals(4, calcularNivel(800))
        assertEquals(3, calcularNivel(400))
        assertEquals(2, calcularNivel(150))
        assertEquals(1, calcularNivel(50))
        assertEquals(1, calcularNivel(0))
    }


    private fun calcularNivel(puntos: Int): Int {
        return when {
            puntos >= 1000 -> 5
            puntos >= 600 -> 4
            puntos >= 300 -> 3
            puntos >= 120 -> 2
            else -> 1
        }
    }
}