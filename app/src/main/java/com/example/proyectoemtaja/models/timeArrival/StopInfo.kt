package com.example.proyectoemtaja.models.timeArrival

import com.google.gson.annotations.SerializedName

/**
 * Datos a recoger para ver los tiempos de los autobuses.
 */
data class StopInfo(
    var label: String,

    @SerializedName("StopLines")
    var stopLines: StopLines,

    @SerializedName("Description")
    var description: String,

    var geometry: Point,

    @SerializedName("Direction")
    var direction: String
) {
}