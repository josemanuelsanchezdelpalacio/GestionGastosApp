package com.dam2jms.gestiongastosapp.states

import com.dam2jms.gestiongastosapp.navigation.AppScreen

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
data class UiState(
    // Login and Register Screen
    val email: String = "",
    val password: String = "",
    val visibilidadPasssword: Boolean = false,

    // Home Screen
    val balanceTotal: Double = 0.0,
    val ingresosMensuales: Double = 0.0,
    val gastosMensuales: Double = 0.0,
    val ahorrosMensuales: Double = 0.0,
    val financialGoal: Double = 0.0,
    val diasHastaMeta: Int = 0,
    val monedaActual: String = "USD",
    val transaccionesRecientes: List<TransactionState> = emptyList(),
    val gastosPorCategoria: Map<String, Double> = emptyMap(),
    val ingresosPorCategoria: Map<String, Double> = emptyMap(),
    val consejosFinancieros: List<String> = emptyList(),
    val tasaAhorro: Double = 0.0,
    val promedioGastoDiario: Double = 0.0,
    val ingresosTotales: Double = ingresosPorCategoria.values.sum(),
    val gastosTotales: Double = gastosPorCategoria.values.sum(),

    // History Screen
    val transaccionesFiltradas: List<TransactionState> = emptyList(),

    // Edit Transaction Screen
    val cantidad: Double = 0.0,
    val categoria: String = "",
    val tipo: String = "",
    val fechaTransaccion: String = LocalDate.now().format(DateTimeFormatter.ISO_DATE), // New field for transaction date

    // Transaction ViewModel
    val gastos: List<TransactionState> = emptyList(),
    val ingresos: List<TransactionState> = emptyList(),

    // Home ViewModel
    val totalGastos: Double = 0.0,
    val ingresosDiarios: Double = 0.0,
    val gastosDiarios: Double = 0.0,
    val ahorrosDiarios: Double = 0.0,

    // Aux ViewModel
    var screenActual: AppScreen = AppScreen.HomeScreen
)
