package com.dam2jms.gestiongastosapp.models

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.states.TransactionUiState
import com.dam2jms.gestiongastosapp.states.UiState
import com.dam2jms.gestiongastosapp.utils.FireStoreUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class AddTransactionViewModel : ViewModel() {
    // Estado de la UI para transacciones
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Método para actualizar datos de transacción
    fun actualizarDatosTransaccion(cantidad: String?, categoria: String?, tipo: String) {
        val cantidadDouble = cantidad?.toDoubleOrNull()
        if (cantidadDouble != null && cantidadDouble > 0) {
            _uiState.update {
                it.copy(
                    cantidad = cantidadDouble,
                    categoria = categoria ?: it.categoria,
                    tipo = tipo
                )
            }
        }
    }

    // Método para añadir transacción
    fun añadirTransaccion(context: Context, seleccionarFecha: LocalDate, onNavigate: (String) -> Unit) {
        val cantidad = uiState.value.cantidad
        val categoria = uiState.value.categoria
        val tipo = uiState.value.tipo

        if (cantidad > 0 && categoria.isNotEmpty() && tipo.isNotEmpty()) {

            val transaccion = TransactionUiState(
                cantidad = cantidad,
                categoria = categoria,
                tipo = tipo,
                fecha = seleccionarFecha.format(DateTimeFormatter.ISO_DATE)
            )

            // Guardar en Firestore
            val nombreColeccion = if (tipo == "ingreso") "ingresos" else "gastos"
            viewModelScope.launch(Dispatchers.IO) {
                FireStoreUtil.añadirTransaccion(
                    coleccion = nombreColeccion,
                    transaccion = transaccion,
                    onSuccess = {
                        Toast.makeText(context, "${tipo.capitalize()} agregado correctamente", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { exception ->
                        Toast.makeText(context, "Error al agregar el ${tipo}: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        } else {
            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }
}
