package com.example.proyectoemtaja.models.timeArrival

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

/**
 * Datos a recoger para ver los tiempos de los autobuses.
 */
data class Data(

    @SerializedName(value = "Arrive")
    var arrive: ArrayList<Arrive>,

    @SerializedName(value = "StopInfo")
    var stopInfo: ArrayList<StopInfo>,

    @SerializedName(value = "ExtraInfo")
    var extraInfo: ArrayList<Any>,

    @SerializedName("Incident")
    var incident: Incident
) {
}