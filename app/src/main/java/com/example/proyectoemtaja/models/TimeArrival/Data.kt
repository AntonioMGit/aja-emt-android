package com.example.proyectoemtaja.models.TimeArrival

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class Data (

    @SerializedName(value = "Arrive")
    var arrive: ArrayList<Arrive>,

    @SerializedName(value = "StopInfo")
    var stopInfo:ArrayList<StopInfo>,

    @SerializedName(value = "ExtraInfo")
    var extraInfo:ArrayList<Any>,

    @SerializedName("Incident")
    var incident: Incident
) {
}