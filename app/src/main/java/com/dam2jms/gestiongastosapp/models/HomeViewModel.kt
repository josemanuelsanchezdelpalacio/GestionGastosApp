package com.dam2jms.gestiongastosapp.models

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
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
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val db = Firebase.firestore
    private val monedasViewModel = MonedasViewModel()

    //inicializacion y carga de datos
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
                        transaccionesRecientes = transacciones.sortedByDescending { it.fecha }.take(10)
                    )
                }
                actualizarBalances()
                actualizarIngresosGastosDiarios()
                actualizarIngresosGastosMensuales()
                actualizarIngresosGastosAnuales()
            },
            onFailure = { e ->
                Log.e("HomeViewModel", "Error al leer transacciones: ${e.message}")
            }
        )
    }

    private fun cargarMetaFinanciera() {

        val userId = Firebase.auth.currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val metaFinanciera = document.getDouble("metaFinanciera") ?: 0.0
                    val fechaMeta = document.getString("fechaMeta")?.let {
                        try {
                            LocalDate.parse(it)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    val metaId = document.getString("financialGoalId") ?: ""

                    _uiState.update { currentState ->
                        currentState.copy(
                            objetivoFinanciero = metaFinanciera,
                            fechaObjetivo = fechaMeta,
                            idObjetivoFinanciero = metaId
                        )
                    }
                    fechaMeta?.let { establecerFechaMeta(it) }
                }
            }
            .addOnFailureListener { e ->
                Log.e("HomeViewModel", "Error al cargar la meta financiera: ${e.message}")
            }
    }

    private fun actualizarBalances() {

        val ingresosTotales = _uiState.value.ingresos.sumOf { it.cantidad }
        val gastosTotales = _uiState.value.gastos.sumOf { it.cantidad }
        val balanceTotal = ingresosTotales - gastosTotales
        val ahorroTotal = ingresosTotales - gastosTotales

        _uiState.update {
            it.copy(
                balanceTotal = balanceTotal,
                ahorrosTotales = ahorroTotal,
                progresoMeta = calcularProgresoMeta()
            )
        }
    }

    private fun actualizarIngresosGastosDiarios() {

        val hoy = LocalDate.now()

        val ingresosDiarios = _uiState.value.ingresos
            .filter { LocalDate.parse(it.fecha) == hoy }
            .sumOf { it.cantidad }
        val gastosDiarios = _uiState.value.gastos
            .filter { LocalDate.parse(it.fecha) == hoy }
            .sumOf { it.cantidad }

        _uiState.update {
            it.copy(
                ingresosDiarios = ingresosDiarios,
                gastosDiarios = gastosDiarios
            )
        }
    }

    private fun actualizarIngresosGastosMensuales() {

        val hoy = LocalDate.now()

        val ingresosMensuales = _uiState.value.ingresos
            .filter { LocalDate.parse(it.fecha).month == hoy.month }
            .sumOf { it.cantidad }
        val gastosMensuales = _uiState.value.gastos
            .filter { LocalDate.parse(it.fecha).month == hoy.month }
            .sumOf { it.cantidad }

        _uiState.update {
            it.copy(
                ingresosMensuales = ingresosMensuales,
                gastosMensuales = gastosMensuales
            )
        }
    }

    private fun actualizarIngresosGastosAnuales() {

        val hoy = LocalDate.now()

        val ingresosAnuales = _uiState.value.ingresos
            .filter { LocalDate.parse(it.fecha).year == hoy.year }
            .sumOf { it.cantidad }
        val gastosAnuales = _uiState.value.gastos
            .filter { LocalDate.parse(it.fecha).year == hoy.year }
            .sumOf { it.cantidad }

        _uiState.update {
            it.copy(
                ingresosAnuales = ingresosAnuales,
                gastosAnuales = gastosAnuales
            )
        }
    }

    fun calcularProgresoMeta(): Double {
        val balanceTotal = _uiState.value.balanceTotal
        val metaFinanciera = _uiState.value.objetivoFinanciero

        return if (metaFinanciera > 0) {
            ((balanceTotal / metaFinanciera) * 100).coerceIn(0.0, 100.0)
        } else {
            0.0
        }
    }

    fun establecerMetaFinanciera(meta: Double, fechaMeta: LocalDate) {
        _uiState.update {
            it.copy(
                objetivoFinanciero = meta,
                fechaObjetivo = fechaMeta,
                diasHastaMeta = ChronoUnit.DAYS.between(LocalDate.now(), fechaMeta).toInt()
            )
        }
        actualizarBalances()
        guardarMetaEnFirebase(meta, fechaMeta)
    }

    private fun guardarMetaEnFirebase(meta: Double, fechaMeta: LocalDate) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .update(mapOf("metaFinanciera" to meta, "fechaMeta" to fechaMeta.toString()))
            .addOnFailureListener { e ->
                Log.e("HomeViewModel", "Error al guardar meta en Firebase: ${e.message}")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun establecerFechaMeta(fechaMeta: LocalDate) {
        val metaFinanciera = _uiState.value.objetivoFinanciero
        if (metaFinanciera <= 0) {
            _uiState.update {
                it.copy(
                    fechaObjetivo = fechaMeta,
                    diasHastaMeta = -1,
                    estadoMeta = "Establece primero una meta financiera"
                )
            }
            return
        }

        val diasDiferencia = ChronoUnit.DAYS.between(LocalDate.now(), fechaMeta).toInt()
        if (diasDiferencia <= 0) {
            _uiState.update {
                it.copy(
                    fechaObjetivo = fechaMeta,
                    diasHastaMeta = -1,
                    estadoMeta = "La fecha meta debe ser posterior a hoy"
                )
            }
            return
        }

        val ahorroNecesarioTotal = metaFinanciera - _uiState.value.balanceTotal
        val ahorroDiarioNecesario = ahorroNecesarioTotal / diasDiferencia

        val estadoMeta = when {
            ahorroNecesarioTotal <= 0 -> "Has alcanzado tu objetivo"
            else -> "Debes ahorrar ${String.format("%.2f", ahorroDiarioNecesario)} diarios para alcanzar tu meta"
        }

        _uiState.update {
            it.copy(
                fechaObjetivo = fechaMeta,
                diasHastaMeta = diasDiferencia,
                ahorroDiarioNecesario = ahorroDiarioNecesario,
                progresoMeta = calcularProgresoMeta(),
                estadoMeta = estadoMeta
            )
        }
        actualizarMetaEnFirebase(fechaMeta)
    }

    private fun actualizarMetaEnFirebase(fechaMeta: LocalDate) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .update(
                mapOf(
                    "fechaMeta" to fechaMeta.toString(),
                    "diasHastaMeta" to _uiState.value.diasHastaMeta,
                    "ahorroDiarioNecesario" to _uiState.value.ahorroDiarioNecesario,
                    "progresoMeta" to _uiState.value.progresoMeta
                )
            )
            .addOnFailureListener { e ->
                Log.e("HomeViewModel", "Error al actualizar la meta en Firebase: ${e.message}")
            }
    }


    fun eliminarMeta(context: Context) {

        val idUsuario = Firebase.auth.currentUser?.uid

        if (idUsuario == null) {
            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        //actualizo el estado local
        _uiState.update { currentState ->
            currentState.copy(
                objetivoFinanciero = 0.0,
                fechaObjetivo = null,
                diasHastaMeta = -1,
                estadoMeta = "",
                ahorroDiarioNecesario = 0.0,
                progresoMeta = 0.0,
                idObjetivoFinanciero = ""
            )
        }

        //actualizo firestore
        FireStoreUtil.eliminarMetaFinanciera(
            idUsuario = idUsuario,
            onSuccess = {
                Toast.makeText(context, "Meta eliminada con éxito", Toast.LENGTH_SHORT).show()
            },
            onFailure = { e ->
                Toast.makeText(context, "Error al eliminar la meta: ${e.message}", Toast.LENGTH_SHORT).show()
                cargarMetaFinanciera()
            }
        )
    }

    fun actualizarMoneda(nuevaMoneda: String) {
        viewModelScope.launch {
            val tasaCambio = try {
                monedasViewModel.obtenerTasaCambio(_uiState.value.monedaActual, nuevaMoneda)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al obtener tasa de cambio: ${e.message}")
                1.0
            }

            _uiState.update { currentState ->
                currentState.copy(
                    ingresosDiarios = currentState.ingresosDiarios * tasaCambio,
                    gastosDiarios = currentState.gastosDiarios * tasaCambio,
                    ingresosMensuales = currentState.ingresosMensuales * tasaCambio,
                    gastosMensuales = currentState.gastosMensuales * tasaCambio,
                    ingresosAnuales = currentState.ingresosAnuales * tasaCambio,
                    gastosAnuales = currentState.gastosAnuales * tasaCambio,
                    balanceTotal = currentState.balanceTotal * tasaCambio,
                    objetivoFinanciero = currentState.objetivoFinanciero * tasaCambio,
                    monedaActual = nuevaMoneda
                )
            }
        }
    }
}

