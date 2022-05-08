package com.example.proyectoemtaja.models.peticiones

import com.google.gson.annotations.SerializedName
import java.time.LocalDate


data class LoginResponse(
    @SerializedName("accessToken")
    var token: String,

    @SerializedName("refreshToken")
    var fechaMaxima: String
)