package com.dam2jms.gestiongastosapp.models

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.dam2jms.gestiongastosapp.states.TransactionUiState
import com.dam2jms.gestiongastosapp.states.UiState
import com.dam2jms.gestiongastosapp.utils.FireStoreUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
    @RequiresApi(Build.VERSION_CODES.O)
    fun crearTransaccion(transaccion: TransactionUiState, context: Context) {

        //nombre de la coleccion
        val nombreColeccion = if (transaccion.tipo == "ingreso") "ingresos" else "gastos"

        //datos de la transaccion creada por el usuario usando la fecha seleccionada
        val datosTransaccion = transaccion.copy(fecha = transaccion.fecha)

        //uso FireStoreUtil para aañadir la transaccion a la BD
        FireStoreUtil.añadirTransaccion(
            coleccion = nombreColeccion,
            transaccion = datosTransaccion,
            onSuccess = {
                Toast.makeText(context, "${transaccion.tipo.capitalize()} agregado con éxito", Toast.LENGTH_SHORT).show()
                //recargo las transacciones que se muestran
                AuxViewModel().leerTransacciones(context)
            },
            onFailure = { exception ->
                Toast.makeText(context, "Error al agregar el ${transaccion.tipo}: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}


