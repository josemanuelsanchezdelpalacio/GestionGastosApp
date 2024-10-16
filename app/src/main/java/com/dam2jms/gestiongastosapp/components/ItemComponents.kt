package com.dam2jms.gestiongastosapp.components

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.ui.theme.*
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.data.Categoria
import com.dam2jms.gestiongastosapp.models.CurrencyViewModel
import com.dam2jms.gestiongastosapp.models.TransactionViewModel
import com.dam2jms.gestiongastosapp.states.TransactionState

object ItemComponents {

    /**
     * componente que representa un elemento de transaccion en la lista
     * @param navController controlador de navegacion entre pantallas
     * @param mvvm viewmodel que tiene la logica y el estado de la transaccion
     * @param context contexto para mostrar mensajes
     * **/
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun TransactionItem(transaccion: TransactionState, navController: NavController, mvvm: TransactionViewModel, context: Context) {

        var expanded by remember { mutableStateOf(false) }
        var validarElimar by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { expanded = !expanded }
                .border(1.dp, naranjaClaro, shape = RoundedCornerShape(8.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Categoria: ${transaccion.categoria}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Cantidad: ${transaccion.cantidad}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Fecha: ${transaccion.fecha}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Tipo: ${transaccion.tipo.capitalize()}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )

                if (expanded) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                navController.navigate(
                                    AppScreen.EditTransactionScreen.createRoute(
                                        transaccion.id
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = naranjaClaro),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "modificar",
                                tint = blanco
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Modificar", color = blanco)
                        }

                        Button(
                            onClick = { validarElimar = true },
                            colors = ButtonDefaults.buttonColors(containerColor = rojo),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "eliminar",
                                tint = blanco
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Eliminar", color = blanco)
                        }
                    }
                }
            }
        }

        if (validarElimar) {
            AlertDialog(
                onDismissRequest = { validarElimar = false },
                title = { Text(text = "Confirmar eliminacion") },
                text = { Text(text = "¿Estas seguro de eliminar esta transaccion?") },
                confirmButton = {
                    Button(
                        onClick = {
                            mvvm.eliminarTransaccionExistente(
                                coleccion = if (transaccion.tipo == "ingreso") "ingresos" else "gastos",
                                transaccionId = transaccion.id,
                                context = context
                            )
                            validarElimar = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = rojo)
                    ) {
                        Text(text = "Eliminar")
                    }
                },
                dismissButton = {
                    Button(onClick = { validarElimar = false }) {
                        Text(text = "Cancelar")
                    }
                }
            )
        }
    }

    /**
     * metodo que devuelve un icono de categoria basado en su nombre
     * @param categoria nombre de la categoria
     * @return un icono que representa la categoria
     * */
    @Composable
    fun obtenerIconoCategoria(categoria: String): ImageVector{
        return when(categoria.toLowerCase()){
            "salario" -> Icons.Default.Money
            "casa" -> Icons.Default.Home
            "ropa" -> Icons.Default.ShoppingBag
            "educacion" -> Icons.Default.School
            "entretenimiento" -> Icons.Default.Movie
            "regalo" -> Icons.Default.CardGiftcard
            "mascota" -> Icons.Default.Pets
            "viajes" -> Icons.Default.Flight
            else -> Icons.Default.Category
        }
    }

    /**
     * componente que representa una categoria, usado para mostrar una lista de categorias
     * @param categoria la categoria que sera mostrada
     * @param onClick accion que sera ejecutada cuandos e haga clic en el elemento
     * */
    @Composable
    fun categoriaItem(categoria: Categoria, onClick:() -> Unit){

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row (
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Icon(
                    imageVector = obtenerIconoCategoria(categoria = categoria.nombre),
                    contentDescription = categoria.nombre,
                    tint = naranjaClaro,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = categoria.nombre, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun CurrencySelectionDialog(
    currencyViewModel: CurrencyViewModel,
    onDismiss: () -> Unit,
    onCurrencySelected: (String) -> Unit
) {
    val currencies by currencyViewModel.monedasDisponibles.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Moneda") },
        text = {
            LazyColumn {
                items(currencies) { currency ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCurrencySelected(currency) }
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currencyViewModel.obtenerSimboloMoneda(currency),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(40.dp)
                        )
                        Text(text = currencyViewModel.obtenerNombreMoneda(currency))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}


