package com.levelup.gamer.utils

fun codigoToId(codigo: String): Long =
    codigo.filter { it.isDigit() }.toLong()
