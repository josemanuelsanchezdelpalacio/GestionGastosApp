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
class RegisterViewModel : ViewModel() {

    //para controlar el estado de la UI
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**metodo para registrar una cuenta directamente con el correo que escriba el usuario**/
    internal suspend fun registrarUsuarioConCorreo(email: String, password: String, auth: FirebaseAuth, context: Context, navController: NavController) {

        //validaciones para los campos de entrada del usuario
        if(!Validaciones.validarCredenciales(email, password, context)) {
            return
        }

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!email.matches(emailPattern.toRegex())) {
            Toast.makeText(context, "Por favor, introduce un correo electrónico real", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isNotEmpty() && password.isNotEmpty()) {
            try {
                //llamo al metodo de firebaseauth para registrar una cuenta y si es correcto navega a homescreen
                val result = auth.fetchSignInMethodsForEmail(email).await()
                if (result.signInMethods?.isNotEmpty() == true) {
                    Toast.makeText(context, "Este correo ya está registrado", Toast.LENGTH_SHORT).show()
                    return
                }

                //enviar correo verificacion
                auth.createUserWithEmailAndPassword(email, password).await()
                auth.currentUser?.sendEmailVerification()?.await()
                Toast.makeText(context, "Registro correcto. Por favor, verifica tu correo", Toast.LENGTH_SHORT).show()
                navController.navigate(AppScreen.LoginScreen.route)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al registrar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**metodo para registrar un usuario con la seleccion de cuentas de google**/
    internal suspend fun registrarUsuarioConGoogle(email: String, password: String, cuenta: GoogleSignInAccount, context: Context, navController: NavController) {

        //compruebo que el id del usuario exista
        cuenta.idToken?.let { token ->

            //obtengo las credenciales a traves del id
            val credencial = GoogleAuthProvider.getCredential(token, null)
            val auth = FirebaseAuth.getInstance()

            try {
                //creo la cuenta en firebase usando las credenciales obtenidas y navega a homescreen si son correctas
                val result = auth.fetchSignInMethodsForEmail(cuenta.email ?: "").await()
                if (result.signInMethods?.isNotEmpty() == true) {
                    Toast.makeText(context, "Esta cuenta de Google ya está registrada", Toast.LENGTH_SHORT).show()
                    return
                }

                val authResult = auth.signInWithCredential(credencial).await()
                authResult.user?.sendEmailVerification()?.await()
                Toast.makeText(context, "Registro con Google correcto", Toast.LENGTH_SHORT).show()
                navController.navigate(AppScreen.LoginScreen.route)

            } catch (e: Exception) {
                Toast.makeText(context, "Error al registrar con Google: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

