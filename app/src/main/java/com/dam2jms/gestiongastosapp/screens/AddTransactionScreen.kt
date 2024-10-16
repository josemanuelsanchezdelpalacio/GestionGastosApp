package com.dam2jms.gestiongastosapp.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.components.DatePickerComponents.showDatePicker
import com.dam2jms.gestiongastosapp.components.ItemComponents.obtenerIconoCategoria
import com.dam2jms.gestiongastosapp.data.Categoria
import com.dam2jms.gestiongastosapp.data.CategoriaAPI
import com.dam2jms.gestiongastosapp.models.AddTransactionViewModel
import com.dam2jms.gestiongastosapp.models.AuxViewModel
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.states.TransactionState
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

    LaunchedEffect(uiState.tipo) {
        categorias = CategoriaAPI.obtenerCategorias(uiState.tipo)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Añadir Transacción", color = blanco, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(colorFondo, colorFondo.copy(alpha = 0.8f)),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(naranjaClaro.copy(alpha = 0.1f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RadioButton(
                    selected = uiState.tipo == "ingreso",
                    onClick = { mvvm.actualizarDatosTransaccion(uiState.cantidad.toString(), uiState.categoria, "ingreso") },
                    colors = RadioButtonDefaults.colors(selectedColor = verde)
                )
                Text("Ingreso", modifier = Modifier.align(Alignment.CenterVertically))

                RadioButton(
                    selected = uiState.tipo == "gasto",
                    onClick = { mvvm.actualizarDatosTransaccion(uiState.cantidad.toString(), uiState.categoria, "gasto") },
                    colors = RadioButtonDefaults.colors(selectedColor = rojo)
                )
                Text("Gasto", modifier = Modifier.align(Alignment.CenterVertically))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.cantidad.toString(),
                onValueChange = { nuevaCantidad ->
                    mvvm.actualizarDatosTransaccion(nuevaCantidad, uiState.categoria, uiState.tipo)
                },
                label = { Text("Cantidad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Filled.AttachMoney, "Cantidad") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = naranjaOscuro,
                    unfocusedBorderColor = naranjaClaro
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categoría
            CategorySelector(
                categorias = categorias,
                selectedCategory = uiState.categoria,
                onCategorySelected = { categoria ->
                    mvvm.actualizarDatosTransaccion(uiState.cantidad.toString(), categoria, uiState.tipo)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    showDatePicker(context, selectedDate) { nuevaFecha ->
                        selectedDate = nuevaFecha
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = naranjaOscuro)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                Spacer(modifier = Modifier.width(8.dp))
                Text(selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Añadir Transacción
            Button(
                onClick = {
                    if (uiState.cantidad > 0 && uiState.categoria.isNotEmpty() && uiState.tipo.isNotEmpty()) {
                        val transaccion = TransactionState(
                            cantidad = uiState.cantidad,
                            categoria = uiState.categoria,
                            tipo = uiState.tipo,
                            fecha = selectedDate.format(DateTimeFormatter.ISO_DATE)
                        )
                        scope.launch {
                            mvvm.agregarTransaccion(transaccion, context)
                            navController.navigate(AppScreen.TransactionScreen.createRoute(selectedDate.toString()))
                        }
                    } else {
                        Toast.makeText(context, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = naranjaOscuro)
            ) {
                Text("Añadir Transacción")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
    categorias: List<Categoria>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = naranjaOscuro,
                unfocusedBorderColor = naranjaClaro
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categorias.forEach { categoria ->
                DropdownMenuItem(
                    text = { Text(categoria.nombre) },
                    onClick = {
                        onCategorySelected(categoria.nombre)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = obtenerIconoCategoria(categoria.nombre),
                            contentDescription = categoria.nombre
                        )
                    }
                )
            }
        }
    }
}
