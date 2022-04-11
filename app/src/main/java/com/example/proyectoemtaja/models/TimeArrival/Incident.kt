package com.example.proyectoemtaja.models.TimeArrival

import com.google.gson.annotations.SerializedName

data class Incident(
    @SerializedName("ListaIncident")
    var listaIncident: ListaIncident
) {

}
