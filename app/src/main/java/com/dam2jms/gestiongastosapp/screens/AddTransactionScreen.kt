package com.dam2jms.gestiongastosapp.screens

import ItemComponents.SelectorCategoria
import android.os.Build
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
import com.dam2jms.gestiongastosapp.states.UiState
import com.dam2jms.gestiongastosapp.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavController, auxViewModel: AuxViewModel, mvvm: AddTransactionViewModel) {

    val uiState by mvvm.uiState.collectAsState()

    //selecciono la fecha teniendo la actual por defecto
    var seleccionarFecha by remember { mutableStateOf(LocalDate.now()) }

    //obtengo las categorias de la clase Categoria
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }

    //cargo las categorias cuando se cambia el tipo de transaccion
    LaunchedEffect(uiState.tipo) {
        categorias = CategoriaAPI.obtenerCategorias(uiState.tipo)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AÑADIR TRANSACCION",
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
                        Icon(Icons.Default.ArrowBackIos, "atras", tint = blanco)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorFondo)
            )
        },
        bottomBar = {
            //barra inferior reutilizable con secciones
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
            navController = navController,
            seleccionarFecha = seleccionarFecha,
            fechaSeleccionada = { newDate -> seleccionarFecha = newDate },
            categorias = categorias
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
    navController: NavController,
    seleccionarFecha: LocalDate,
    fechaSeleccionada: (LocalDate) -> Unit,
    categorias: List<Categoria>
) {

    val context = LocalContext.current

    //llamo al metodo para seleccionar una categoria
    val categoriaSeleccionada: (String) -> Unit = { categoria ->
        mvvm.actualizarDatosTransaccion(uiState.cantidad.toString(), categoria, uiState.tipo)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(colorFondo)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .shadow(6.dp, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = colorFondo),
            border = BorderStroke(2.dp, naranjaClaro)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    //botones para seleccionar el tipo de transaccion
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
                        unfocusedBorderColor = grisClaro
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Categoria", color = grisClaro, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))

                //para la lista de categorias
                SelectorCategoria(
                    categorias = categorias,
                    categoriaSeleccionada = uiState.categoria,
                    onCategorySelected = categoriaSeleccionada,
                    tipo = uiState.tipo
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Fecha", color = grisClaro, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showDatePicker(context, seleccionarFecha, fechaSeleccionada)
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
                        seleccionarFecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        color = if (uiState.tipo == "ingreso") verde else rojo
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        //boton para añadir la transaccion
        Button(
            onClick = {
                mvvm.añadirTransaccion(context, seleccionarFecha) { route ->
                    navController.navigate(AppScreen.TransactionScreen.createRoute(route))
                }
            },
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
                text = "Añadir Transaccion",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}



