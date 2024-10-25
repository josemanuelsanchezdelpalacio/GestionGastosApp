package com.dam2jms.gestiongastosapp.screens

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.dam2jms.gestiongastosapp.ui.theme.gris
import com.dam2jms.gestiongastosapp.ui.theme.naranjaClaro
import com.dam2jms.gestiongastosapp.ui.theme.naranjaOscuro
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, auxViewModel: AuxViewModel, loginViewModel: LoginViewModel, auth: FirebaseAuth, googleSignInClient: GoogleSignInClient){

    val context = LocalContext.current
    val uiState by auxViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "INICIAR SESION", fontWeight = FontWeight.Bold, color = blanco)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = naranjaOscuro)
            )
        },
        containerColor = colorFondo
    ) { paddingValues ->
        LoginScreenBody(paddingValues = paddingValues, navController = navController, auxViewModel = auxViewModel, loginViewModel = loginViewModel, uiState = uiState, context = context, auth = auth, googleSignInClient = googleSignInClient)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenBody(paddingValues: PaddingValues, navController: NavController, auxViewModel: AuxViewModel, loginViewModel: LoginViewModel, uiState: UiState, context: Context, auth: FirebaseAuth, googleSignInClient: GoogleSignInClient){

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.imagen_logo),
            contentDescription = "imagen_logo",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = { auxViewModel.onChange(it, uiState.password) },
            label = { Text(text = "Correo electronico") },
            singleLine = true,
            leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = "Correo electronico", tint = naranjaClaro) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = naranjaClaro,
                unfocusedBorderColor = gris,
                focusedLabelColor = naranjaClaro
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { auxViewModel.onChange(uiState.email, it) },
            label = { Text(text = "Contraseña") },
            singleLine = true,
            leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = "Contraseña", tint = naranjaClaro) },
            visualTransformation = if(uiState.visibilidadPasssword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { auxViewModel.visibilidadPassword() }) {
                    Icon(
                        imageVector = if(uiState.visibilidadPasssword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if(uiState.visibilidadPasssword) "Mostrar contraseña" else "Ocultar contraseña",
                        tint = naranjaClaro
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = naranjaClaro,
                unfocusedBorderColor = gris,
                focusedLabelColor = naranjaClaro
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { scope.launch { loginViewModel.iniciarSesionConCorreo(uiState.email, uiState.password, auth, context, navController) } },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = naranjaClaro)
        ) {
            Text(text = "Iniciar sesion", color = blanco)
        }
        Spacer(modifier = Modifier.height(16.dp))

        val googleSignInLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { resultado ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(resultado.data)
            try{
                val cuenta = task.getResult(ApiException::class.java)
                scope.launch { loginViewModel.registrarUsuarioConGoogle(uiState.email, uiState.password, cuenta, context, navController) }
            }catch (e: ApiException){
                Toast.makeText(context, "Error al iniciar sesion con Google: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        Button(
            onClick = {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = naranjaClaro)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(id = R.drawable.icono_google) , contentDescription = "icono google", tint = blanco)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Iniciar sesion con Google", color = blanco)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate(AppScreen.RegisterScreen.route) }) {
            Text(text = "¿No tienes cuenta? Registrate", color = naranjaClaro)
        }
    }
}


