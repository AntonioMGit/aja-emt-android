package com.example.proyectoemtaja.models.timeArrival

import com.google.gson.annotations.SerializedName

data class Arrive(

    var line: String,
    var stop: String,
    var isHead: String,
    var destination: String,
    var deviation: Int,
    var bus: Int,
    var geometry: Point,
    var estimateArrive: Int,

    @SerializedName("DistanceBus")
    var distanceBus: Int,

    var positionTypeBus: String
) {

}
