package com.example.proyectoemtaja.models.peticiones

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

/**
 * Respuesta a peticion de actualizar usuario
 */
data class LoginResponse(
    @SerializedName("accessToken")
    var token: String,
)