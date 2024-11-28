package com.dam2jms.gestiongastosapp.models

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.states.UiState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.O)
class RegisterViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Registro con Email y Password
    fun registerWithEmail(email: String, password: String, username: String) {
        if (!validateRegistrationInputs(email, password, username)) return

        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    initializeNewUser(user, username)
                }
                _uiState.value = UiState(error = "") // Limpia cualquier error previo
            } catch (e: Exception) {
                _uiState.value = UiState(error = "Registration failed: ${e.localizedMessage}")
            }
        }
    }

    // Manejo del resultado del registro con Google
    fun handleGoogleSignUpResult(result: ActivityResult, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)

                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val authResult = auth.signInWithCredential(credential).await()

                val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
                if (isNewUser) {
                    initializeNewUser(authResult.user, account.displayName ?: "Unknown")
                }
                onSuccess()
            } catch (e: ApiException) {
                onFailure("Error al obtener la cuenta de Google: ${e.localizedMessage}")
            } catch (e: Exception) {
                onFailure("Error durante el registro: ${e.localizedMessage}")
            }
        }
    }

    // Validar entradas de registro
    private fun validateRegistrationInputs(email: String, password: String, username: String): Boolean {
        return when {
            email.isBlank() || password.isBlank() || username.isBlank() -> {
                _uiState.value = UiState(error = "All fields are required.")
                false
            }
            password.length < 6 -> {
                _uiState.value = UiState(error = "Password must be at least 6 characters long.")
                false
            }
            else -> true
        }
    }

    // Inicialización de nuevos usuarios en Firestore
    private suspend fun initializeNewUser(user: FirebaseUser?, username: String) {
        user?.let {
            val userMap = mapOf(
                "username" to username,
                "email" to user.email,
                "createdAt" to System.currentTimeMillis()
            )
            db.collection("users").document(user.uid).set(userMap).await()
        }
    }
}

