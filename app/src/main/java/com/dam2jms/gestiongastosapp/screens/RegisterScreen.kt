package com.dam2jms.gestiongastosapp.screens

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.R
import com.dam2jms.gestiongastosapp.models.RegisterViewModel
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.ui.theme.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, registerViewModel: RegisterViewModel, googleSignInClient: GoogleSignInClient) {

    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        registerViewModel.handleGoogleSignUpResult(
            result = result,
            onSuccess = {
                navController.navigate(AppScreen.HomeScreen.route) {
                    popUpTo(AppScreen.RegisterScreen.route) { inclusive = true }
                }
            },
            onFailure = { errorMessage ->
                registerViewModel.uiState.value.copy(error = errorMessage)
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Crear Cuenta",
                        color = blanco,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorFondo
                ),
                modifier = Modifier.shadow(4.dp)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorFondo)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // App Logo
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    color = naranjaClaro
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.imagen_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(80.dp),
                            tint = Color.Unspecified
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                RegisterForm(
                    registerViewModel = registerViewModel,
                    onRegisterSuccess = {
                        navController.navigate(AppScreen.HomeScreen.route) {
                            popUpTo(AppScreen.RegisterScreen.route) { inclusive = true }
                        }
                    },
                    isLoading = isLoading,
                    onLoadingChange = { isLoading = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Google Sign Up Button
                Button(
                    onClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blanco,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icono_google),
                            contentDescription = "Google Icon",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Continuar con Google",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(
                    onClick = { navController.navigate(AppScreen.LoginScreen.route) }
                ) {
                    Text(
                        "¿Ya tienes una cuenta? Iniciar sesión",
                        color = naranjaClaro,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorFondo.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = naranjaClaro)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterForm(
    registerViewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    isLoading: Boolean,
    onLoadingChange: (Boolean) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val uiState by registerViewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario", color = grisClaro) },
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = naranjaClaro
                )
            },
            singleLine = true,
            isError = username.isBlank(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = naranjaClaro,
                focusedBorderColor = naranjaClaro,
                unfocusedBorderColor = grisClaro,
                focusedLabelColor = naranjaClaro,
                unfocusedLabelColor = grisClaro
            ),
            textStyle = TextStyle(color = blanco)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = grisClaro) },
            leadingIcon = {
                Icon(
                    Icons.Default.Email,
                    contentDescription = null,
                    tint = naranjaClaro
                )
            },
            singleLine = true,
            isError = email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = naranjaClaro,
                focusedBorderColor = naranjaClaro,
                unfocusedBorderColor = grisClaro,
                focusedLabelColor = naranjaClaro,
                unfocusedLabelColor = grisClaro
            ),
            textStyle = TextStyle(color = blanco)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", color = grisClaro) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = naranjaClaro
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar Contraseña" else "Mostrar Contraseña",
                        tint = if (passwordVisible) naranjaClaro else grisClaro
                    )
                }
            },
            singleLine = true,
            isError = password.isNotBlank() && password.length < 6,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = naranjaClaro,
                focusedBorderColor = naranjaClaro,
                unfocusedBorderColor = grisClaro,
                focusedLabelColor = naranjaClaro,
                unfocusedLabelColor = grisClaro
            ),
            textStyle = TextStyle(color = blanco)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                onLoadingChange(true)
                registerViewModel.registerWithEmail(email, password, username)
                if (uiState.error == null) {
                    onRegisterSuccess()
                }
                onLoadingChange(false)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = email.isNotBlank() && password.isNotBlank() && username.isNotBlank() && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = naranjaClaro,
                contentColor = Color.Black,
                disabledContainerColor = grisClaro
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.Black
                )
            } else {
                Text(
                    "Registrarse",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // Error Message
        AnimatedVisibility(visible = uiState.error != null) {
            Text(
                text = uiState.error ?: "",
                color = rojo,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
