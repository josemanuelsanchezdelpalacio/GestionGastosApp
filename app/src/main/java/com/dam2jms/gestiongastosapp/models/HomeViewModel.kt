package com.dam2jms.gestiongastosapp.models

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam2jms.gestiongastosapp.states.UiState
import com.dam2jms.gestiongastosapp.utils.FireStoreUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private val db = Firebase.firestore

    private val currencyViewModel = CurrencyViewModel()

    init {
        leerTransacciones()
        cargarMetaFinanciera()
    }

    private fun leerTransacciones() {
        FireStoreUtil.obtenerTransacciones(
            onSuccess = { transacciones ->
                val ingresos = transacciones.filter { it.tipo == "ingreso" }
                val gastos = transacciones.filter { it.tipo == "gasto" }
                _uiState.update { currentState ->
                    currentState.copy(
                        ingresos = ingresos,
                        gastos = gastos,
                        transaccionesRecientes = transacciones.sortedByDescending { it.fecha }.take(5)
                    )
                }
                actualizarBalances()
                actualizarGastosPorCategoria()
                actualizarConsejosFinancieros()
            },
            onFailure = { e ->
                Log.e("HomeViewModel", "Error al leer transacciones: ${e.message}")
            }
        )
    }

    private fun actualizarBalances() {
        val currentState = _uiState.value
        val hoy = LocalDate.now()
        val inicioMes = YearMonth.now().atDay(1)
        val ingresosDiarios = currentState.ingresos.filter { it.fecha == hoy.toString() }.sumOf { it.cantidad }
        val gastosDiarios = currentState.gastos.filter { it.fecha == hoy.toString() }.sumOf { it.cantidad }
        val ingresosMensuales = currentState.ingresos.filter { LocalDate.parse(it.fecha).isAfter(inicioMes.minusDays(1)) }.sumOf { it.cantidad }
        val gastosMensuales = currentState.gastos.filter { LocalDate.parse(it.fecha).isAfter(inicioMes.minusDays(1)) }.sumOf { it.cantidad }
        val balanceTotal = ingresosMensuales - gastosMensuales
        val ahorrosDiarios = ingresosDiarios - gastosDiarios
        val ahorrosMensuales = ingresosMensuales - gastosMensuales
        val promedioGastoDiario = if (hoy.dayOfMonth > 0) gastosMensuales / hoy.dayOfMonth else 0.0
        val tasaAhorro = if (ingresosMensuales > 0) (ahorrosMensuales / ingresosMensuales) * 100 else 0.0

        _uiState.update {
            it.copy(
                ingresosDiarios = ingresosDiarios,
                gastosDiarios = gastosDiarios,
                ingresosMensuales = ingresosMensuales,
                gastosMensuales = gastosMensuales,
                balanceTotal = balanceTotal,
                ahorrosDiarios = ahorrosDiarios,
                ahorrosMensuales = ahorrosMensuales,
                promedioGastoDiario = promedioGastoDiario,
                tasaAhorro = tasaAhorro
            )
        }
    }

    private fun actualizarGastosPorCategoria() {
        val gastosPorCategoria = _uiState.value.gastos
            .groupBy { it.categoria }
            .mapValues { (_, transacciones) -> transacciones.sumOf { it.cantidad } }
        _uiState.update { it.copy(gastosPorCategoria = gastosPorCategoria) }
    }

    fun actualizarMoneda(nuevaMoneda: String) {
        viewModelScope.launch {
            val monedaActual = _uiState.value.monedaActual
            val tasaCambio = try {
                currencyViewModel.obtenerTasaCambio(monedaActual, nuevaMoneda)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al obtener tasa de cambio: ${e.message}")
                1.0
            }

            _uiState.update { currentState ->
                currentState.copy(
                    ingresosDiarios = (currentState.ingresosDiarios * tasaCambio),
                    gastosDiarios = (currentState.gastosDiarios * tasaCambio),
                    ingresosMensuales = (currentState.ingresosMensuales * tasaCambio),
                    gastosMensuales = (currentState.gastosMensuales * tasaCambio),
                    ahorrosDiarios = (currentState.ahorrosDiarios * tasaCambio),
                    ahorrosMensuales = (currentState.ahorrosMensuales * tasaCambio),
                    balanceTotal = (currentState.balanceTotal * tasaCambio),
                    monedaActual = nuevaMoneda
                )
            }
            actualizarGastosPorCategoria(tasaCambio)
        }
    }

    private fun actualizarGastosPorCategoria(tasaCambio: Double) {
        val gastosPorCategoria = _uiState.value.gastosPorCategoria.mapValues { (_, valor) ->
            valor * tasaCambio
        }
        _uiState.update { it.copy(gastosPorCategoria = gastosPorCategoria) }
    }

    fun obtenerCategoriaMasGastada(): Pair<String, Double>? {
        return _uiState.value.gastosPorCategoria.maxByOrNull { it.value }?.toPair()
    }

    fun calcularPorcentajeAhorro(): Double {
        val ingresosMensuales = _uiState.value.ingresosMensuales
        val ahorrosMensuales = _uiState.value.ahorrosMensuales
        return if (ingresosMensuales > 0) (ahorrosMensuales / ingresosMensuales) * 100 else 0.0
    }

    fun actualizarConsejosFinancieros() {
        val consejos = mutableListOf<String>()
        val tasaAhorro = calcularPorcentajeAhorro()

        if (tasaAhorro < 20) {
            consejos.add("Intenta aumentar tu tasa de ahorro al 20% de tus ingresos.")
        }

        val categoriaMasGastada = obtenerCategoriaMasGastada()
        categoriaMasGastada?.let { (categoria, gasto) ->
            consejos.add("Considera reducir tus gastos en $categoria, que es tu categoría de mayor gasto.")
        }

        if (_uiState.value.gastosMensuales > _uiState.value.ingresosMensuales) {
            consejos.add("Tus gastos superan tus ingresos. Busca formas de reducir gastos o aumentar ingresos.")
        }

        _uiState.update { it.copy(consejosFinancieros = consejos) }
    }

    private fun cargarMetaFinanciera() {
        val userId = Firebase.auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val meta = document.getDouble("metaFinanciera") ?: 0.0
                val ahorrosMensuales = _uiState.value.ahorrosMensuales
                val mesesHastaMeta = if (ahorrosMensuales > 0) (meta / ahorrosMensuales).toInt() else Int.MAX_VALUE
                val diasHastaMeta = mesesHastaMeta * 30 // Aproximación

                _uiState.update {
                    it.copy(
                        financialGoal = meta,
                        diasHastaMeta = diasHastaMeta
                    )
                }
            }
            .addOnFailureListener { e ->
                Log.e("HomeViewModel", "Error al leer la meta financiera: ${e.message}")
            }
    }
}
