package com.dam2jms.gestiongastosapp.models

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.states.UiState
import com.dam2jms.gestiongastosapp.utils.Validaciones
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.O)
class LoginViewModel : ViewModel() {

    //para controlar el estado de la UI
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**metodo para iniciar sesion directamente con el correo que escriba el usuario**/
    internal suspend fun iniciarSesionConCorreo(email: String, password: String, auth: FirebaseAuth, context: Context, navController: NavController) {

        //validaciones para comprobar que el email y la password tengan el formato correcto para firebase
        if(!Validaciones.validarCredenciales(email, password, context)){
            return
        }

        if (email.isNotEmpty() && password.isNotEmpty()) {
            try {
                //llamo al metodo de firebaseauth para iniciar sesion y si es correcto navega a homescreen
                auth.signInWithEmailAndPassword(email, password).await()
                Toast.makeText(context, "Inicio de sesion correcto", Toast.LENGTH_SHORT).show()
                navController.navigate(AppScreen.HomeScreen.route)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al intentar iniciar sesion: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Existen campos vacios", Toast.LENGTH_SHORT).show()
        }
    }

    /**metodo para inciiar sesion con la seleccion de cuentas de google**/
    internal suspend fun iniciarSesionConGoogle(cuenta: GoogleSignInAccount, context: Context, navController: NavController) {

        //compruebo que el id del usuario exista
        val idToken = cuenta.idToken ?: run {
            Toast.makeText(context, "Token de ID no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        //obtengo las credenciales a traves del id
        val credencial = GoogleAuthProvider.getCredential(idToken, null)

        val auth = FirebaseAuth.getInstance()

        try {
            //inicio sesion usando las credenciales obtenidas y navega a homescreen si son correctas
            auth.signInWithCredential(credencial).await()
            Toast.makeText(context, "Inicio de sesion con Google correcto", Toast.LENGTH_SHORT).show()
            navController.navigate(AppScreen.HomeScreen.route)
        } catch (e: Exception) {
            Toast.makeText(context, "Error al iniciar sesion", Toast.LENGTH_SHORT).show()
        }
    }

    /**metodo para recuperar la contraseña a traves del correo que introduzcal el usuario**/
    internal suspend fun recuperarContraseña(email: String, context: Context) {

        //validaciones para comprobar que el email tenga el formato correcto
        if(!Validaciones.validarEmail(email)){
            return
        }

        if (email.isNotEmpty()) {
            try {
                //envio un correo de recuperacion a traves del email introducido por el usuario
                val auth = FirebaseAuth.getInstance()
                auth.sendPasswordResetEmail(email).await()
                Toast.makeText(context, "Se ha enviado un correo para recuperar la contraseña", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al enviar el correo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Introduce un correo electronico valido", Toast.LENGTH_SHORT).show()
        }
    }
}

