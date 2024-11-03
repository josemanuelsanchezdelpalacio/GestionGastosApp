import ItemComponents.SelectorMoneda
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.components.BottomAppBarReutilizable
import com.dam2jms.gestiongastosapp.components.DatePickerComponents.showDatePicker
import com.dam2jms.gestiongastosapp.models.AuxViewModel
import com.dam2jms.gestiongastosapp.models.MonedasViewModel
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.states.TransactionUiState
import com.dam2jms.gestiongastosapp.states.UiState
import com.dam2jms.gestiongastosapp.ui.theme.azul
import com.dam2jms.gestiongastosapp.ui.theme.blanco
import com.dam2jms.gestiongastosapp.ui.theme.colorFondo
import com.dam2jms.gestiongastosapp.ui.theme.grisClaro
import com.dam2jms.gestiongastosapp.ui.theme.naranjaClaro
import com.dam2jms.gestiongastosapp.ui.theme.rojo
import com.dam2jms.gestiongastosapp.ui.theme.verde
import java.time.LocalDate
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    auxViewModel: AuxViewModel,
    monedasViewModel: MonedasViewModel
) {
    val context = LocalContext.current
    val uiState by homeViewModel.uiState.collectAsState()
    var mostrarListaMonedas by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PANEL FINANCIERO",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = blanco
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorFondo),
                actions = {
                    IconButton(onClick = { mostrarListaMonedas = true }) {
                        Icon(
                            Icons.Filled.MonetizationOn,
                            contentDescription = "Cambiar Moneda",
                            tint = blanco
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBarReutilizable(
                navController = navController,
                screenActual = AppScreen.HomeScreen,
                cambiarSeccion = { pantalla ->
                    navController.navigate(pantalla.route)
                }
            )
        },
        containerColor = colorFondo
    ) { innerPadding ->
        homeScreenBody(
            innerPadding,
            uiState,
            context,
            monedasViewModel,
            homeViewModel,
            mostrarListaMonedas,
            onDismiss = { mostrarListaMonedas = false },
            onCurrencySelected = { monedaSeleccionada ->
                homeViewModel.actualizarMoneda(monedaSeleccionada)
                mostrarListaMonedas = false
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun homeScreenBody(
    innerPadding: PaddingValues,
    uiState: UiState,
    context: Context,
    monedasViewModel: MonedasViewModel,
    homeViewModel: HomeViewModel,
    mostrarListaMonedas: Boolean,
    onDismiss: () -> Unit,
    onCurrencySelected: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            balanceCard(uiState)
        }
        item {
            metasCard(homeViewModel, context)
        }
        item {
            transaccionesRecientesCard(uiState.transaccionesRecientes, uiState.monedaActual)
        }
    }

    if (mostrarListaMonedas) {
        SelectorMoneda(
            monedasViewModel = monedasViewModel,
            onDismiss = onDismiss,
            monedaSeleccionada = onCurrencySelected
        )
    }
}

@Composable
fun balanceCard(uiState: UiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        border = BorderStroke(1.dp, naranjaClaro)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(
                "BALANCE TOTAL",
                color = blanco,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Balance total
            Text(
                text = "${uiState.monedaActual} ${String.format("%,.2f", uiState.balanceTotal)}",
                color = blanco,
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Ingresos y gastos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Ingresos",
                        color = blanco,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Diarios: ${String.format("%,.2f", uiState.ingresosDiarios)}", color = verde, style = MaterialTheme.typography.bodyMedium)
                    Text("Mensuales: ${String.format("%,.2f", uiState.ingresosMensuales)}", color = verde, style = MaterialTheme.typography.bodyMedium)
                    Text("Anuales: ${String.format("%,.2f", uiState.ingresosAnuales)}", color = verde, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.width(16.dp)) // Espacio entre columnas
                Column {
                    Text(
                        "Gastos",
                        color = blanco,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Diarios: ${String.format("%,.2f", uiState.gastosDiarios)}", color = rojo, style = MaterialTheme.typography.bodyMedium)
                    Text("Mensuales: ${String.format("%,.2f", uiState.gastosMensuales)}", color = rojo, style = MaterialTheme.typography.bodyMedium)
                    Text("Anuales: ${String.format("%,.2f", uiState.gastosAnuales)}", color = rojo, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Ahorros
            Divider(color = blanco.copy(alpha = 0.5f), thickness = 1.dp) // Separador
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Ahorros totales: ${uiState.monedaActual} ${String.format("%,.2f", uiState.ahorrosTotales)}",
                color = azul,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun metasCard(homeViewModel: HomeViewModel, context: Context) {
    val uiState by homeViewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        border = BorderStroke(1.dp, naranjaClaro)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text(
                "Meta Financiera",
                color = blanco,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.objetivoFinanciero > 0 && uiState.fechaObjetivo != null) {
                // Mostrar meta establecida
                Text("Meta Total: ${uiState.monedaActual} ${String.format("%,.2f", uiState.objetivoFinanciero)}", color = verde, style = MaterialTheme.typography.bodyMedium)
                Text("Días Restantes: ${uiState.diasHastaMeta}", color = azul, style = MaterialTheme.typography.bodyMedium)
                Text("Ahorro diario necesario: ${String.format("%.2f", uiState.ahorroDiarioNecesario)} ${uiState.monedaActual}", color = grisClaro, style = MaterialTheme.typography.bodySmall)

                if (uiState.estadoMeta.isNotEmpty()) {
                    Text(uiState.estadoMeta, color = if (uiState.estadoMeta.startsWith("¡")) verde else naranjaClaro, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                }
            } else {
                // Mensaje cuando no hay meta establecida
                Text("No hay meta establecida", color = grisClaro, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { showDialog = true }, modifier = Modifier.weight(1f)) {
                    Text(if (uiState.objetivoFinanciero > 0) "Modificar Meta" else "Establecer Meta")
                }
                if (uiState.objetivoFinanciero > 0) {
                    Button(
                        onClick = {
                            homeViewModel.eliminarMeta(context)
                            selectedDate = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = rojo)
                    ) {
                        Text("Eliminar Meta")
                    }
                }
            }
        }
    }

    if (showDialog) {
        var goalAmount by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDialog = false; errorMessage = ""; goalAmount = "" },
            title = { Text("Establecer Meta Financiera") },
            text = {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    TextField(
                        value = goalAmount,
                        onValueChange = { goalAmount = it; errorMessage = "" },
                        label = { Text("Cantidad objetivo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            showDatePicker(context, LocalDate.now()) { fecha -> selectedDate = fecha }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedDate?.toString() ?: "Seleccionar fecha")
                    }
                    if (errorMessage.isNotEmpty()) {
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val amount = goalAmount.toDoubleOrNull()
                    when {
                        amount == null || amount <= 0 -> { errorMessage = "Por favor, introduce una cantidad válida" }
                        selectedDate == null -> { errorMessage = "Por favor, selecciona una fecha" }
                        selectedDate!!.isBefore(LocalDate.now()) -> { errorMessage = "La fecha debe ser posterior a hoy" }
                        else -> {
                            homeViewModel.establecerMetaFinanciera(amount, selectedDate!!)
                            showDialog = false
                            goalAmount = ""
                            errorMessage = ""
                        }
                    }
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false; errorMessage = ""; goalAmount = "" }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


@Composable
fun transaccionesRecientesCard(transacciones: List<TransactionUiState>, monedaActual: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        border = BorderStroke(1.dp, naranjaClaro)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text(
                "Transacciones Recientes",
                color = blanco,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Listar transacciones recientes
            if (transacciones.isNotEmpty()) {
                for (transaccion in transacciones) {
                    TransaccionItem(transaccion, monedaActual)
                }
            } else {
                Text(
                    "No hay transacciones recientes.",
                    color = grisClaro,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun TransaccionItem(transaccion: TransactionUiState, monedaActual: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                transaccion.descripcion,
                color = blanco,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                transaccion.fecha.toString(),
                color = grisClaro,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(
            "${monedaActual} ${String.format("%,.2f", transaccion.cantidad)}",
            color = if (transaccion.cantidad < 0) rojo else verde,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

