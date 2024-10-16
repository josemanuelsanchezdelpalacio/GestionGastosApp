package com.dam2jms.gestiongastosapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET

/** Simula una API para obtener categorias de ingresos y gastos*/
data class Categoria(val id: String, val nombre: String, var tipo: String)

object CategoriaAPI {

    /**metodo que obtiene una lista de categorias segun el tipo (ingreso o gasto)
     * @param tipo Tipo de categoria a obtener. Puede ser ingreso o gasto
     * @return Lista de categorias segun el tipo
     * */
    suspend fun obtenerCategorias(tipo: String): List<Categoria> = withContext(Dispatchers.IO) {
        when(tipo){
            "ingreso" -> listOf(
                Categoria(id = "1", nombre = "reembolso", tipo = "ingreso"),
                Categoria(id = "2", nombre = "salario", tipo = "ingreso"),
                Categoria(id = "3", nombre = "otros", tipo = "ingreso")
            )

            "gasto" -> listOf(
                Categoria(id = "4", nombre = "casa", tipo = "gasto"),
                Categoria(id = "5", nombre = "ropa", tipo = "gasto"),
                Categoria(id = "6", nombre = "educacion", tipo = "gasto"),
                Categoria(id = "7", nombre = "entretenimiento", tipo = "gasto"),
                Categoria(id = "8", nombre = "regalo", tipo = "gasto"),
                Categoria(id = "9", nombre = "mascota", tipo = "gasto"),
                Categoria(id = "10", nombre = "viajes", tipo = "gasto"),
                Categoria(id = "11", nombre = "otros", tipo = "gasto")
            )
            else -> emptyList()
        }
    }
}