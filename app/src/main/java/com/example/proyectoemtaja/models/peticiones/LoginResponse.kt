package com.example.proyectoemtaja.models.peticiones

import com.google.gson.annotations.SerializedName
import java.time.LocalDate


data class LoginResponse (
    @SerializedName("token")
    var token: String,

    @SerializedName("fechaMaxima")
    var fechaMaxima: LocalDate
)