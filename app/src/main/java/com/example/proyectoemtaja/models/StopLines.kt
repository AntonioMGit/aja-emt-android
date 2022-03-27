package com.example.projectemt.models

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class StopLines(@SerializedName("Data")var data: ArrayList<Line>) {

}
