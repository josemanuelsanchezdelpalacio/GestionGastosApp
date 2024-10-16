package com.dam2jms.gestiongastosapp.states

import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
data class TransactionState(
    var id: String = "",
    val cantidad: Double = 0.0,
    val tipo: String = "",
    val categoria: String = "",
    val fecha: String = "",
    val descripcion: String = ""
)

