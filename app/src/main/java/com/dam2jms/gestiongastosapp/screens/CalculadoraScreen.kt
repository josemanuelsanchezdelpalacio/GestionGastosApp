package com.dam2jms.gestiongastosapp.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.models.AuxViewModel
import com.dam2jms.gestiongastosapp.models.CalculadoraViewModel
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.states.CalculadoraUiState
import com.dam2jms.gestiongastosapp.ui.theme.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculadoraScreen(
    navController: NavController,
    calculadoraViewModel: CalculadoraViewModel,
    auxViewModel: AuxViewModel
) {
    val uiState by calculadoraViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var showMetaDialog by remember { mutableStateOf(false) }
    var nuevaMetaText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CALCULADORA FINANCIERA", color = blanco, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(AppScreen.HomeScreen.route) }) {
                        Icon(Icons.Default.ArrowBack, "Atrás", tint = blanco)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = naranjaOscuro)
            )
        },
        bottomBar = {
            auxViewModel.bottomAppBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = naranjaOscuro
            ) {
                listOf(
                    "Resumen" to Icons.Default.Dashboard,
                    "Préstamos" to Icons.Default.AccountBalance,
                    "Dividir Gasto" to Icons.Default.Group,
                    "Jubilación" to Icons.Default.Person
                ).forEachIndexed { index, (title, icon) ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                        icon = { Icon(icon, contentDescription = title) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    when (selectedTab) {
                        0 -> ResumenFinancieroCard(uiState = uiState, onSetMetaClick = { showMetaDialog = true })
                        1 -> PrestamoCard(calculadoraViewModel = calculadoraViewModel)
                        2 -> CalcularGastosGrupales(calculadoraViewModel = calculadoraViewModel)
                        3 -> JubilacionCard(calculadoraViewModel = calculadoraViewModel)
                    }
                }
            }
        }

        if (showMetaDialog) {
            AlertDialog(
                onDismissRequest = { showMetaDialog = false },
                title = { Text("Establecer Meta Financiera") },
                text = {
                    OutlinedTextField(
                        value = nuevaMetaText,
                        onValueChange = { nuevaMetaText = it },
                        label = { Text("Meta") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        nuevaMetaText.toDoubleOrNull()?.let { meta ->
                            calculadoraViewModel.actualizarMetaFinanciera(meta)
                        }
                        showMetaDialog = false
                    }) {
                        Text("Establecer")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showMetaDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun ResumenFinancieroCard(
    uiState: CalculadoraUiState,
    onSetMetaClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Meta Financiera",
                    style = MaterialTheme.typography.titleMedium,
                    color = naranjaOscuro
                )
                IconButton(onClick = onSetMetaClick) {
                    Icon(Icons.Default.Edit, "Editar meta", tint = naranjaOscuro)
                }
            }

            Text(
                text = "Meta: ${uiState.metaFinanciera}${uiState.monedaActual}",
                style = MaterialTheme.typography.bodyLarge
            )

            LinearProgressIndicator(
                progress = (uiState.progresoMeta / 100.0).toFloat().coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth(),
                color = naranjaOscuro
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Ingresos Totales", color = verde)
                    Text("${uiState.ingresosTotales}${uiState.monedaActual}")
                }
                Column {
                    Text("Gastos Totales", color = rojo)
                    Text("${uiState.gastosTotales}${uiState.monedaActual}")
                }
            }

            Text(
                text = "Balance: ${uiState.balanceTotal}${uiState.monedaActual}",
                style = MaterialTheme.typography.titleMedium,
                color = if (uiState.balanceTotal >= 0) verde else rojo
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalcularGastosGrupales(calculadoraViewModel: CalculadoraViewModel) {
    var totalGasto by remember { mutableStateOf("") }
    var numPersonas by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf(0.0) }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Dividir Gasto Grupal",
                style = MaterialTheme.typography.titleMedium,
                color = naranjaOscuro,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = totalGasto,
                onValueChange = { totalGasto = it },
                label = { Text("Total del Gasto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = numPersonas,
                onValueChange = { numPersonas = it },
                label = { Text("Número de Personas") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            Button(
                onClick = {
                    val total = totalGasto.toDoubleOrNull() ?: 0.0
                    val personas = numPersonas.toIntOrNull() ?: 1
                    resultado = calculadoraViewModel.calcularDivisionGasto(total, personas)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Calcular")
            }

            if (resultado > 0.0) {
                Text(
                    text = "Cada persona debe pagar: $resultado",
                    style = MaterialTheme.typography.titleMedium,
                    color = naranjaOscuro,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PrestamoCard(calculadoraViewModel: CalculadoraViewModel) {
    var montoPrestamo by remember { mutableStateOf("") }
    var tasaInteres by remember { mutableStateOf("") }
    var años by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Cálculo de Préstamos",
                style = MaterialTheme.typography.titleMedium,
                color = naranjaOscuro
            )

            OutlinedTextField(
                value = montoPrestamo,
                onValueChange = { montoPrestamo = it },
                label = { Text("Monto del préstamo") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tasaInteres,
                onValueChange = { tasaInteres = it },
                label = { Text("Tasa de Interés (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = años,
                onValueChange = { años = it },
                label = { Text("Años del préstamo") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    calculadoraViewModel.calcularPrestamo(
                        montoPrestamo.toDoubleOrNull() ?: 0.0,
                        tasaInteres.toDoubleOrNull() ?: 0.0,
                        años.toIntOrNull() ?: 0
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Calcular Préstamo")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun JubilacionCard(calculadoraViewModel: CalculadoraViewModel) {
    var edadActual by remember { mutableStateOf("") }
    var edadJubilacion by remember { mutableStateOf("") }
    var gastosMensuales by remember { mutableStateOf("") }
    var ahorroActual by remember { mutableStateOf("") }
    var rendimientoAnual by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Planificación de Jubilación",
                style = MaterialTheme.typography.titleMedium,
                color = naranjaOscuro
            )

            OutlinedTextField(
                value = edadActual,
                onValueChange = { edadActual = it },
                label = { Text("Edad Actual") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = edadJubilacion,
                onValueChange = { edadJubilacion = it },
                label = { Text("Edad de Jubilación") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = gastosMensuales,
                onValueChange = { gastosMensuales = it },
                label = { Text("Gastos Mensuales Deseados") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ahorroActual,
                onValueChange = { ahorroActual = it },
                label = { Text("Ahorro Actual") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = rendimientoAnual,
                onValueChange = { rendimientoAnual = it },
                label = { Text("Rendimiento Anual Esperado (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    calculadoraViewModel.calcularPlanJubilacion(
                        edadActual.toIntOrNull() ?: 0,
                        edadJubilacion.toIntOrNull() ?: 0,
                        gastosMensuales.toDoubleOrNull() ?: 0.0,
                        ahorroActual.toDoubleOrNull() ?: 0.0,
                        rendimientoAnual.toDoubleOrNull() ?: 0.0
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Calcular Plan de Jubilación")
            }

            calculadoraViewModel.uiState.collectAsState().value.ahorroMensualNecesario?.let { ahorro ->
                Text(
                    "Ahorro Mensual Necesario: $ahorro${calculadoraViewModel.uiState.value.monedaActual}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            calculadoraViewModel.uiState.collectAsState().value.montoFinalJubilacion?.let { montoFinal ->
                Text(
                    "Monto Final Estimado: $montoFinal${calculadoraViewModel.uiState.value.monedaActual}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}