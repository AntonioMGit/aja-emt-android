package com.example.proyectoemtaja.models.paradasLinea

import com.google.gson.annotations.SerializedName

data class DataParadasLinea (

    var line: String,
    var label: String,
    @SerializedName("stops")
    var stops: ArrayList<StopsParadasLinea>,
    @SerializedName("timeTable")
    var timeTable: ArrayList<TimeTableParadasLinea>

){
    constructor(l: DataParadasLinea) : this(l.line, l.label, l.stops, l.timeTable)
}