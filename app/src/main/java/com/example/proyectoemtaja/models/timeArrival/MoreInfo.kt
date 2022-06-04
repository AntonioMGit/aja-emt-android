package com.example.proyectoemtaja.models.timeArrival

import com.google.gson.annotations.SerializedName

/**
 * Datos a recoger para ver los tiempos de los autobuses.
 */
data class MoreInfo(
    @SerializedName(value = "@url")
    val url: String,

    @SerializedName(value = "@type")
    val mimeType: String,

    @SerializedName(value = "@length")
    val length: String
) {

}
