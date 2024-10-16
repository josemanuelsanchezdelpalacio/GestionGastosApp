package com.dam2jms.gestiongastosapp.models

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.dam2jms.gestiongastosapp.states.TransactionState
import com.dam2jms.gestiongastosapp.states.UiState
import com.dam2jms.gestiongastosapp.ui.theme.*
import com.dam2jms.gestiongastosapp.utils.FireStoreUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class TransactionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        leerTransacciones()
    }

    fun actualizarTransaccion(ingresos: List<TransactionState>, gastos: List<TransactionState>) {
        _uiState.update { it.copy(ingresos = ingresos, gastos = gastos) }
    }

    fun leerTransacciones() {
        FireStoreUtil.obtenerTransacciones(
            onSuccess = { transacciones ->
                val ingresos = transacciones.filter { it.tipo == "ingreso" }
                val gastos = transacciones.filter { it.tipo == "gasto" }
                actualizarTransaccion(ingresos, gastos)
            },
            onFailure = { exception ->
            }
        )
    }

    fun eliminarTransaccionExistente(coleccion: String, transaccionId: String, context: Context) {
        FireStoreUtil.eliminarTransaccion(
            coleccion, transaccionId,
            onSuccess = {
                Toast.makeText(context, "Transacción eliminada correctamente", Toast.LENGTH_SHORT).show()
                leerTransacciones()
            },
            onFailure = {
                Toast.makeText(context, "Error al eliminar la transacción", Toast.LENGTH_SHORT).show()
            }
        )
    }

    @Composable
    fun horizontalCalendar(fechaSeleccionada: LocalDate, onDateSelected: (LocalDate) -> Unit) {
        val fechas = remember {
            (0..30).map { LocalDate.now().minusDays(it.toLong()) }
        }

        LazyRow(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(fechas) { fecha ->
                val seleccionada = fecha == fechaSeleccionada
                val background = if (seleccionada) naranjaClaro else colorFondo
                val textColor = if (seleccionada) blanco else naranjaOscuro

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(background)
                        .border(1.dp, if (seleccionada) naranjaOscuro else gris, CircleShape)
                        .clickable { onDateSelected(fecha) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = fecha.dayOfMonth.toString(),
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    fun filtrarTransacciones(fecha: LocalDate, tipo: String): List<TransactionState> {
        val fechaString = fecha.format(DateTimeFormatter.ISO_DATE)
        return when (tipo) {
            "ingresos" -> _uiState.value.ingresos.filter { it.fecha == fechaString }
            "gastos" -> _uiState.value.gastos.filter { it.fecha == fechaString }
            else -> emptyList()
        }
    }
}

