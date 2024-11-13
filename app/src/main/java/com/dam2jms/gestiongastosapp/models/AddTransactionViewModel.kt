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

    //para controlar el estado de la UI
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /** metodo para actualizar los datos de la transaccion en el UI*/
    fun actualizarDatosTransaccion(cantidad: String?, categoria: String?, tipo: String) {

        //actualizo la UI con los datos nuevos
        _uiState.update {
            it.copy(
                cantidad = cantidad?.toDoubleOrNull() ?: uiState.value.cantidad,
                categoria = categoria ?: uiState.value.categoria,
                tipo = tipo
            )
        }
    }

    /**metodo para crear una transaccion en firestore y actualizo la UI*/
    fun añadirTransaccion(context: Context, seleccionarFecha: LocalDate, onNavigate: (String) -> Unit) {

        if (uiState.value.cantidad > 0 && uiState.value.categoria.isNotEmpty() && uiState.value.tipo.isNotEmpty()) {
            val transaccion = TransactionUiState(
                cantidad = uiState.value.cantidad,
                categoria = uiState.value.categoria,
                tipo = uiState.value.tipo,
                fecha = seleccionarFecha.format(DateTimeFormatter.ISO_DATE)
            )

            //nombre de la coleccion
            val nombreColeccion = if (transaccion.tipo == "ingreso") "ingresos" else "gastos"

            viewModelScope.launch(Dispatchers.IO) {
                FireStoreUtil.añadirTransaccion(
                    coleccion = nombreColeccion,
                    transaccion = transaccion,
                    onSuccess = {
                        Toast.makeText(context, "${transaccion.tipo.capitalize()} agregado correctamente", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { exception ->
                        Toast.makeText(context, "Error al agregar el ${transaccion.tipo}: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        } else {
            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

}


