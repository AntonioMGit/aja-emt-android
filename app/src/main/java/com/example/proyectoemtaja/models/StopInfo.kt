package com.example.projectemt.models

import com.google.gson.annotations.SerializedName

data class StopInfo(var label:String,@SerializedName("StopLines")var stopLines:StopLines,@SerializedName("Description")var description:String,var geometry :Point,@SerializedName("Direction")var direction:String) {
}