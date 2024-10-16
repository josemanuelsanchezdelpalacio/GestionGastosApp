package com.dam2jms.gestiongastosapp.states

data class FilaPrestamo(
    val numeroCuota: Int,
    val cuota: Double,
    val interes: Double,
    val amortizacion: Double,
    val capitalPendiente: Double
)
