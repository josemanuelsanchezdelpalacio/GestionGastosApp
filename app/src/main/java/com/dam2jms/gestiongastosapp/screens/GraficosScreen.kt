package com.dam2jms.gestiongastosapp.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.components.GraficoBarras
import com.dam2jms.gestiongastosapp.components.GraficoCircular
import com.dam2jms.gestiongastosapp.components.GraficoLineas
import com.dam2jms.gestiongastosapp.models.AuxViewModel
import com.dam2jms.gestiongastosapp.models.GraficosViewModel
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.ui.theme.blanco
import com.dam2jms.gestiongastosapp.ui.theme.colorFondo
import com.dam2jms.gestiongastosapp.ui.theme.naranjaClaro
import com.dam2jms.gestiongastosapp.ui.theme.naranjaOscuro
import com.dam2jms.gestiongastosapp.ui.theme.rojo
import com.dam2jms.gestiongastosapp.ui.theme.verde
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GraficosScreen(navController: NavController, graficosViewModel: GraficosViewModel, auxViewModel: AuxViewModel){

    val uiState by graficosViewModel.uiState.collectAsState()
    var seleccionSeccion by remember { mutableStateOf(0) }
    val cambiarMoneda = NumberFormat.getCurrencyInstance(Locale("es", "ES"))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "GRAFICOS FINANCIEROS", color = blanco, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = naranjaOscuro),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Atras")
                    }
                }
            )
        },
        bottomBar = {
            auxViewModel.bottomAppBar(navController = navController)
        }
    ) { paddingValues ->
        // Usar LazyColumn en lugar de combinar Column con verticalScroll
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sección de encabezado antes de los gráficos
            item {
                ScrollableTabRow(
                    selectedTabIndex = seleccionSeccion,
                    containerColor = blanco,
                    contentColor = naranjaOscuro
                ) {
                    listOf(
                        "Ingresos vs Gastos" to Icons.Default.BarChart,
                        "Evolucion" to Icons.Default.ShowChart,
                        "Categorias" to Icons.Default.PieChart
                    ).forEachIndexed { index, (titulo, icono) ->
                        Tab(
                            selected = seleccionSeccion == index,
                            onClick = { seleccionSeccion = index },
                            text = { Text(text = titulo) },
                            icon = { Icon(imageVector = icono, contentDescription = titulo) }
                        )
                    }
                }
            }

            // Mostrar el gráfico correspondiente según la pestaña seleccionada
            item {
                when(seleccionSeccion){
                    0 -> GraficoBarras(ingresos = uiState.ingresos, gastos = uiState.gastos, fechas = uiState.fechas)
                    1 -> GraficoLineas(datos = uiState.evolucionBalance, fechas = uiState.fechas)
                    2 -> GraficoCircular(datos = uiState.gastosPorCategoria)
                }
            }
        }
    }
}

@Composable
fun LeyendaIngresosGastos(totalIngresos: Double, totalGastos: Double) {

    val cambiarMoneda = NumberFormat.getCurrencyInstance(Locale("es", "ES"))

    Column {
        Text(
            text = "Resumen del periodo",
            style = MaterialTheme.typography.titleMedium,
            color = naranjaOscuro
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Total ingresos: ", color = verde)
            Text(text = cambiarMoneda.format(totalIngresos), color = verde)
        }
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "Total gastos: ", color = rojo)
            Text(text = cambiarMoneda.format(totalGastos), color = rojo)
        }
    }
}

@Composable
fun LeyendaEvolucion(balanceInicial: Double, balanceFinal: Double) {

    val cambiarMoneda = NumberFormat.getCurrencyInstance(Locale("es", "ES"))
    val diferencia = balanceFinal - balanceInicial
    val colorDiferencia = if(diferencia >= 0) verde else rojo

    Column {
        Text(
            text = "Evolucion del balance",
            style = MaterialTheme.typography.titleMedium,
            color = naranjaOscuro
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Balance Inicial: ")
            Text(text = cambiarMoneda.format(balanceInicial))
        }
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "Balance final: ")
            Text(text = cambiarMoneda.format(balanceFinal))
        }
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "Diferencia: ")
            Text(text = cambiarMoneda.format(diferencia), color = colorDiferencia)
        }
    }

}

@Composable
fun LeyendaCategorias(categorias: Map<String, Double>) {

    val cambiarMoneda = NumberFormat.getCurrencyInstance(Locale("es", "ES"))

    Column {
        Text(
            text = "Gastos por categoria",
            style = MaterialTheme.typography.titleMedium,
            color = naranjaOscuro
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    categorias.forEach{ (categoria, cantidad) ->
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(categoria)
            Text(text = cambiarMoneda.format(cantidad))
        }
    }

}

