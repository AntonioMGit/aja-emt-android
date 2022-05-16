package com.example.proyectoemtaja.models.paradasLinea

import com.example.proyectoemtaja.models.timeArrival.Point
import com.google.gson.annotations.SerializedName

data class StopsParadasLinea (
    var stop: String,
    var name: String,
    var postalAddress: String,
    var geometry: Point,
    var pmv: String,
    @SerializedName("dataLine")
    var dataLine: ArrayList<String>
){
    constructor(l: StopsParadasLinea) : this(l.stop, l.name, l.postalAddress, l.geometry, l.pmv, l.dataLine)

}