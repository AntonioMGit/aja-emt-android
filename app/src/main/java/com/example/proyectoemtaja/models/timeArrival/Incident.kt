package com.example.proyectoemtaja.models.timeArrival

import com.google.gson.annotations.SerializedName

/**
 * Datos a recoger para ver los tiempos de los autobuses.
 */
data class Incident(
    @SerializedName("ListaIncident")
    var listaIncident: ListaIncident
) {

}
