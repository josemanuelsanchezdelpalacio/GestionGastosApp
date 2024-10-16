package com.dam2jms.gestiongastosapp.models

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.states.UiState
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.O)
class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    internal suspend fun iniciarSesionConCorreo(email: String, password: String, auth: FirebaseAuth, context: Context, navController: NavController) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                Toast.makeText(context, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show()
                navController.navigate(AppScreen.HomeScreen.route) {
                    popUpTo(AppScreen.LoginScreen.route) { inclusive = true }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error al intentar iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Existen campos vacíos", Toast.LENGTH_SHORT).show()
        }
    }

    internal suspend fun registrarUsuarioConGoogle(cuenta: GoogleSignInAccount, context: Context, navController: NavController) {
        cuenta.idToken?.let {
            val credencial = GoogleAuthProvider.getCredential(it, null)
            val auth = FirebaseAuth.getInstance()

            try {
                auth.signInWithCredential(credencial).await()
                Toast.makeText(context, "Inicio de sesión con Google correcto", Toast.LENGTH_SHORT).show()
                navController.navigate(AppScreen.HomeScreen.route) {
                    popUpTo(AppScreen.LoginScreen.route) { inclusive = true }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error al intentar iniciar sesión con Google: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

