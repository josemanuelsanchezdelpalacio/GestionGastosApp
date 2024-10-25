package com.dam2jms.gestiongastosapp.components

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dam2jms.gestiongastosapp.ui.theme.azul
import com.dam2jms.gestiongastosapp.ui.theme.rojo
import com.dam2jms.gestiongastosapp.ui.theme.verde
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GraficoBarras(ingresos: List<Double>, gastos: List<Double>, fechas: List<LocalDate>, modifier: Modifier = Modifier){

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setPinchZoom(false)
                setDrawBarShadow(false)
                setDrawValueAboveBar(false)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    textSize = 12f
                    textColor = Color.BLACK
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    textSize = 12f
                    textColor = Color.BLACK
                    setDrawAxisLine(true)
                    valueFormatter = object : ValueFormatter(){
                        override fun getFormattedValue(value: Float): String {
                            return String.format("%.2f€", value)
                        }
                    }
                }

                axisRight.isEnabled = false

                legend.apply {
                    verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    setDrawInside(false)
                    textSize = 12f
                    formSize = 12f
                    xEntrySpace = 12f
                }

                animateY(1000)
            }
        },
        update = { grafico ->
            val entradasIngresos = ingresos.mapIndexed { index, valor ->
                BarEntry(index.toFloat(), valor.toFloat())
            }
            val entradasGastos = gastos.mapIndexed { index, valor ->
                BarEntry(index.toFloat(), valor.toFloat())
            }

            val ingresosDataSet = BarDataSet(entradasIngresos, "Ingresos").apply {
                verde
                valueTextSize = 12f
                valueTextColor = Color.BLACK
                valueFormatter = object : ValueFormatter(){
                    override fun getFormattedValue(value: Float): String {
                        return String.format("%.2f€", value)
                    }
                }
            }
            val gastosDataSet = BarDataSet(entradasGastos,"Gastos").apply {
                rojo
                valueTextSize = 12f
                valueTextColor = Color.BLACK
                valueFormatter = object : ValueFormatter(){
                    override fun getFormattedValue(value: Float): String {
                        return String.format("%.2f€", value)
                    }
                }
            }

            val etiquetasFechas = fechas.map { fecha ->
                fecha.format(DateTimeFormatter.ofPattern("dd/MM"))
            }

            grafico.xAxis.valueFormatter = IndexAxisValueFormatter(etiquetasFechas)
            val barData = BarData(ingresosDataSet, gastosDataSet).apply {
                barWidth = 0.35f
            }

            grafico.data = barData
            grafico.groupBars(-0.5f, 0.06f, 0.02f)
            grafico.setFitBars(true)
            grafico.invalidate()
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GraficoLineas(datos: List<Double>, fechas: List<LocalDate>, modifier: Modifier = Modifier){

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setDrawGridBackground(false)
                setPinchZoom(false)

                axisLeft.apply {
                    setDrawGridLines(false)
                    textSize = 12f
                    textColor = Color.BLACK
                    valueFormatter = object : ValueFormatter(){
                        override fun getFormattedValue(value: Float): String {
                            return String.format("%.2f€", value)
                        }
                    }
                }

                xAxis.apply {
                    setDrawGridLines(true)
                    textSize = 12f
                    textColor = Color.BLACK
                    setDrawAxisLine(true)
                }

                axisRight.isEnabled = false

                legend.apply {
                    verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    setDrawInside(false)
                    textSize = 12f
                    formSize = 12f
                }

                animateX(1000)
            }
        },
        update = { grafico ->
            val entradas = datos.mapIndexed { index, valor ->
                Entry(index.toFloat(), valor.toFloat())
            }

            val dataSet = LineDataSet(entradas, "Evolucion balance").apply {
                azul
                setCircleColors(Color.BLUE)
                lineWidth = 2f
                circleRadius = 4f
                setDrawFilled(true)
                fillColor = Color.BLUE
                fillAlpha = 50
                mode = LineDataSet.Mode.CUBIC_BEZIER
                valueTextSize = 10f
                valueTextColor = Color.BLACK
                valueFormatter = object : ValueFormatter(){
                    override fun getFormattedValue(value: Float): String {
                        return String.format("%.2f€", value)
                    }
                }
            }

            val etiquetasFechas = fechas.map { fecha ->
                fecha.format(DateTimeFormatter.ofPattern("dd/MM"))
            }

            grafico.xAxis.valueFormatter = IndexAxisValueFormatter(etiquetasFechas)

            grafico.data = LineData(dataSet)
            grafico.invalidate()
        }
    )
}


@Composable
fun GraficoCircular(datos: Map<String, Double>, modifier: Modifier = Modifier){

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                setUsePercentValues(true)
                setExtraOffsets(5f, 10f, 5f, 5f)

                dragDecelerationFrictionCoef = 0.95f
                isDrawHoleEnabled = true
                setHoleColor(Color.WHITE)
                setTransparentCircleColor(Color.WHITE)
                setTransparentCircleAlpha(110)
                holeRadius = 58f
                transparentCircleRadius = 61f

                setDrawCenterText(true)
                centerText = "Gastos por\nCategoria"
                setCenterTextSize(16f)

                legend.apply {
                    verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                    orientation = Legend.LegendOrientation.VERTICAL
                    setDrawInside(false)
                    textSize = 12f
                    xEntrySpace = 7f
                    yEntrySpace = 0f
                    yOffset = 0f
                }

                animateY(1000)
            }
        },
        update = { grafico ->
            val entradas = datos.map { (categoria, valor) ->
                PieEntry(valor.toFloat(), categoria)
            }

            val colores = listOf(
                Color.rgb(244, 67, 54),    // Rojo
                Color.rgb(156, 39, 176),   // Púrpura
                Color.rgb(33, 150, 243),   // Azul
                Color.rgb(76, 175, 80),    // Verde
                Color.rgb(255, 152, 0),    // Naranja
                Color.rgb(233, 30, 99),    // Rosa
                Color.rgb(0, 188, 212),    // Cyan
                Color.rgb(255, 235, 59)    // Amarillo
            )

            val dataSet = PieDataSet(entradas, "").apply {
                this.colors = colores
                valueLinePart1OffsetPercentage = 80f
                valueLinePart1Length = 0.2f
                valueLinePart2Length = 0.4f
                yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                valueTextSize = 12f
                valueTextColor = Color.BLACK
                selectionShift = 5f
            }

            val data = PieData(dataSet).apply {
                setValueFormatter(PercentFormatter(grafico))
                setValueTextSize(11f)
                setValueTextColor(Color.BLACK)
            }

            grafico.data = data
            grafico.invalidate()
        }
    )
}


