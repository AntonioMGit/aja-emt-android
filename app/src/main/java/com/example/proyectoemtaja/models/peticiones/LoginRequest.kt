package com.example.proyectoemtaja.models.peticiones

import com.google.gson.annotations.SerializedName


/**
 * Peticion de login
 */
data class LoginRequest(
    @SerializedName("correo")
    var correo: String,

    @SerializedName("clave")
    var clave: String
)