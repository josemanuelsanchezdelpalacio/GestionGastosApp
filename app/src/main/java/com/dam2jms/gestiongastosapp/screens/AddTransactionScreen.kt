package com.dam2jms.gestiongastosapp.screens

import ItemComponents.SelectorCategoria
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.components.BottomAppBarReutilizable
import com.dam2jms.gestiongastosapp.components.DatePickerComponents.showDatePicker
import com.dam2jms.gestiongastosapp.data.Categoria
import com.dam2jms.gestiongastosapp.data.CategoriaAPI
import com.dam2jms.gestiongastosapp.models.AddTransactionViewModel
import com.dam2jms.gestiongastosapp.models.AuxViewModel
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.states.TransactionUiState
import com.dam2jms.gestiongastosapp.states.UiState
import com.dam2jms.gestiongastosapp.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    auxViewModel: AuxViewModel,
    mvvm: AddTransactionViewModel
) {
    val uiState by mvvm.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }

    // Cargar las categorías cuando se cambia el tipo de transacción
    LaunchedEffect(uiState.tipo) {
        categorias = CategoriaAPI.obtenerCategorias(uiState.tipo)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AÑADIR TRANSACCIÓN",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = blanco
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIos, "Atrás", tint = blanco)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorFondo)
            )
        },
        bottomBar = {
            BottomAppBarReutilizable(
                navController = navController,
                screenActual = AppScreen.AddTransactionScreen,
                cambiarSeccion = { pantalla ->
                    navController.navigate(pantalla.route)
                }
            )
        },
        containerColor = colorFondo
    ) { paddingValues ->
        AddTransactionBody(
            paddingValues = paddingValues,
            uiState = uiState,
            mvvm = mvvm,
            selectedDate = selectedDate,
            onDateSelected = { newDate -> selectedDate = newDate },
            categorias = categorias,
            onCategorySelected = { categoria ->
                mvvm.actualizarDatosTransaccion(uiState.cantidad.toString(), categoria, uiState.tipo)
            },
            onAddTransaction = {
                if (uiState.cantidad > 0 && uiState.categoria.isNotEmpty() && uiState.tipo.isNotEmpty()) {
                    val transaccion = TransactionUiState(
                        cantidad = uiState.cantidad,
                        categoria = uiState.categoria,
                        tipo = uiState.tipo,
                        fecha = selectedDate.format(DateTimeFormatter.ISO_DATE)
                    )
                    scope.launch {
                        mvvm.crearTransaccion(transaccion, context)
                        navController.navigate(AppScreen.TransactionScreen.createRoute(selectedDate.toString()))
                    }
                } else {
                    Toast.makeText(context, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBody(
    paddingValues: PaddingValues,
    uiState: UiState,
    mvvm: AddTransactionViewModel,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    categorias: List<Categoria>,
    onCategorySelected: (String) -> Unit,
    onAddTransaction: () -> Unit
) {
    val context = LocalContext.current // Mover el contexto aquí para que sea accesible

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(colorFondo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Card para la transacción
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .shadow(6.dp, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = colorFondo),
            border = BorderStroke(2.dp, naranjaClaro)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Fila para seleccionar tipo de transacción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Botón de Ingreso
                    Button(
                        onClick = { mvvm.actualizarDatosTransaccion(uiState.cantidad.toString(), uiState.categoria, "ingreso") },
                        colors = ButtonDefaults.buttonColors(containerColor = verde),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.TrendingUp, "Ingreso")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ingreso", color = blanco)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Botón de Gasto
                    Button(
                        onClick = { mvvm.actualizarDatosTransaccion(uiState.cantidad.toString(), uiState.categoria, "gasto") },
                        colors = ButtonDefaults.buttonColors(containerColor = rojo),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.TrendingDown, "Gasto")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gasto", color = blanco)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para la cantidad
                Text("Cantidad", color = grisClaro, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.cantidad.toString(),
                    onValueChange = { nuevaCantidad ->
                        mvvm.actualizarDatosTransaccion(nuevaCantidad, uiState.categoria, uiState.tipo)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Icon(
                            Icons.Filled.AttachMoney,
                            "Cantidad",
                            tint = if (uiState.tipo == "ingreso") verde else rojo
                        )
                    },
                    textStyle = TextStyle(blanco),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = if (uiState.tipo == "ingreso") verde else rojo,
                        unfocusedBorderColor = grisClaro,
                        focusedLabelColor = if (uiState.tipo == "ingreso") verde else rojo,
                        unfocusedLabelColor = grisClaro
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para la categoría
                Text("Categoría", color = grisClaro, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                SelectorCategoria(
                    categorias = categorias,
                    selectedCategory = uiState.categoria,
                    onCategorySelected = onCategorySelected
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para la fecha
                Text("Fecha", color = grisClaro, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        showDatePicker(context, selectedDate) { nuevaFecha -> onDateSelected(nuevaFecha) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (uiState.tipo == "ingreso") verde else rojo
                    ),
                    border = BorderStroke(1.dp, if (uiState.tipo == "ingreso") verde else rojo)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        color = if (uiState.tipo == "ingreso") verde else rojo
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de añadir transacción
        Button(
            onClick = onAddTransaction,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (uiState.tipo == "ingreso") verde else rojo
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, "Añadir")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Añadir Transacción",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}


