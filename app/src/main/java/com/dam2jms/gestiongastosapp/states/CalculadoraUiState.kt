package com.dam2jms.gestiongastosapp.states

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@RequiresApi(Build.VERSION_CODES.O)
data class CalculadoraUiState(

    //para la de prestamos
    val cantidad: String = "",
    val tasaAnual: String = "",
    val plazoMeses: String = "",
    val cuotaPrestamo: Double = 0.0,
    val tablaPrestamo: List<FilaPrestamo> = emptyList(),
    val totalIntereses: Double = 0.0,

    //para la division de gastos
    val totalPersonas: Int = 0,
    val cantidadPorPersona: Double = 0.0,
    var cantidadTotal: Double = 0.0,
    var numeroPersonas: Int = 0,

    //para la del retorno de inversion
    val roi: Double = 0.0,
    val gananciaTotal: Double = 0.0,
    var ganancia: Double = 0.0,
    var inversion: Double = 0.0,

    //para la de inflacion
    val perdidaPoderAdquisitivo: Double = 0.0,
    val cantidadAjustadaInflacion: Double = 0.0,
    var tasaInflacion: Double = 0.0,
    var años: Int = 0

)
