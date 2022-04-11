package com.example.proyectoemtaja.models.listaParadas

import com.example.proyectoemtaja.models.TimeArrival.Point
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class DataListaParadas (
    var node:String,
    var geometry: Point,
    var name:String,
    var wifi:String,
    @SerializedName("lines")
    var lines : ArrayList<String>
    ){
}