package com.dam2jms.gestiongastosapp.models

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.states.TransactionUiState
import com.dam2jms.gestiongastosapp.states.UiState
import com.dam2jms.gestiongastosapp.ui.theme.colorFondo
import com.dam2jms.gestiongastosapp.ui.theme.grisClaro
import com.dam2jms.gestiongastosapp.ui.theme.naranjaClaro
import com.dam2jms.gestiongastosapp.utils.FireStoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class AuxViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val fireStoreUtil: FireStoreUtil = FireStoreUtil
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun onChange(email: String, password: String) {
        _uiState.update { it.copy(email = email, password = password) }
    }

    fun visibilidadPassword() {
        _uiState.update { it.copy(visibilidadPasssword = !_uiState.value.visibilidadPasssword) }
    }

    fun actualizarTransaccion(ingresos: List<TransactionUiState>, gastos: List<TransactionUiState>) {
        _uiState.update { it.copy(ingresos = ingresos, gastos = gastos) }
    }

    /**metodo para leer todas las transacciones en firestore y las categoriza en ingresos y gastos*/
    fun leerTransacciones(context: Context) {

        FireStoreUtil.obtenerTransacciones(
            onSuccess = { transacciones ->
                //filtro las transacciones segun el tipo
                val ingresos = transacciones.filter { it.tipo == "ingreso" }
                val gastos = transacciones.filter { it.tipo == "gasto" }

                //actualizo la UI con las listas de ingresos y gastos
                _uiState.update { it.copy(ingresos = ingresos, gastos = gastos) }
            },
            onFailure = { exception ->
                Toast.makeText(context, "Error al leer las transacciones: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun eliminarTransaccionExistente(tipo: String, transaccionId: String, context: Context) {

        val userId = Firebase.auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            try {
                FireStoreUtil.eliminarTransaccion(tipo, transaccionId, userId,
                    onSuccess = {
                        Toast.makeText(context, "Transaccon eliminada correctamente", Toast.LENGTH_SHORT).show()
                        leerTransacciones(context)
                    },
                    onFailure = { exception ->
                        Toast.makeText(context, "Error al eliminar la transaccion: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(context, "Error inesperado al eliminar la transaccion", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


