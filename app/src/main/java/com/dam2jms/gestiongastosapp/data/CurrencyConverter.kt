package com.dam2jms.gestiongastosapp.data

import android.util.Log
import com.google.api.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.URL

class CurrencyConverter {

    //url para las solicitudes a la api
    private val baseURL = "https://api.frankfurter.app"

    /**metodo que obtiene las tasas de cambio para una moneda
     * @param url para la solicitud
     * @param respuesta lee la respuesta de la URL
     * @param jsonObject convierte la respuesta en un objeto JSON
     * @param tasas extrae del JSON las tasas de cambio
     * @return devuelve las tasas de cambio en un Map clave-valor
     * */
    suspend fun obtenerTasasMonedas(moneda: String): Map<String, Double>{
        return withContext(Dispatchers.IO){
            try {
                val url = URL("$baseURL/latest?from=$moneda")
                val respuesta = url.readText()
                val jsonObject = JSONObject(respuesta)
                val tasas = jsonObject.getJSONObject("rates")
                tasas.keys().asSequence().associateWith { tasas.getDouble(it) }
            } catch (e: FileNotFoundException) {
                Log.e("CurrencyConverter", "URL no encontrada: ${e.message}")
                emptyMap<String, Double>()
            } catch (e: Exception) {
                Log.e("CurrencyConverter", "Error: ${e.message}")
                emptyMap<String, Double>()
            }
        }
    }

    /**metodo que hace el cambio de una moneda a otra
     * @param tasas obtengo la tasas de cambio para la moneda original
     * @param tasa obtengo la tasa de cambio para la moneda de destino
     * @return devuelve la cantidad convertida */
    suspend fun convertirMoneda(cantidad: Double, monedaOrigen: String, monedaDestino: String): Double {
        return withContext(Dispatchers.IO){
            try {
                val url = URL("$baseURL/laters?amount=$cantidad&from=$monedaOrigen&to=$monedaDestino")
                val respuesta = url.readText()
                val jsonObject = JSONObject(respuesta)
                jsonObject.getJSONObject("rates").getDouble(monedaDestino)
            }catch (e: Exception){
                Log.e("CurrencyConverter", "Error en la conversion: ${e.message}")
                0.0
            }
        }
    }
}
