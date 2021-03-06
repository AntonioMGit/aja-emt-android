package com.example.proyectoemtaja.models.timeArrival

import com.google.gson.annotations.SerializedName

/**
 * Datos a recoger para ver los tiempos de los autobuses.
 */
data class Line(
    @SerializedName("Label")
    var label: String,

    var line: String,

    @SerializedName("Description")
    var description: String,

    var distance: Int,

    var to: String
) {

}
