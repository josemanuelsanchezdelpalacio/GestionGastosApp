package com.dam2jms.gestiongastosapp.utils

import com.dam2jms.gestiongastosapp.states.TransactionUiState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

object FireStoreUtil {
    private val db = FirebaseFirestore.getInstance()

    /**
     * Obtiene todas las transacciones de Firestore para el usuario actual.
     *
     * @param onSuccess Función a ejecutar en caso de éxito con la lista de transacciones.
     * @param onFailure Función a ejecutar en caso de error.
     */
    fun obtenerTransacciones(onSuccess: (List<TransactionUiState>) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("ingresos")
            .get()
            .addOnSuccessListener { ingresosSnapshot ->
                val ingresos = ingresosSnapshot.documents.mapNotNull { document ->
                    val transaccion = document.toObject(TransactionUiState::class.java)
                    transaccion?.apply { id = document.id }
                }
                db.collection("users")
                    .document(userId)
                    .collection("gastos")
                    .get()
                    .addOnSuccessListener { gastosSnapshot ->
                        val gastos = gastosSnapshot.documents.mapNotNull { document ->
                            val transaccion = document.toObject(TransactionUiState::class.java)
                            transaccion?.apply { id = document.id }
                        }
                        onSuccess(ingresos + gastos)
                    }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Agrega una nueva transacción a Firestore.
     *
     * @param transaccion La transacción a agregar.
     * @param onSuccess Función a ejecutar en caso de éxito.
     * @param onFailure Función a ejecutar en caso de error.
     */
    fun añadirTransaccion(coleccion: String, transaccion: TransactionUiState, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection(coleccion)
            .add(transaccion)
            .addOnSuccessListener { documentReference ->
                // Asignar el ID del documento al objeto Transaccion
                documentReference.update("id", documentReference.id)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * metodo para eliminar una transaccion especifica de Firestore.
     * @param collection Colección de la transacción ("ingresos" o "gastos")
     * @param transaccionId ID de la transacción a eliminar
     * @param onSuccess Función a ejecutar en caso de éxito
     * @param onFailure Función a ejecutar en caso de error
     */
    fun eliminarTransaccion(coleccion: String, transaccionId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection(coleccion)
            .document(transaccionId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun editarTransaccion(coleccion: String, transaccion: TransactionUiState, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return

        if (transaccion.id.isEmpty()) {
            onFailure(Exception("El ID de la transacción no puede estar vacío."))
            return
        }

        db.collection("users")
            .document(userId)
            .collection(coleccion)
            .document(transaccion.id)
            .set(transaccion)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun eliminarTransaccion(tipo: String, transaccionId: String, userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val transactionRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection(tipo)
            .document(transaccionId)

        transactionRef.delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }


    fun eliminarMetaFinanciera(
        idUsuario: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Referencia al documento del usuario
        val userRef = db.collection("users").document(idUsuario)

        // Primero verificamos si el documento existe
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // El documento existe, procedemos a actualizar
                    userRef.update(
                        mapOf(
                            "metaFinanciera" to 0.0,
                            "fechaMeta" to null,
                            "diasHastaMeta" to -1,
                            "ahorroDiarioNecesario" to 0.0,
                            "progresoMeta" to 0.0,
                            "financialGoalId" to ""
                        )
                    )
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure(it) }
                } else {
                    // Si el documento no existe, lo creamos con valores por defecto
                    val defaultData = hashMapOf(
                        "metaFinanciera" to 0.0,
                        "fechaMeta" to null,
                        "diasHastaMeta" to -1,
                        "ahorroDiarioNecesario" to 0.0,
                        "progresoMeta" to 0.0,
                        "financialGoalId" to ""
                    )
                    userRef.set(defaultData)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure(it) }
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

}


