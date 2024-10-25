package com.dam2jms.gestiongastosapp.models

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dam2jms.gestiongastosapp.states.GraficosUiState
import com.dam2jms.gestiongastosapp.states.TransactionState
import com.dam2jms.gestiongastosapp.utils.FireStoreUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
class GraficosViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GraficosUiState())
    val uiState: StateFlow<GraficosUiState> = _uiState.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val id_usuarioActual: String?
        get() = Firebase.auth.currentUser?.uid

    init {
        viewModelScope.launch {
            cargarDatosFinancieros()
        }
    }

    private fun cargarDatosFinancieros(){
        id_usuarioActual?.let { idUsuario ->
            FireStoreUtil.obtenerTransacciones(
                onSuccess = { transacciones ->
                    procesarTransacciones(transacciones)
                },
                onFailure = { exception ->
                    Log.e(TAG, "Error al cargar las transacciones: ${exception}")
                }
            )
        }
    }

    private fun procesarTransacciones(transacciones: List<TransactionState>) {
        try {
            val ingresos = transacciones.filter { it.tipo == "ingreso" }
            val gastos = transacciones.filter { it.tipo == "gasto" }

            val totalIngresos = ingresos.sumOf { it.cantidad }
            val totalGastos = gastos.sumOf { it.cantidad }
            val balanceTotal = totalIngresos - totalGastos

            // Agrupar por día en lugar de por mes
            val ingresosDiarios = obtenerTransaccionesDiarias(ingresos)
            val gastosDiarios = obtenerTransaccionesDiarias(gastos)

            val diasOrdenados = (ingresosDiarios.keys + gastosDiarios.keys)
                .toSortedSet()

            var balanceAcumulado = 0.0
            val evolucionBalance = diasOrdenados.map { dia ->
                val ingresosDia = ingresosDiarios[dia] ?: 0.0
                val gastosDia = gastosDiarios[dia] ?: 0.0
                balanceAcumulado += (ingresosDia - gastosDia)
                balanceAcumulado
            }

            val gastosPorCategoria = gastos
                .groupBy { it.categoria }
                .mapValues { (_, transacciones) ->
                    transacciones.sumOf { it.cantidad }
                }
                .toSortedMap()

            _uiState.update { estadoActual ->
                estadoActual.copy(
                    balanceTotal = balanceTotal,
                    totalIngresos = totalIngresos,
                    totalGastos = totalGastos,
                    ingresos = ingresosDiarios.values.toList(),
                    gastos = gastosDiarios.values.toList(),
                    fechas = diasOrdenados.toList(),
                    evolucionBalance = evolucionBalance,
                    gastosPorCategoria = gastosPorCategoria,
                    balanceInicial = evolucionBalance.firstOrNull() ?: 0.0,
                    balanceFinal = evolucionBalance.lastOrNull() ?: 0.0
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al procesar transacciones: ", e)
        }
    }

    private fun obtenerTransaccionesDiarias(transacciones: List<TransactionState>): Map<LocalDate, Double> {
        return transacciones
            .groupBy {
                LocalDate.parse(it.fecha)
            }
            .mapValues { (_, transaccionesDia) ->
                transaccionesDia.sumOf { it.cantidad }
            }
            .toSortedMap()
    }

    fun actualizarMoneda(nuevaMoneda: String, tasaCambio: Double) {

        viewModelScope.launch {
            try{
                _uiState.update { estadoActual ->
                    estadoActual.copy(
                        monedaActual = nuevaMoneda,
                        balanceTotal = estadoActual.balanceTotal * tasaCambio,
                        totalIngresos = estadoActual.totalIngresos * tasaCambio,
                        totalGastos = estadoActual.totalGastos * tasaCambio,
                        ingresos = estadoActual.ingresos.map { it * tasaCambio },
                        gastos = estadoActual.gastos.map { it * tasaCambio },
                        evolucionBalance = estadoActual.evolucionBalance.map { it * tasaCambio },
                        gastosPorCategoria = estadoActual.gastosPorCategoria.mapValues { it.value * tasaCambio },
                        balanceInicial = estadoActual.balanceInicial * tasaCambio,
                        balanceFinal = estadoActual.balanceFinal * tasaCambio
                    )
                }
            }catch (e: Exception){
                Log.e(TAG, "Error al actualizar moneda: ", e)
            }
        }
    }

    fun refrescarDatos(){
        viewModelScope.launch {
            cargarDatosFinancieros()
        }
    }

    companion object{
        private const val TAG = "GraficosViewModel"
    }
}

