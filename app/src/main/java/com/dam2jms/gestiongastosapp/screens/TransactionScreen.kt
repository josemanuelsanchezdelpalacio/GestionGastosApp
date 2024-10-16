package com.dam2jms.gestiongastosapp.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.components.DatePickerComponents.showDatePicker
import com.dam2jms.gestiongastosapp.components.ItemComponents.TransactionItem
import com.dam2jms.gestiongastosapp.models.TransactionViewModel
import com.dam2jms.gestiongastosapp.models.AuxViewModel
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.states.UiState
import com.dam2jms.gestiongastosapp.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    navController: NavController,
    mvvm: TransactionViewModel,
    auxViewModel: AuxViewModel,
    seleccionarFecha: String
) {
    val uiState by mvvm.uiState.collectAsState()
    var displayType by remember { mutableStateOf("ingresos") }
    var fecha by remember { mutableStateOf(LocalDate.parse(seleccionarFecha)) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        mvvm.leerTransacciones()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("TRANSACCIONES", color = blanco, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(AppScreen.HomeScreen.route)
                    }) {
                        Icon(Icons.Default.ArrowBack, "Atrás", tint = blanco)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showDatePicker(context, fecha) { nuevaFecha ->
                            fecha = nuevaFecha
                            mvvm.leerTransacciones()
                        }
                    }) {
                        Icon(Icons.Default.CalendarToday, "Seleccionar fecha", tint = blanco)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = naranjaOscuro)
            )
        },
        bottomBar = {
            auxViewModel.bottomAppBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppScreen.AddTransactionScreen.route) },
                containerColor = naranjaClaro,
                contentColor = blanco
            ) {
                Icon(Icons.Filled.Add, "Añadir transacción")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        TransactionScreenContent(
            paddingValues = paddingValues,
            uiState = uiState,
            mvvm = mvvm,
            navController = navController,
            displayType = displayType,
            onDisplayTypeChange = { displayType = it },
            fecha = fecha
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreenContent(
    paddingValues: PaddingValues,
    uiState: UiState,
    mvvm: TransactionViewModel,
    navController: NavController,
    displayType: String,
    onDisplayTypeChange: (String) -> Unit,
    fecha: LocalDate
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colorFondo, colorFondo.copy(alpha = 0.8f)),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Transacciones del día",
            style = MaterialTheme.typography.titleMedium,
            color = naranjaOscuro,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            color = naranjaOscuro,
            modifier = Modifier.padding(bottom = 16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )

        mvvm.horizontalCalendar(
            fechaSeleccionada = fecha,
            onDateSelected = { nuevaFecha ->
                navController.navigate(AppScreen.TransactionScreen.createRoute(nuevaFecha.toString()))
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(naranjaClaro.copy(alpha = 0.1f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RadioButton(
                selected = displayType == "ingresos",
                onClick = { onDisplayTypeChange("ingresos") },
                colors = RadioButtonDefaults.colors(selectedColor = verde)
            )
            Text("Ingresos", modifier = Modifier.align(Alignment.CenterVertically))

            RadioButton(
                selected = displayType == "gastos",
                onClick = { onDisplayTypeChange("gastos") },
                colors = RadioButtonDefaults.colors(selectedColor = rojo)
            )
            Text("Gastos", modifier = Modifier.align(Alignment.CenterVertically))
        }

        Spacer(modifier = Modifier.height(16.dp))

        val filtroTransacciones = mvvm.filtrarTransacciones(fecha, displayType)

        if (filtroTransacciones.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filtroTransacciones) { transaccion ->
                    TransactionItem(
                        transaccion = transaccion,
                        navController = navController,
                        mvvm = mvvm,
                        context = LocalContext.current
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay transacciones para este día",
                    style = MaterialTheme.typography.bodyLarge,
                    color = naranjaClaro,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

