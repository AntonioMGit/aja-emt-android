package com.example.proyectoemtaja.models.timeArrival

import com.google.gson.annotations.SerializedName

data class Incident(
    @SerializedName("ListaIncident")
    var listaIncident: ListaIncident
) {

}
