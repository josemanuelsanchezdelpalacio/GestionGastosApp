package com.dam2jms.gestiongastosapp.models

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.dam2jms.gestiongastosapp.states.CalculadoraUiState
import com.dam2jms.gestiongastosapp.states.FilaPrestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
class CalculadoraViewModel : ViewModel() {

    //para controlar el estado de la UI
    private val _uiState = MutableStateFlow(CalculadoraUiState())
    val uiState: StateFlow<CalculadoraUiState> = _uiState.asStateFlow()

    /** metodo para calcular un prestamo basado en la cantidad, tasa de interes anual y plazo en meses*/
    fun calcularPrestamo(cantidad: Double, tasaAnual: Double, plazoMeses: Int, context: Context) {

        //compruebo que los datos que escribe el usuario sean validos
        if (cantidad <= 0 || tasaAnual < 0 || plazoMeses <= 0) {
            Toast.makeText(context, "Datos de entrada no validos", Toast.LENGTH_SHORT).show()
            return
        }

        //calculo la tasa de interes mensual
        val tasaMensual = tasaAnual / 12 / 100

        //formula para calcular la cuota mensual del prestamo
        val cuotaMensual = cantidad * tasaMensual * (1 + tasaMensual).pow(plazoMeses) / ((1 + tasaMensual).pow(plazoMeses) - 1)

        //listo para almacenar la tabla de amortizacion (detalles de las fechas y cantidades a pagar) del prestamo
        val tablaPrestamo = mutableListOf<FilaPrestamo>()

        //saldo pendiente del prestamo
        var saldoPendiente = cantidad

        //para calcular cada fila de la tabla de amortizacion por cada mes
        for (mes in 1..plazoMeses) {

            //calculo el interes del mes actual
            val interesMes = saldoPendiente * tasaMensual

            //calculo la cantidad pagada en el mes actual
            val capitalMes = cuotaMensual - interesMes

            //actualizo el salddo pendiente
            saldoPendiente -= capitalMes

            //añado una fila a la tabla de amortizacion con los datos calculados
            tablaPrestamo.add(
                FilaPrestamo(
                    mes = mes,
                    cuota = cuotaMensual.roundToDecimal(2),
                    capital = capitalMes.roundToDecimal(2),
                    interes = interesMes.roundToDecimal(2),
                    saldoPendiente = saldoPendiente.roundToDecimal(2)
                )
            )
        }

        //actualizo la UI con la cuota mensual, total de intereses y tabla de amortizacion
        _uiState.update { it.copy(
            cuotaPrestamo = cuotaMensual.roundToDecimal(2),
            totalIntereses = (cuotaMensual * plazoMeses - cantidad).roundToDecimal(2),
            tablaPrestamo = tablaPrestamo
        )}
    }

    /** metodo para calcular la division de gastos entre varias personas*/
    fun calcularDivisionGastos(cantidadTotal: Double, numeroPersonas: Int, context: Context) {

        //compruebo que los datos sean validos
        if (cantidadTotal < 0 || numeroPersonas <= 0) {
            Toast.makeText(context, "Datos de entrada no validos", Toast.LENGTH_SHORT).show()
            return
        }

        //calculo la cantidad correspondiente a cada persona
        val cantidadPorPersona = (cantidadTotal / numeroPersonas).roundToDecimal(2)

        //actualizo la UI con los datos calculados
        _uiState.update { it.copy(
            cantidadPorPersona = cantidadPorPersona,
            totalPersonas = numeroPersonas
        )}
    }

    /** metodo para calcular el retorno de inversion (ROI)*/
    fun calcularROI(inversionInicial: Double, retornoFinal: Double, context: Context) {

        //compruebo que la inversion inicial sea válida
        if (inversionInicial <= 0) {
            Toast.makeText(context, "La inversion inicial debe ser mayor que cero", Toast.LENGTH_SHORT).show()
            return
        }

        //calculo el ROI como el porcentaje de ganancia sobre la inversion inicial
        val roi = ((retornoFinal - inversionInicial) / inversionInicial * 100).roundToDecimal(2)

        //actualizo la UI con el ROI y la ganancia total
        _uiState.update { it.copy(
            roi = roi,
            gananciaTotal = (retornoFinal - inversionInicial).roundToDecimal(2)
        )}
    }

    /**metodo para calcular el efecto de la inflacion en una cantidad a lo largo del tiempo*/
    fun calcularInflacion(montoOriginal: Double, tasaInflacion: Double, años: Int, context: Context) {

        //compruebo que los datos sean validos
        if (montoOriginal <= 0 || tasaInflacion < 0 || años < 0) {
            Toast.makeText(context, "Datos de entrada no validos", Toast.LENGTH_SHORT).show()
            return
        }

        //calculo la cantidad ajustada con la inflacion despues de los años indicados
        val cantidadFinal = montoOriginal * (1 + tasaInflacion / 100).pow(años)

        //calculo la perdida de poder adquisitivo en base a la cantidad original
        val perdidaPoder = montoOriginal - cantidadFinal

        //actualizoz el estado de la UI con los datos calculados
        _uiState.update { it.copy(
            montoAjustadoInflacion = cantidadFinal.roundToDecimal(2),
            perdidaPoderAdquisitivo = perdidaPoder.roundToDecimal(2)
        )}
    }

    /**metodo para redondear un numero*/
    private fun Double.roundToDecimal(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return (this * factor).roundToInt() / factor
    }
}


