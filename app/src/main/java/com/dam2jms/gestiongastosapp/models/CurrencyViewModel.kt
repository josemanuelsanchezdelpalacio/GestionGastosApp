package com.dam2jms.gestiongastosapp.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam2jms.gestiongastosapp.data.CurrencyConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Currency
import java.util.Locale

class CurrencyViewModel : ViewModel() {

    private val monedaConvertida = CurrencyConverter()

    private val _resultadoConversion = MutableStateFlow<Map<String, Double>>(emptyMap())

    private val _monedasDisponibles = MutableStateFlow<List<String>>(emptyList())
    val monedasDisponibles: StateFlow<List<String>> = _monedasDisponibles

    private val monedasImportantes = listOf(
        "USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CHF", "CNY", "SEK", "NZD",
        "MXN", "SGD", "HKD", "NOK", "KRW", "TRY", "INR", "RUB", "BRL", "ZAR"
    )

    init {
        obtenerMonedasDisponibles()
    }

    private fun obtenerMonedasDisponibles() {
        viewModelScope.launch {
            val tasas = monedaConvertida.obtenerTasasMonedas("EUR")
            _monedasDisponibles.value = monedasImportantes.filter { it in tasas.keys }
        }
    }

    fun obtenerSimboloMoneda(codigoMoneda: String): String {
        return try {
            Currency.getInstance(codigoMoneda).symbol
        } catch (e: IllegalArgumentException) {
            codigoMoneda
        }
    }

    fun obtenerNombreMoneda(codigoMoneda: String): String {
        return try {
            val moneda = Currency.getInstance(codigoMoneda)
            val nombre = moneda.getDisplayName(Locale.getDefault())
            "$nombre ($codigoMoneda)"
        } catch (e: IllegalArgumentException) {
            codigoMoneda
        }
    }

    suspend fun obtenerTasaCambio(monedaOrigen: String, monedaDestino: String): Double {
        return try {
            val rates = monedaConvertida.obtenerTasasMonedas(monedaOrigen)
            rates[monedaDestino] ?: 1.0
        } catch (e: Exception) {
            1.0
        }
    }
}
