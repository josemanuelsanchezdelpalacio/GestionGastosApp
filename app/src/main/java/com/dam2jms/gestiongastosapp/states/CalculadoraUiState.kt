package com.dam2jms.gestiongastosapp.states

import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
data class CalculadoraUiState(
    val metaFinanciera: Double = 0.0,
    val progresoMeta: Double = 0.0,
    val cuotaPrestamo: Double = 0.0,
    val tablaPrestamo: List<FilaPrestamo> = emptyList(),
    val ahorroMensualNecesario: Double = 0.0,
    val montoFinalJubilacion: Double = 0.0,
    val monedaActual: String = "€",
    val ingresosTotales: Double = 0.0,
    val gastosTotales: Double = 0.0,
    val balanceTotal: Double = 0.0,
    val montoConvertido: Double = 0.0,
    val totalIntereses: Double = 0.0,
    val perdidaPoderAdquisitivo: Double = 0.0,
    val montoAjustadoInflacion: Double = 0.0,
    val roi: Double = 0.0,
    val gananciaTotal: Double = 0.0,
    val totalPersonas: Int = 0,
    val cantidadPorPersona: Double = 0.0,
    val montoFuturo: Double = 0.0
)
