package com.example.proyectoemtaja.models

import com.google.gson.annotations.SerializedName

data class Incident(
    @SerializedName("ListaIncident")
    var listaIncident: ListaIncident) {

}
