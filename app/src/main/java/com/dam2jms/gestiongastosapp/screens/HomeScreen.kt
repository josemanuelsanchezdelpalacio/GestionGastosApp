package com.dam2jms.gestiongastosapp.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.components.CurrencySelectionDialog
import com.dam2jms.gestiongastosapp.models.AuxViewModel
import com.dam2jms.gestiongastosapp.models.CurrencyViewModel
import com.dam2jms.gestiongastosapp.models.HomeViewModel
import com.dam2jms.gestiongastosapp.states.TransactionState
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
                BalanceCard(
                    balance = uiState.balanceTotal,
                    moneda = uiState.monedaActual,
                    tasaAhorro = uiState.tasaAhorro
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Ahorro Diario",
                        value = uiState.ahorrosDiarios,
                        moneda = uiState.monedaActual,
                        icon = Icons.Default.Savings,
                        color = verde
                    )
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Gasto Promedio",
                        value = uiState.promedioGastoDiario,
                        moneda = uiState.monedaActual,
                        icon = Icons.Default.TrendingDown,
                        color = rojo
                    )
                }
            }

            item {
                RecentTransactionsCard(
                    transacciones = uiState.transaccionesRecientes,
                    moneda = uiState.monedaActual
                )
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
fun BalanceCard(balance: Double, moneda: String, tasaAhorro: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = naranjaOscuro)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Balance Total",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "$moneda ${String.format("%.2f", balance)}",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            val copyProgress: MutableState<Float> = remember { mutableStateOf(0.0f) }

            LinearProgressIndicator(
                progress = copyProgress.value,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                color = verde,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Text(
                "Tasa de ahorro: ${String.format("%.1f", tasaAhorro)}%",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun QuickStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: Double,
    moneda: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                "$moneda ${String.format("%.2f", value)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun RecentTransactionsCard(transacciones: List<TransactionState>, moneda: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Transacciones Recientes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            transacciones.forEach { transaccion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            transaccion.categoria,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            transaccion.fecha,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Text(
                        "$moneda ${String.format("%.2f", transaccion.cantidad)}",
                        color = if (transaccion.tipo == "ingreso") verde else rojo,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (transaccion != transacciones.last()) {
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}
