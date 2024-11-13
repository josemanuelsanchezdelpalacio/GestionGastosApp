package com.dam2jms.gestiongastosapp.screens

import ItemComponents.SelectorMoneda
import ItemComponents.TransaccionItem
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dam2jms.gestiongastosapp.R
import com.dam2jms.gestiongastosapp.components.BottomAppBarReutilizable
import com.dam2jms.gestiongastosapp.components.DatePickerComponents.showDatePicker
import com.dam2jms.gestiongastosapp.models.AuxViewModel
import com.dam2jms.gestiongastosapp.models.HomeViewModel
import com.dam2jms.gestiongastosapp.models.MonedasViewModel
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.states.Article
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

    LaunchedEffect(Unit) {
        homeViewModel.initializeNews(context)
    }

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
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIos, "atras", tint = blanco)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorFondo),
                actions = {
                    IconButton(onClick = { mostrarListaMonedas = true }) {
                        Icon(
                            Icons.Filled.MonetizationOn,
                            contentDescription = "Cambiar moneda",
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
    ) { paddingValues ->
        homeScreenBody(
            paddingValues = paddingValues,
            uiState = uiState,
            context = context,
            monedasViewModel = monedasViewModel,
            homeViewModel = homeViewModel,
            mostrarListaMonedas = mostrarListaMonedas,
            onDismissMonedas = { mostrarListaMonedas = false },
            navController = navController,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun homeScreenBody(
    paddingValues: PaddingValues,
    uiState: UiState,
    context: Context,
    monedasViewModel: MonedasViewModel,
    homeViewModel: HomeViewModel,
    mostrarListaMonedas: Boolean,
    onDismissMonedas: () -> Unit,
    navController: NavController,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { balanceCard(uiState = uiState) }
        item { metasCard(homeViewModel = homeViewModel, context = context) }
        item {
            NewsCard(
                articles = uiState.newsArticles,
                isLoading = uiState.isLoadingNews,
                error = uiState.newsError,
                onRefresh = { homeViewModel.refreshNews(context) },
                context = context
            )
        }
    }

    if (mostrarListaMonedas) {
        SelectorMoneda(
            monedasViewModel = monedasViewModel,
            onDismiss = onDismissMonedas,
            monedaSeleccionada = { selectedMoneda ->
                homeViewModel.actualizarMoneda(selectedMoneda)
                onDismissMonedas()
            }
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

            //cantidad total de ingresos
            Text(
                text = "${uiState.monedaActual} ${String.format("%,.2f", uiState.balanceTotal)}",
                color = blanco,
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))

            //ingresos y gastos por separado
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

            //datos de ahorros
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
    var fechaSeleccionada by remember { mutableStateOf<LocalDate?>(null) }

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

                Text("Meta Total: ${uiState.monedaActual} ${String.format("%,.2f", uiState.objetivoFinanciero)}", color = verde, style = MaterialTheme.typography.bodyMedium)
                Text("Días restantes: ${uiState.diasHastaMeta}", color = azul, style = MaterialTheme.typography.bodyMedium)
                Text("Ahorro diario necesario: ${String.format("%.2f", uiState.ahorroDiarioNecesario)} ${uiState.monedaActual}", color = grisClaro, style = MaterialTheme.typography.bodySmall)

                //calculo el progreso actual
                val progresoActual = uiState.balanceTotal
                val objetivoFinanciero = uiState.objetivoFinanciero

                //calculo el porcentaje de progreso
                val porcentajeProgreso = if (objetivoFinanciero > 0) {
                    (progresoActual / objetivoFinanciero * 100).coerceIn(0.0, 100.0)
                } else { 0.0 }

                //barra de progreso
                LinearProgressIndicator(
                    progress = (porcentajeProgreso / 100).toFloat(),
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = verde,
                    trackColor = grisClaro
                )

                if (uiState.estadoMeta.isNotEmpty()) {
                    Text(uiState.estadoMeta, color = if (uiState.estadoMeta.startsWith("¡")) verde else naranjaClaro, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                }
            } else {
                Text("No hay meta establecida", color = grisClaro, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { showDialog = true }, modifier = Modifier.weight(1f)) {
                    Text(if (uiState.objetivoFinanciero > 0) "Modificar meta" else "Establecer Meta")
                }
                if (uiState.objetivoFinanciero > 0) {
                    Button(
                        onClick = {
                            homeViewModel.eliminarMeta(context)
                            fechaSeleccionada = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = rojo)
                    ) {
                        Text("Eliminar meta")
                    }
                }
            }
        }
    }

    if (showDialog) {
        var cantidadObjetivo by remember { mutableStateOf("") }
        var mensajeError by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDialog = false; mensajeError = ""; cantidadObjetivo = "" },
            title = { Text("Establecer meta financiera") },
            text = {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    TextField(
                        value = cantidadObjetivo,
                        onValueChange = { cantidadObjetivo = it; mensajeError = "" },
                        label = { Text("Cantidad objetivo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            showDatePicker(context, LocalDate.now()) { fecha -> fechaSeleccionada = fecha }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(fechaSeleccionada?.toString() ?: "Seleccionar fecha")
                    }

                    if (mensajeError.isNotEmpty()) {
                        Text(text = mensajeError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val canridad = cantidadObjetivo.toDoubleOrNull()

                    when {
                        canridad == null || canridad <= 0 -> { mensajeError = "Por favor, introduce una cantidad válida" }
                        fechaSeleccionada == null -> { mensajeError = "Por favor, selecciona una fecha" }
                        fechaSeleccionada!!.isBefore(LocalDate.now()) -> { mensajeError = "La fecha debe ser posterior a hoy" }
                        else -> {
                            homeViewModel.establecerMetaFinanciera(canridad, fechaSeleccionada!!)
                            showDialog = false
                            cantidadObjetivo = ""
                            mensajeError = ""
                        }
                    }
                }) { Text("Confirmar") }
            },
            dismissButton = {
                Button(onClick = { showDialog = false; mensajeError = ""; cantidadObjetivo = "" }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun NewsCard(
    articles: List<Article>,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Noticias Financieras",
                    color = blanco,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = { onRefresh() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar noticias",
                        tint = blanco
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = naranjaClaro)
                    }
                }
                error != null -> {
                    Text(
                        text = error,
                        color = rojo,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                articles.isEmpty() -> {
                    Text(
                        "No hay noticias disponibles.",
                        color = grisClaro,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.height(400.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(articles) { article ->
                            NewsItem(
                                article = article,
                                onItemClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsItem(
    article: Article,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick),
        colors = CardDefaults.cardColors(containerColor = colorFondo.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, grisClaro)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Imagen de la noticia
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(article.urlToImage)
                    .crossfade(true)
                    .build(),
                contentDescription = "News Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.imagen_noticias)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = article.title,
                    color = blanco,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                article.description?.let {
                    Text(
                        text = it,
                        color = grisClaro,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = article.source.name,
                        color = naranjaClaro,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = article.publishedAt.substring(0, 10),
                        color = grisClaro,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}


