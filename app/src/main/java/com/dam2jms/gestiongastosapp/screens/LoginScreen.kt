package com.dam2jms.gestiongastosapp.screens

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.gestiongastosapp.R
import com.dam2jms.gestiongastosapp.models.AuxViewModel
import com.dam2jms.gestiongastosapp.models.LoginViewModel
import com.dam2jms.gestiongastosapp.navigation.AppScreen
import com.dam2jms.gestiongastosapp.states.UiState
import com.dam2jms.gestiongastosapp.ui.theme.blanco
import com.dam2jms.gestiongastosapp.ui.theme.colorFondo
import com.dam2jms.gestiongastosapp.ui.theme.grisClaro
import com.dam2jms.gestiongastosapp.ui.theme.naranjaClaro
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    auxViewModel: AuxViewModel,
    loginViewModel: LoginViewModel,
    auth: FirebaseAuth,
    googleSignInClient: GoogleSignInClient
) {

    val context = LocalContext.current
    val uiState by auxViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "INICIAR SESION", fontWeight = FontWeight.Bold, color = blanco) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colorFondo)
            )
        },
        containerColor = colorFondo
    ) { paddingValues ->
        LoginScreenBody(
            paddingValues = paddingValues,
            navController = navController,
            auxViewModel = auxViewModel,
            loginViewModel = loginViewModel,
            auth = auth,
            googleSignInClient = googleSignInClient,
            uiState = uiState,
            context = context,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenBody(
    paddingValues: PaddingValues,
    navController: NavController,
    auxViewModel: AuxViewModel,
    loginViewModel: LoginViewModel,
    auth: FirebaseAuth,
    googleSignInClient: GoogleSignInClient,
    uiState: UiState,
    context: Context
) {

    val scope = rememberCoroutineScope()

    var showAlertDialog by remember { mutableStateOf(false) }
    var emailRecuperarPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(naranjaClaro)
                .border(BorderStroke(4.dp, blanco), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.imagen_logo),
                contentDescription = "imagen_logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { auxViewModel.onChange(it, uiState.password) },
            label = { Text(text = "Correo electronico", color = blanco) },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Correo electronico",
                    tint = naranjaClaro
                )
            },
            textStyle = TextStyle(blanco),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = naranjaClaro,
                unfocusedBorderColor = grisClaro,
                focusedLabelColor = naranjaClaro,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { auxViewModel.onChange(uiState.email, it) },
            label = { Text(text = "Contraseña", color = blanco) },
            singleLine = true,
            textStyle = TextStyle(blanco),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = naranjaClaro,
                unfocusedBorderColor = grisClaro,
                focusedLabelColor = naranjaClaro,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Contraseña",
                    tint = naranjaClaro
                )
            },
            //para cambiar entre contraseña oculta y mostrada
            visualTransformation = if (uiState.visibilidadPasssword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { auxViewModel.visibilidadPassword() }) {
                    Icon(
                        imageVector = if (uiState.visibilidadPasssword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (uiState.visibilidadPasssword) "Mostrar contraseña" else "Ocultar contraseña",
                        tint = naranjaClaro
                    )
                }
            },
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = {
                showAlertDialog = true
                emailRecuperarPassword = uiState.email
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "¿Olvidaste la contraseña?", color = naranjaClaro, style = MaterialTheme.typography.bodyMedium)
        }

        //alertDialog para recuperar la contraseña a traves del correo que indique el usuario
        if (showAlertDialog) {
            AlertDialog(
                onDismissRequest = { showAlertDialog = false },
                title = { Text(text = "Recuperar contraseña", style = MaterialTheme.typography.titleLarge) },
                text = {
                    Column {
                        Text(text = "Introduce tu correo para recuperar la contraseña", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
                        OutlinedTextField(
                            value = emailRecuperarPassword,
                            onValueChange = { emailRecuperarPassword = it },
                            label = { Text(text = "Correo electronico") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = naranjaClaro, unfocusedBorderColor = grisClaro)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                loginViewModel.recuperarContraseña(emailRecuperarPassword, context)
                                showAlertDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = naranjaClaro)
                    ) {
                        Text(text = "Enviar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showAlertDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = naranjaClaro)
                    ) {
                        Text(text = "Cancelar")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    loginViewModel.iniciarSesionConCorreo(uiState.email, uiState.password, auth, context, navController)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = naranjaClaro)
        ) {
            Text(text = "Iniciar sesion", color = blanco, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        val seleccionarCuentaLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { resultado ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(resultado.data)
            try {
                val cuenta = task.getResult(ApiException::class.java)
                if (cuenta != null) {
                    scope.launch { loginViewModel.iniciarSesionConGoogle(cuenta, context, navController) }
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Error al iniciar sesión con Google: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        Button(
            onClick = {
                //para que se cierre sesion y que no siempre entre con la misma cuenta
                googleSignInClient.signOut().addOnCompleteListener {
                    val inicioSesion = googleSignInClient.signInIntent
                    seleccionarCuentaLauncher.launch(inicioSesion)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = naranjaClaro)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(id = R.drawable.icono_google),
                    contentDescription = "icono google",
                    tint = blanco
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Iniciar sesion con Google", color = blanco, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate(AppScreen.RegisterScreen.route) }) {
            Text(text = "¿No tienes cuenta? Registrate", color = naranjaClaro)
        }
    }
}

