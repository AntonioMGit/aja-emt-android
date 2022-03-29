package com.example.proyectoemtaja.models

import com.google.gson.annotations.SerializedName

data class Line(
    @SerializedName("Label")
    var label:String,

    var line:String,

    @SerializedName("Description")
    var description:String,

    var distance:Int,

    var to:String) {

}
