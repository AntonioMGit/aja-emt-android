package com.example.proyectoemtaja.models.paradasLinea

import com.google.gson.annotations.SerializedName

data class TimeTableParadasLinea (

    var IdLine:String,
    var Label:String,
    var nameA:String,
    var nameB:String,
    @SerializedName("typeOfDays")
    var typeOfDays:ArrayList<DirectionsParadasLinea>

){
    constructor(l: TimeTableParadasLinea) : this(l.IdLine, l.Label, l.nameA, l.nameB, l.typeOfDays)

}