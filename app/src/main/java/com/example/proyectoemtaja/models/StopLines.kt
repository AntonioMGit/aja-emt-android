package com.example.proyectoemtaja.models

import com.example.proyectoemtaja.models.Line
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class StopLines(
    @SerializedName("Data")
    var data: ArrayList<Line>

) {

}
