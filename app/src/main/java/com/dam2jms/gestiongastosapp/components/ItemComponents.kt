import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.data.Categoria
import com.dam2jms.gestiongastosapp.data.obtenerIconoCategoria
import com.dam2jms.gestiongastosapp.ui.theme.*
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.models.MonedasViewModel
import com.dam2jms.gestiongastosapp.states.TransactionUiState
import kotlinx.coroutines.flow.update

object ItemComponents {

    /**metodo para crear un componente reutilizable para la innformacion de las transacciones en un card con opciones para editar y eliminar**/
    @Composable
    fun TransaccionItem(
        transaccion: TransactionUiState,
        monedaActual: String,
        navController: NavController,
        onEliminar: (String) -> Unit,
        onClick: () -> Unit // Agregado el parámetro onClick
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = colorFondo)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable(onClick = onClick), // Hacer que toda la tarjeta sea clickeable
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Información de la transacción
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaccion.descripcion,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = blanco
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${monedaActual} ${String.format("%,.2f", transaccion.cantidad)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (transaccion.tipo == "ingreso") verde else rojo
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = transaccion.fecha,
                        style = MaterialTheme.typography.bodySmall,
                        color = grisClaro
                    )
                }

                // Botones para editar y eliminar
                Row {
                    IconButton(
                        onClick = {
                            navController.navigate(AppScreen.EditTransactionScreen.createRoute(transaccion.id))
                        },
                        content = {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar transacción", tint = blanco)
                        }
                    )

                    IconButton(
                        onClick = { onEliminar(transaccion.id) },
                        content = {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar transacción", tint = blanco)
                        }
                    )
                }
            }
        }
    }

    /**metodo reutilizable para crear una lista de monedas seleccionables**/
    @Composable
    fun SelectorMoneda(monedasViewModel: MonedasViewModel, onDismiss: () -> Unit, monedaSeleccionada: (String) -> Unit) {

        val monedas by monedasViewModel.monedasDisponibles.collectAsState()

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Seleccionar moneda") },
            text = {
                LazyColumn {
                    items(monedas) { moneda ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { monedaSeleccionada(moneda) }
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = monedasViewModel.obtenerSimboloMoneda(moneda),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(40.dp)
                            )
                            Text(text = monedasViewModel.obtenerNombreMoneda(moneda))
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SelectorCategoria(selectedCategory: String, categorias: List<Categoria>, onCategorySelected: (String) -> Unit) {

        var expanded by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Categoría",
                style = MaterialTheme.typography.bodyLarge,
                color = blanco,
                fontWeight = FontWeight.Medium
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    value = selectedCategory.replaceFirstChar { it.uppercase() },
                    onValueChange = {}, // Este campo es solo de lectura
                    leadingIcon = {
                        Icon(
                            imageVector = obtenerIconoCategoria(selectedCategory),
                            contentDescription = "Icono categoría",
                            tint = naranjaClaro
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    textStyle = TextStyle(color = blanco),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = naranjaClaro,
                        unfocusedBorderColor = naranjaClaro,
                        focusedLabelColor = naranjaClaro,
                        unfocusedLabelColor = naranjaClaro,
                        cursorColor = naranjaClaro,
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(grisClaro, grisClaro)
                            )
                        )
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = obtenerIconoCategoria(categoria.nombre),
                                        contentDescription = "Icono ${categoria.nombre}",
                                        tint = naranjaClaro,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = categoria.nombre.replaceFirstChar { it.uppercase() },
                                        color = blanco
                                    )
                                }
                            },
                            onClick = {
                                onCategorySelected(categoria.nombre)
                                expanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = blanco,
                                leadingIconColor = naranjaClaro
                            ),
                            modifier = Modifier.background(
                                if (categoria.nombre == selectedCategory) {
                                    naranjaClaro.copy(alpha = 0.1f)
                                } else {
                                    Color.Transparent
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}



