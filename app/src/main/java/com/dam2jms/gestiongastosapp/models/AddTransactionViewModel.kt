package com.dam2jms.gestiongastosapp.models

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.dam2jms.gestiongastosapp.states.TransactionState
import com.dam2jms.gestiongastosapp.states.UiState
import com.dam2jms.gestiongastosapp.utils.FireStoreUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@RequiresApi(Build.VERSION_CODES.O)
class AddTransactionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun actualizarDatosTransaccion(cantidad: String?, categoria: String?, tipo: String) {
        _uiState.update {
            it.copy(
                cantidad = cantidad?.toDoubleOrNull() ?: uiState.value.cantidad,
                categoria = categoria ?: uiState.value.categoria,
                tipo = tipo
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun agregarTransaccion(transaccion: TransactionState, context: Context) {
        val nombreColeccion = if (transaccion.tipo == "ingreso") "ingresos" else "gastos"
        val nuevaTransaccion = transaccion.copy(fecha = transaccion.fecha)

        FireStoreUtil.añadirTransaccion(
            coleccion = nombreColeccion,
            transaccion = nuevaTransaccion,
            onSuccess = {
                Toast.makeText(context, "${transaccion.tipo.capitalize()} agregado con éxito", Toast.LENGTH_SHORT).show()
                leerTransacciones()
            },
            onFailure = { exception ->
                Toast.makeText(context, "Error al agregar el ${transaccion.tipo}: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun leerTransacciones() {
        FireStoreUtil.obtenerTransacciones(
            onSuccess = { transacciones ->
                val ingresos = transacciones.filter { it.tipo == "ingreso" }
                val gastos = transacciones.filter { it.tipo == "gasto" }
                _uiState.update { it.copy(ingresos = ingresos, gastos = gastos) }
            },
            onFailure = { }
        )
    }
}