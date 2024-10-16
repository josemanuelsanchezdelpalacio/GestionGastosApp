package com.dam2jms.gestiongastosapp.models

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.dam2jms.gestiongastosapp.components.ItemComponents.obtenerIconoCategoria
import com.dam2jms.gestiongastosapp.data.Categoria
import com.dam2jms.gestiongastosapp.states.TransactionState
import com.dam2jms.gestiongastosapp.states.UiState
import com.dam2jms.gestiongastosapp.ui.theme.naranjaClaro
import com.dam2jms.gestiongastosapp.utils.FireStoreUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditTransactionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun cargarTransaccion(transactionId: String) {
        FireStoreUtil.obtenerTransacciones(
            onSuccess = { transacciones ->
                val transaccion = transacciones.find { it.id == transactionId }
                transaccion?.let { transaction ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            cantidad = transaction.cantidad,
                            categoria = transaction.categoria,
                            tipo = transaction.tipo,
                            fechaTransaccion = transaction.fecha
                        )
                    }
                }
            },
            onFailure = { /* Handle error */ }
        )
    }

    fun actualizarDatosTransaccion(cantidad: String, categoria: String, tipo: String, fecha: String) {
        _uiState.update { currentState ->
            currentState.copy(
                cantidad = cantidad.toDoubleOrNull() ?: currentState.cantidad,
                categoria = categoria,
                tipo = tipo,
                fechaTransaccion = fecha
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editarTransaccion(transaccion: TransactionState, context: Context) {
        val nombreColeccion = if (transaccion.tipo == "ingreso") "ingresos" else "gastos"

        FireStoreUtil.editarTransaccion(
            coleccion = nombreColeccion,
            transaccion = transaccion,
            onSuccess = {
                Toast.makeText(context, "${transaccion.tipo.capitalize()} editado con éxito", Toast.LENGTH_SHORT).show()
                leerTransacciones()
            },
            onFailure = { exception ->
                Toast.makeText(context, "Error al editar el ${transaccion.tipo}: ${exception.message}", Toast.LENGTH_SHORT).show()
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
            onFailure = { /* Manejo de errores */ }
        )
    }
}