package com.example.proyectoemtaja.models.timeArrival

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

/**
 * Datos a recoger para ver los tiempos de los autobuses.
 */
data class TimeArrivalBus(
    var code: String,

    var description: String,

    var datetime: String,

    @SerializedName("data")
    var data: ArrayList<Data>
) {
}

