package com.dam2jms.gestiongastosapp.states

import java.time.LocalDate

data class GraficosUiState(
    val balanceTotal: Double = 0.0,
    val totalIngresos: Double = 0.0,
    val totalGastos: Double = 0.0,
    val ingresos: List<Double> = emptyList(),
    val gastos: List<Double> = emptyList(),
    val fechas: List<LocalDate> = emptyList(),
    val evolucionBalance: List<Double> = emptyList(),
    val gastosPorCategoria: Map<String, Double> = emptyMap(),
    val balanceInicial: Double = 0.0,
    val balanceFinal: Double = 0.0,
    val monedaActual: String = "EUR"
)
