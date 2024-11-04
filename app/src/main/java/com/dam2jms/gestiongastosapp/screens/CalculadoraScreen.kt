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
import androidx.compose.ui.graphics.Color
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
fun CalculadoraScreen(navController: NavController, calculadoraViewModel: CalculadoraViewModel, auxViewModel: AuxViewModel) {

    val uiState by calculadoraViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var seleccionarCalculadora by remember { mutableStateOf(0) }

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
                        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "atras", tint = blanco)
                    }
                }
            )
        },
        bottomBar = {
            //barra inferior con las pantallas
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
            //barra superior con las distintas calculadoras
            item {
                calculadoraViewModel.calculadoraSelector(
                    seleccionarCalculadora = seleccionarCalculadora,
                    onSeleccionarCalculadora = { seleccionarCalculadora = it },
                    colorFondo = colorFondo,
                    colorSeleccionado = naranjaClaro,
                    colorTexto = blanco
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            //ventanas para cada calculadora
            item {
                when (seleccionarCalculadora) {
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
fun PrestamoCalculator(uiState: CalculadoraUiState, viewModel: CalculadoraViewModel, context: Context) {

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
                "Calculadora de prestamos",
                style = MaterialTheme.typography.titleMedium,
                color = naranjaClaro
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.cantidad,
                onValueChange = { viewModel.actualizarDatosPrestamo(it, uiState.tasaAnual, uiState.plazoMeses) },
                label = { Text("Monto del prestamo", color = blanco) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = naranjaClaro,
                    unfocusedBorderColor = grisClaro
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.tasaAnual,
                onValueChange = { viewModel.actualizarDatosPrestamo(uiState.cantidad, it, uiState.plazoMeses) },
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
                value = uiState.plazoMeses,
                onValueChange = { viewModel.actualizarDatosPrestamo(uiState.cantidad, uiState.tasaAnual, it) },
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
                        uiState.cantidad.toDoubleOrNull() ?: 0.0,
                        uiState.tasaAnual.toDoubleOrNull() ?: 0.0,
                        uiState.plazoMeses.toIntOrNull() ?: 0,
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

                if (uiState.tablaPrestamo.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Tabla de amortizacion",
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
                "Division de gastos",
                style = MaterialTheme.typography.titleMedium,
                color = naranjaClaro
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.cantidadTotal.toString(),
                onValueChange = {
                    val newCantidadTotal = it.toDoubleOrNull() ?: 0.0
                    viewModel.actualizarDatosDivisionGastos(newCantidadTotal, uiState.numeroPersonas)
                },
                label = { Text("Cantidad total", color = blanco) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = naranjaClaro,
                    unfocusedBorderColor = grisClaro
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.numeroPersonas.toString(),
                onValueChange = {
                    val newNumeroPersonas = it.toIntOrNull() ?: 0
                    viewModel.actualizarDatosDivisionGastos(uiState.cantidadTotal, newNumeroPersonas)
                },
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
                        uiState.cantidadTotal,
                        uiState.numeroPersonas,
                        context
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
fun ROICalculator(uiState: CalculadoraUiState, viewModel: CalculadoraViewModel, context: Context) {

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
                value = uiState.ganancia.toString(),
                onValueChange = {
                    val nuevaGanancia = it.toDoubleOrNull() ?: 0.0
                    viewModel.actualizarDatosROI(nuevaGanancia, uiState.inversion)
                },
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
                value = uiState.inversion.toString(),
                onValueChange = {
                    val nuevaInversion = it.toDoubleOrNull() ?: 0.0
                    viewModel.actualizarDatosROI(uiState.ganancia, nuevaInversion)
                },
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
                        uiState.ganancia,
                        uiState.inversion,
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
fun InflacionCalculator(uiState: CalculadoraUiState, viewModel: CalculadoraViewModel, context: Context) {

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
                "Calculadora de inflacion",
                style = MaterialTheme.typography.titleMedium,
                color = naranjaClaro
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.cantidadAjustadaInflacion.toString(),
                onValueChange = {
                    val nuevaCantidad = it.toDoubleOrNull() ?: 0.0
                    viewModel.actualizarDatosInflacion(nuevaCantidad, uiState.tasaInflacion, uiState.años)
                },
                label = { Text("Cantidad actual", color = blanco) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = naranjaClaro,
                    unfocusedBorderColor = grisClaro
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.tasaInflacion.toString(),
                onValueChange = {
                    val nuevaTasa = it.toDoubleOrNull() ?: 0.0
                    viewModel.actualizarDatosInflacion(uiState.cantidadAjustadaInflacion, nuevaTasa, uiState.años)
                },
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
                value = uiState.años.toString(),
                onValueChange = {
                    val nuevoAño = it.toIntOrNull() ?: 0
                    viewModel.actualizarDatosInflacion(uiState.cantidadAjustadaInflacion, uiState.tasaInflacion, nuevoAño)
                },
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
                        uiState.cantidadAjustadaInflacion,
                        uiState.tasaInflacion,
                        uiState.años,
                        context = context
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = naranjaClaro)
            ) {
                Text("Calcular")
            }

            if (uiState.cantidadAjustadaInflacion > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Cantidad en el futuro: ${uiState.cantidadAjustadaInflacion}",
                    color = verde
                )
            }
        }
    }
}




