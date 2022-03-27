package com.example.projectemt.models

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class Data(var arrive: ArrayList<Arrive>, var stopInfo:ArrayList<StopInfo>, var extrainfo:ArrayList<Object>, @SerializedName("Incident")var incident:Incident) {
}