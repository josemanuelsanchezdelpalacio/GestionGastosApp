package com.dam2jms.gestiongastosapp.screens

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.dam2jms.gestiongastosapp.models.RegisterViewModel
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
fun RegisterScreen(
    navController: NavController,
    auxViewModel: AuxViewModel,
    registerViewModel: RegisterViewModel,
    auth: FirebaseAuth,
    googleSignInClient: GoogleSignInClient
) {

    val context = LocalContext.current
    val uiState by auxViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "REGISTRO", fontWeight = FontWeight.Bold, color = blanco) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBackIosNew, contentDescription = "atras", tint = blanco)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colorFondo)
            )
        },
        containerColor = colorFondo
    ) { paddingValues ->
        RegisterScreenBody(
            paddingValues = paddingValues,
            navController = navController,
            auxViewModel = auxViewModel,
            registerViewModel = registerViewModel,
            uiState = uiState,
            context = context,
            auth = auth,
            googleSignInClient = googleSignInClient
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenBody(
    paddingValues: PaddingValues,
    navController: NavController,
    auxViewModel: AuxViewModel,
    registerViewModel: RegisterViewModel,
    uiState: UiState,
    context: Context,
    auth: FirebaseAuth,
    googleSignInClient: GoogleSignInClient
) {

    val scope = rememberCoroutineScope()

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
                Icon(imageVector = Icons.Filled.Email, contentDescription = "Correo electronico", tint = naranjaClaro)
            },
            textStyle = TextStyle(color = blanco),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = naranjaClaro,
                unfocusedBorderColor = grisClaro,
                focusedLabelColor = naranjaClaro,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { auxViewModel.onChange(uiState.email, it) },
            label = { Text(text = "Contraseña", color = blanco) },
            singleLine = true,
            textStyle = TextStyle(color = blanco),
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
                Icon(imageVector = Icons.Filled.Lock, contentDescription = "Contraseña", tint = naranjaClaro)
            },
            visualTransformation = if (uiState.visibilidadPasssword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { auxViewModel.visibilidadPassword() }) {
                    Icon(
                        imageVector = if (uiState.visibilidadPasssword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (uiState.visibilidadPasssword) "Mostrar contraseña" else "Ocultar contraseña",
                        tint = naranjaClaro
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    registerViewModel.registrarUsuarioConCorreo(uiState.email, uiState.password, auth, context, navController)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = naranjaClaro)
        ) {
            Text(text = "Registrarse", color = blanco, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        val seleccionarCuentaLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { resultado ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(resultado.data)
            try {
                val cuenta = task.getResult(ApiException::class.java)
                if (cuenta != null) {
                    scope.launch { registerViewModel.registrarUsuarioConGoogle(uiState.email, uiState.password, cuenta, context, navController) }
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Error al registrarse con Google: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        Button(
            onClick = {
                googleSignInClient.signOut().addOnCompleteListener {
                    val registrarCuenta = googleSignInClient.signInIntent
                    seleccionarCuentaLauncher.launch(registrarCuenta)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = naranjaClaro)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(id = R.drawable.icono_google), contentDescription = "icono google", tint = blanco)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Registrar sesión con Google", color = blanco, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate(AppScreen.LoginScreen.route) }) {
            Text(text = "¿Ya tienes cuenta? Inicia sesión", color = naranjaClaro)
        }
    }
}

