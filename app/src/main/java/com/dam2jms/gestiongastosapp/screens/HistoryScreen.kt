package com.dam2jms.gestiongastosapp.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.components.DatePickerComponents.showDatePicker
import com.dam2jms.gestiongastosapp.components.ItemComponents.TransactionItem
import com.dam2jms.gestiongastosapp.data.Categoria
import com.dam2jms.gestiongastosapp.data.CategoriaAPI
import com.dam2jms.gestiongastosapp.models.AuxViewModel
import com.dam2jms.gestiongastosapp.models.HistoryViewModel
import com.dam2jms.gestiongastosapp.models.TransactionViewModel
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.states.UiState
import com.dam2jms.gestiongastosapp.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(navController: NavController, auxViewModel: AuxViewModel, mvvm: HistoryViewModel) {
    val uiState by mvvm.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historial de Transacciones", color = blanco, fontWeight = FontWeight.Bold) },
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
        HistoryScreenContent(
            paddingValues = paddingValues,
            uiState = uiState,
            mvvm = mvvm,
            navController = navController
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreenContent(
    paddingValues: PaddingValues,
    uiState: UiState,
    mvvm: HistoryViewModel,
    navController: NavController
) {
    var buscarTipo by remember { mutableStateOf("fecha") }
    var tipo by remember { mutableStateOf("todos") }
    var buscarFecha by remember { mutableStateOf(LocalDate.now()) }
    var buscarCategoria by remember { mutableStateOf("") }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(tipo) {
        categorias = CategoriaAPI.obtenerCategorias(if (tipo == "todos") "" else tipo)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colorFondo, colorFondo.copy(alpha = 0.8f)),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = naranjaClaro.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = buscarTipo == "fecha",
                    onClick = { buscarTipo = "fecha" },
                    label = { Text("Fecha") },
                    leadingIcon = {
                        if (buscarTipo == "fecha") {
                            Icon(Icons.Filled.Check, contentDescription = null)
                        }
                    }
                )
                FilterChip(
                    selected = buscarTipo == "categoria",
                    onClick = { buscarTipo = "categoria" },
                    label = { Text("Categoría") },
                    leadingIcon = {
                        if (buscarTipo == "categoria") {
                            Icon(Icons.Filled.Check, contentDescription = null)
                        }
                    }
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = naranjaClaro.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = tipo == "todos",
                    onClick = { tipo = "todos" },
                    label = { Text("Todos") },
                    leadingIcon = {
                        if (tipo == "todos") {
                            Icon(Icons.Filled.Check, contentDescription = null)
                        }
                    }
                )
                FilterChip(
                    selected = tipo == "ingreso",
                    onClick = { tipo = "ingreso" },
                    label = { Text("Ingresos") },
                    leadingIcon = {
                        if (tipo == "ingreso") {
                            Icon(Icons.Filled.Check, contentDescription = null)
                        }
                    }
                )
                FilterChip(
                    selected = tipo == "gasto",
                    onClick = { tipo = "gasto" },
                    label = { Text("Gastos") },
                    leadingIcon = {
                        if (tipo == "gasto") {
                            Icon(Icons.Filled.Check, contentDescription = null)
                        }
                    }
                )
            }
        }

        if (buscarTipo == "fecha") {
            OutlinedButton(
                onClick = {
                    showDatePicker(context, buscarFecha) { nuevaFecha ->
                        buscarFecha = nuevaFecha
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = naranjaOscuro)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                Spacer(modifier = Modifier.width(8.dp))
                Text(buscarFecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            }
        } else {
            CategorySelector(
                categorias = categorias,
                selectedCategory = buscarCategoria,
                onCategorySelected = { categoria ->
                    buscarCategoria = categoria
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                mvvm.buscarTransacciones(buscarTipo, tipo, buscarFecha, buscarCategoria)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = naranjaOscuro)
        ) {
            Icon(Icons.Default.Search, contentDescription = "Buscar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Buscar Transacciones")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.transaccionesFiltradas.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.transaccionesFiltradas) { transaccion ->
                    TransactionItem(
                        transaccion = transaccion,
                        navController = navController,
                        mvvm = TransactionViewModel(),
                        context = context
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay transacciones que coincidan con los criterios de búsqueda",
                    style = MaterialTheme.typography.bodyLarge,
                    color = naranjaClaro,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}