package com.dam2jms.gestiongastosapp.models

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.states.UiState
import com.dam2jms.gestiongastosapp.ui.theme.blanco
import com.dam2jms.gestiongastosapp.ui.theme.naranjaOscuro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class AuxViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun onChange(email: String, password: String) {
        _uiState.update { it.copy(email = email, password = password) }
    }

    fun visibilidadPassword() {
        _uiState.value =
            _uiState.value.copy(visibilidadPasssword = !uiState.value.visibilidadPasssword)
    }

    fun setScreenActual(screen: AppScreen) {
        _uiState.update { it.copy(screenActual = screen) }
    }

    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun bottomAppBar(navController: NavController) {
        BottomAppBar(
            containerColor = naranjaOscuro,
            contentColor = blanco
        ) {
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Home, "Inicio") },
                label = { Text("Inicio") },
                selected = uiState.value.screenActual == AppScreen.HomeScreen,
                onClick = {
                    navController.navigate(AppScreen.HomeScreen.route) {
                        popUpTo(AppScreen.HomeScreen.route) { inclusive = true }
                    }
                    setScreenActual(AppScreen.HomeScreen)
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.List, "Transacciones") },
                label = { Text("Transacciones") },
                selected = uiState.value.screenActual == AppScreen.TransactionScreen,
                onClick = {
                    val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                    navController.navigate(AppScreen.TransactionScreen.createRoute(currentDate))
                    setScreenActual(AppScreen.TransactionScreen)
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.History, "Historial") },
                label = { Text("Historial") },
                selected = uiState.value.screenActual == AppScreen.HistoryScreen,
                onClick = {
                    navController.navigate(AppScreen.HistoryScreen.route)
                    setScreenActual(AppScreen.HistoryScreen)
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Calculate, "Calculadora") },
                label = { Text("Calculadora") },
                selected = uiState.value.screenActual == AppScreen.CalculadoraScreen,
                onClick = {
                    navController.navigate(AppScreen.CalculadoraScreen.route)
                    setScreenActual(AppScreen.CalculadoraScreen)
                }
            )
        }
    }
}



