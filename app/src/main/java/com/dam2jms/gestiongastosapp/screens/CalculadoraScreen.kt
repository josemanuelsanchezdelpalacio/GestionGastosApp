package com.dam2jms.gestiongastosapp.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.components.BottomAppBarReutilizable
import com.dam2jms.gestiongastosapp.models.AuxViewModel
import com.dam2jms.gestiongastosapp.models.CalculadoraViewModel
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.states.CalculadoraUiState
import com.dam2jms.gestiongastosapp.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculadoraScreen(
    navController: NavController,
    calculadoraViewModel: CalculadoraViewModel,
    auxViewModel: AuxViewModel
) {
    val uiState by calculadoraViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var selectedCalculator by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CALCULADORAS FINANCIERAS",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = blanco
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorFondo),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Atrás", tint = blanco)
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBarReutilizable(
                navController = navController,
                screenActual = AppScreen.CalculadoraScreen,
                cambiarSeccion = { pantalla ->
                    navController.navigate(pantalla.route)
                }
            )
        },
        containerColor = colorFondo
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Selector de calculadora
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedCalculator,
                    containerColor = colorFondo,
                    contentColor = naranjaClaro,
                    edgePadding = 8.dp
                ) {
                    listOf(
                        "Préstamos" to Icons.Default.AttachMoney,
                        "División Gastos" to Icons.Default.Groups,
                        "ROI" to Icons.Default.TrendingUp,
                        "Inflación" to Icons.Default.Timeline
                    ).forEachIndexed { index, (titulo, icono) ->
                        Tab(
                            selected = selectedCalculator == index,
                            onClick = { selectedCalculator = index },
                            text = { Text(text = titulo, color = if (selectedCalculator == index) naranjaClaro else colorFondo) },
                            icon = {
                                Icon(imageVector = icono, contentDescription = titulo, tint = if (selectedCalculator == index) naranjaClaro else blanco)
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Calculadoras
            item {
                when (selectedCalculator) {
                    0 -> PrestamoCalculator(uiState, calculadoraViewModel, context)
                    1 -> DivisionGastosCalculator(uiState, calculadoraViewModel, context)
                    2 -> ROICalculator(uiState, calculadoraViewModel, context)
                    3 -> InflacionCalculator(uiState, calculadoraViewModel, context)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PrestamoCalculator(
    uiState: CalculadoraUiState,
    viewModel: CalculadoraViewModel,
    context: Context
) {
    var monto by remember { mutableStateOf("") }
    var tasaAnual by remember { mutableStateOf("") }
    var plazoMeses by remember { mutableStateOf("") }
    val cambiarMoneda = NumberFormat.getCurrencyInstance(Locale("es", "ES"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        border = BorderStroke(2.dp, naranjaClaro)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Calculadora de Préstamos",
                style = MaterialTheme.typography.titleMedium,
                color = naranjaClaro
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it },
                label = { Text("Monto del préstamo", color = blanco) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = naranjaClaro,
                    unfocusedBorderColor = grisClaro
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tasaAnual,
                onValueChange = { tasaAnual = it },
                label = { Text("Tasa anual (%)", color = blanco) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = naranjaClaro,
                    unfocusedBorderColor = grisClaro
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = plazoMeses,
                onValueChange = { plazoMeses = it },
                label = { Text("Plazo en meses", color = blanco) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = naranjaClaro,
                    unfocusedBorderColor = grisClaro
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.calcularPrestamo(
                        monto.toDoubleOrNull() ?: 0.0,
                        tasaAnual.toDoubleOrNull() ?: 0.0,
                        plazoMeses.toIntOrNull() ?: 0,
                        context = context
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = naranjaClaro)
            ) {
                Text("Calcular")
            }

            if (uiState.cuotaPrestamo > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    Text(
                        "Cuota mensual: ${cambiarMoneda.format(uiState.cuotaPrestamo)}",
                        color = verde
                    )
                    Text(
                        "Total intereses: ${cambiarMoneda.format(uiState.totalIntereses)}",
                        color = rojo
                    )
                }

                // Tabla de amortización
                if (uiState.tablaPrestamo.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Tabla de amortización",
                        style = MaterialTheme.typography.titleSmall,
                        color = naranjaClaro
                    )
                    LazyColumn {
                        items(uiState.tablaPrestamo) { fila ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Mes ${fila.mes}", color = blanco)
                                Text(cambiarMoneda.format(fila.capital), color = verde)
                                Text(cambiarMoneda.format(fila.interes), color = rojo)
                                Text(cambiarMoneda.format(fila.saldoPendiente), color = blanco)
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DivisionGastosCalculator(
    uiState: CalculadoraUiState,
    viewModel: CalculadoraViewModel,
    context: Context
) {
    var montoTotal by remember { mutableStateOf("") }
    var numeroPersonas by remember { mutableStateOf("") }
    val cambiarMoneda = NumberFormat.getCurrencyInstance(Locale("es", "ES"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        border = BorderStroke(2.dp, naranjaClaro)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "División de Gastos",
                style = MaterialTheme.typography.titleMedium,
                color = naranjaClaro
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = montoTotal,
                onValueChange = { montoTotal = it },
                label = { Text("Monto total", color = blanco) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = naranjaClaro,
                    unfocusedBorderColor = grisClaro
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = numeroPersonas,
                onValueChange = { numeroPersonas = it },
                label = { Text("Número de personas", color = blanco) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = naranjaClaro,
                    unfocusedBorderColor = grisClaro
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.calcularDivisionGastos(
                        montoTotal.toDoubleOrNull() ?: 0.0,
                        numeroPersonas.toIntOrNull() ?: 0,
                        context = context
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = naranjaClaro)
            ) {
                Text("Calcular")
            }

            if (uiState.cantidadPorPersona > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Gasto por persona: ${cambiarMoneda.format(uiState.cantidadPorPersona)}",
                    color = verde
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ROICalculator(
    uiState: CalculadoraUiState,
    viewModel: CalculadoraViewModel,
    context: Context
) {
    var ganancia by remember { mutableStateOf("") }
    var inversion by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        border = BorderStroke(2.dp, naranjaClaro)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Calculadora de ROI",
                style = MaterialTheme.typography.titleMedium,
                color = naranjaClaro
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = ganancia,
                onValueChange = { ganancia = it },
                label = { Text("Ganancia obtenida", color = blanco) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = naranjaClaro,
                    unfocusedBorderColor = grisClaro
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = inversion,
                onValueChange = { inversion = it },
                label = { Text("Inversión inicial", color = blanco) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = naranjaClaro,
                    unfocusedBorderColor = grisClaro
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.calcularROI(
                        ganancia.toDoubleOrNull() ?: 0.0,
                        inversion.toDoubleOrNull() ?: 0.0,
                        context = context
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = naranjaClaro)
            ) {
                Text("Calcular")
            }

            if (uiState.roi > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "ROI: ${uiState.roi}%",
                    color = verde
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InflacionCalculator(
    uiState: CalculadoraUiState,
    viewModel: CalculadoraViewModel,
    context: Context
) {
    var montoActual by remember { mutableStateOf("") }
    var tasaInflacion by remember { mutableStateOf("") }
    var anos by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        border = BorderStroke(2.dp, naranjaClaro)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Calculadora de Inflación",
                style = MaterialTheme.typography.titleMedium,
                color = naranjaClaro
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = montoActual,
                onValueChange = { montoActual = it },
                label = { Text("Monto actual", color = blanco) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = naranjaClaro,
                    unfocusedBorderColor = grisClaro
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tasaInflacion,
                onValueChange = { tasaInflacion = it },
                label = { Text("Tasa de inflación (%)", color = blanco) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = naranjaClaro,
                    unfocusedBorderColor = grisClaro
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = anos,
                onValueChange = { anos = it },
                label = { Text("Años", color = blanco) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = naranjaClaro,
                    unfocusedBorderColor = grisClaro
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.calcularInflacion(
                        montoActual.toDoubleOrNull() ?: 0.0,
                        tasaInflacion.toDoubleOrNull() ?: 0.0,
                        anos.toIntOrNull() ?: 0,
                        context = context
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = naranjaClaro)
            ) {
                Text("Calcular")
            }

            if (uiState.montoFuturo > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Monto en el futuro: ${uiState.montoFuturo}",
                    color = verde
                )
            }
        }
    }
}
