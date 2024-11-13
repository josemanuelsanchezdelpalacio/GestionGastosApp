package com.dam2jms.gestiongastosapp.states

data class FilaPrestamo(
    val mes: Int,
    val cuota: Double,
    val interes: Double,
    val capital: Double,
    val saldoPendiente: Double
)