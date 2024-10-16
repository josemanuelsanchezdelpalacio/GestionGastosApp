package com.dam2jms.gestiongastosapp.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.components.CurrencySelectionDialog
import com.dam2jms.gestiongastosapp.models.AuxViewModel
import com.dam2jms.gestiongastosapp.models.CurrencyViewModel
import com.dam2jms.gestiongastosapp.models.HomeViewModel
import com.dam2jms.gestiongastosapp.ui.theme.amarillo
import com.dam2jms.gestiongastosapp.ui.theme.blanco
import com.dam2jms.gestiongastosapp.ui.theme.colorFondo
import com.dam2jms.gestiongastosapp.ui.theme.naranjaOscuro
import com.dam2jms.gestiongastosapp.ui.theme.rojo
import com.dam2jms.gestiongastosapp.ui.theme.verde

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel, auxViewModel: AuxViewModel, currencyViewModel: CurrencyViewModel) {

    val uiState by homeViewModel.uiState.collectAsState()

    var showCurrencyDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RESUMEN FINANCIERO", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = naranjaOscuro),
                actions = {
                    IconButton(onClick = { showCurrencyDialog = true }) {
                        Icon(
                            Icons.Filled.MonetizationOn,
                            contentDescription = "Cambiar Moneda",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            auxViewModel.bottomAppBar(navController = navController)
        },
        containerColor = colorFondo
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ResumenFinanciero(
                    total = uiState.balanceTotal,
                    ingresosDiarios = uiState.ingresosDiarios,
                    gastosDiarios = uiState.gastosDiarios,
                    ahorrosDiarios = uiState.ahorrosDiarios,
                    ingresosMensuales = uiState.ingresosMensuales,
                    gastosMensuales = uiState.gastosMensuales,
                    ahorrosMensuales = uiState.ahorrosMensuales,
                    moneda = uiState.monedaActual
                )
            }
            item {
                GraficoGastosPorCategoria(gastosPorCategoria = uiState.gastosPorCategoria)
            }
        }
    }

    if (showCurrencyDialog) {
        CurrencySelectionDialog(
            currencyViewModel = currencyViewModel,
            onDismiss = { showCurrencyDialog = false },
            onCurrencySelected = { selectedCurrency ->
                homeViewModel.actualizarMoneda(selectedCurrency)
                showCurrencyDialog = false
            }
        )
    }
}

@Composable
fun ResumenFinanciero(total: Double, ingresosDiarios: Double, gastosDiarios: Double, ahorrosDiarios: Double, ingresosMensuales: Double, gastosMensuales: Double, ahorrosMensuales: Double, moneda: String) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = blanco)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Resumen Financiero", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            ResumenItem("Balance Total", total, moneda, amarillo)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Diario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    ResumenItem("Ingresos", ingresosDiarios, moneda, verde)
                    ResumenItem("Gastos", gastosDiarios, moneda, rojo)
                    ResumenItem("Ahorros", ahorrosDiarios, moneda, amarillo)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Mensual", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    ResumenItem("Ingresos", ingresosMensuales, moneda, verde)
                    ResumenItem("Gastos", gastosMensuales, moneda, rojo)
                    ResumenItem("Ahorros", ahorrosMensuales, moneda, amarillo)
                }
            }
        }
    }
}

@Composable
fun ResumenItem(label: String, amount: Double, currency: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            "$currency ${String.format("%.2f", amount)}",
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GraficoGastosPorCategoria(gastosPorCategoria: Map<String, Double>) {
    val total = gastosPorCategoria.values.sum()
    val colors = listOf(rojo, Color.Blue, verde, amarillo, Color.Magenta)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Gastos por Categoría",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                if (total > 0) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        var startAngle = 0f
                        gastosPorCategoria.values.forEachIndexed { index, value ->
                            val sweepAngle = ((value / total) * 360f).toFloat()  // Conversión a Float
                            drawArc(
                                color = colors[index % colors.size],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                size = Size(size.width, size.height),
                                style = Stroke(width = 60f)
                            )
                            startAngle += sweepAngle
                        }
                    }
                } else {
                    Text("Sin datos disponibles para graficar")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            gastosPorCategoria.entries.forEachIndexed { index, (categoria, gasto) ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(colors[index % colors.size])
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(categoria, fontSize = 14.sp)
                    }
                    Text(
                        "${String.format("%.2f", (gasto / total * 100))}%",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
