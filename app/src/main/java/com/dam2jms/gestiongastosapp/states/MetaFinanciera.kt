package com.dam2jms.gestiongastosapp.states

data class MetaFinanciera(
    val id: String = "",
    val nombre: String = "",
    val montoObjetivo: Double = 0.0,
    val montoActual: Double = 0.0,  // Cantidad ahorrada
    val fechaLimite: String = "",
    val completada: Boolean = false,
    val progreso: Double = 0.0 // Progreso en porcentaje
)