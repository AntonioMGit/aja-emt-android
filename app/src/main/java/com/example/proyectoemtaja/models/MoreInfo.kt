package com.example.proyectoemtaja.models

import com.google.gson.annotations.SerializedName

data class MoreInfo (
    @SerializedName(value = "@url")
    val url: String,

    @SerializedName(value = "@type")
    val mimeType: String,

    @SerializedName(value = "@length")
    val length: String
    ){

}
