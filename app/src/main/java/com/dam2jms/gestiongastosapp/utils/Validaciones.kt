package com.dam2jms.gestiongastosapp.utils

import android.content.Context
import android.util.Patterns
import android.widget.Toast

object Validaciones{

    /**Valida el correo electronico para que tenga la estructura de un email valido*/
    fun validarCorreo(context: Context, email: String): Boolean {
        val patronEmail = Patterns.EMAIL_ADDRESS
        return if(email.isNotEmpty() && patronEmail.matcher(email).matches()) {
            true
        }else{
            Toast.makeText(context, "Ingrese un correo electronico valido", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**Valida la contraseña para que tenga como minimo 6 caracteres (el minimo para firebase)*/
    fun validaContraseña(context: Context, password: String): Boolean {
        return if(password.length >= 6){
            true
        }else{
            Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /** Metodo para validar la cantidad para la transaccion **/
    fun validarCantidad(context: Context, amount: String): Boolean {
        return if (amount.isNotEmpty() && amount.toDoubleOrNull() != null && amount.toDouble() > 0) {
            true
        } else {
            Toast.makeText(context, "Ingrese una cantidad valida", Toast.LENGTH_SHORT).show()
            false
        }
    }


    /** Metodo para validar la descripcion de la transaccion **/
    fun validarDescripcion(context: Context, description: String): Boolean {
        return if (description.isNotEmpty()) {
            true
        } else {
            Toast.makeText(context, "Ingrese una descripcion", Toast.LENGTH_SHORT).show()
            false
        }
    }
}


