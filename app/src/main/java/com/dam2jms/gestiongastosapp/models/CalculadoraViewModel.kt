package com.dam2jms.gestiongastosapp.models

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam2jms.gestiongastosapp.states.CalculadoraUiState
import com.dam2jms.gestiongastosapp.states.FilaPrestamo
import com.dam2jms.gestiongastosapp.utils.FireStoreUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
class CalculadoraViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CalculadoraUiState())
    val uiState: StateFlow<CalculadoraUiState> = _uiState.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUserId: String?
        get() = Firebase.auth.currentUser?.uid

    init {
        viewModelScope.launch {
            actualizarResumenFinanciero()
        }
    }

    // Funciones de Meta Financiera
    fun actualizarMetaFinanciera(nuevaMeta: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(metaFinanciera = nuevaMeta) }
            guardarMetaEnFirestore(nuevaMeta)
        }
    }

    private fun guardarMetaEnFirestore(meta: Double) {
        currentUserId?.let { userId ->
            firestore.collection("users")
                .document(userId)
                .update("metaFinanciera", meta)
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error al guardar meta: ", e)
                }
        }
    }

    // Funciones de Resumen Financiero
    private fun actualizarResumenFinanciero() {
        currentUserId?.let { userId ->
            FireStoreUtil.obtenerTransacciones(
                onSuccess = { transacciones ->
                    val ingresos = transacciones.filter { it.tipo == "ingreso" }.sumOf { it.cantidad }
                    val gastos = transacciones.filter { it.tipo == "gasto" }.sumOf { it.cantidad }
                    val balance = ingresos - gastos

                    _uiState.update { currentState ->
                        currentState.copy(
                            ingresosTotales = ingresos,
                            gastosTotales = gastos,
                            balanceTotal = balance,
                            progresoMeta = calcularProgresoMeta(balance, currentState.metaFinanciera)
                        )
                    }
                },
                onFailure = { e ->
                    Log.e(TAG, "Error al obtener transacciones: ", e)
                }
            )

            // Obtener meta financiera guardada
            firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    document.getDouble("metaFinanciera")?.let { meta ->
                        _uiState.update { it.copy(metaFinanciera = meta) }
                    }
                }
        }
    }

    private fun calcularProgresoMeta(balance: Double, meta: Double): Double {
        return if (meta > 0) (balance / meta * 100).coerceIn(0.0, 100.0) else 0.0
    }

    fun calcularDivisionGasto(totalGasto: Double, numPersonas: Int): Double {
        return if (numPersonas > 0) {
            totalGasto / numPersonas
        } else {
            0.0
        }
    }

    // Funciones de Préstamo
    @RequiresApi(Build.VERSION_CODES.O)
    fun calcularPrestamo(montoPrestamo: Double, tasaInteresAnual: Double, años: Int) {
        viewModelScope.launch {
            try {
                val tasaMensual = tasaInteresAnual / 12 / 100
                val meses = años * 12
                val cuota = calcularCuotaMensual(montoPrestamo, tasaMensual, meses)
                val tablaPrestamo = generarTablaPrestamo(montoPrestamo, tasaMensual, cuota, meses)

                _uiState.update {
                    it.copy(
                        cuotaPrestamo = cuota,
                        tablaPrestamo = tablaPrestamo
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en cálculo de préstamo: ", e)
            }
        }
    }

    private fun calcularCuotaMensual(montoPrestamo: Double, tasaMensual: Double, meses: Int): Double {
        return montoPrestamo * tasaMensual * (1 + tasaMensual).pow(meses) / ((1 + tasaMensual).pow(meses) - 1)
    }

    private fun generarTablaPrestamo(
        montoPrestamo: Double,
        tasaMensual: Double,
        cuota: Double,
        meses: Int
    ): List<FilaPrestamo> {
        val tabla = mutableListOf<FilaPrestamo>()
        var capitalPendiente = montoPrestamo

        for (numeroCuota in 1..meses) {
            val interesMes = capitalPendiente * tasaMensual
            val amortizacion = cuota - interesMes
            capitalPendiente = maxOf(0.0, capitalPendiente - amortizacion)

            tabla.add(
                FilaPrestamo(
                    numeroCuota = numeroCuota,
                    cuota = redondearDosDecimales(cuota),
                    interes = redondearDosDecimales(interesMes),
                    amortizacion = redondearDosDecimales(amortizacion),
                    capitalPendiente = redondearDosDecimales(capitalPendiente)
                )
            )
        }
        return tabla
    }

    // Función auxiliar para redondear a dos decimales
    private fun redondearDosDecimales(valor: Double): Double {
        return (valor * 100).roundToInt() / 100.0
    }

    // Funciones de Jubilación
    fun calcularPlanJubilacion(
        edadActual: Int,
        edadJubilacion: Int,
        gastosMensualesDeseados: Double,
        ahorroActual: Double,
        rendimientoAnualEsperado: Double,
        inflacionEstimada: Double = 2.0
    ) {
        viewModelScope.launch {
            try {
                val ahorroMensual = calcularAhorroMensualNecesario(
                    edadActual, edadJubilacion, gastosMensualesDeseados,
                    ahorroActual, rendimientoAnualEsperado, inflacionEstimada
                )

                _uiState.update {
                    it.copy(
                        ahorroMensualNecesario = ahorroMensual,
                        montoFinalJubilacion = calcularMontoFinalJubilacion(ahorroMensual, edadActual, edadJubilacion)
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en cálculo de jubilación: ", e)
            }
        }
    }

    private fun calcularAhorroMensualNecesario(
        edadActual: Int,
        edadJubilacion: Int,
        gastosMensualesDeseados: Double,
        ahorroActual: Double,
        rendimientoAnualEsperado: Double,
        inflacionEstimada: Double
    ): Double {
        val añosHastaJubilacion = edadJubilacion - edadActual
        val añosDeJubilacion = ESPERANZA_VIDA - edadJubilacion
        val factorInflacion = (1 + inflacionEstimada / 100).pow(añosHastaJubilacion)
        val gastosMensualesAjustados = gastosMensualesDeseados * factorInflacion
        val gastosTotalesNecesarios = gastosMensualesAjustados * 12 * añosDeJubilacion
        val tasaReal = (1 + rendimientoAnualEsperado / 100) / (1 + inflacionEstimada / 100) - 1
        val tasaMensualReal = tasaReal / 12

        return (gastosTotalesNecesarios - ahorroActual * (1 + tasaMensualReal).pow(añosHastaJubilacion * 12)) /
                (añosHastaJubilacion * 12)
    }

    private fun calcularMontoFinalJubilacion(
        ahorroMensual: Double,
        edadActual: Int,
        edadJubilacion: Int
    ): Double {
        val añosAhorro = edadJubilacion - edadActual
        return ahorroMensual * 12 * añosAhorro
    }

    companion object {
        private const val TAG = "CalculadoraViewModel"
        private const val ESPERANZA_VIDA = 85
    }
}