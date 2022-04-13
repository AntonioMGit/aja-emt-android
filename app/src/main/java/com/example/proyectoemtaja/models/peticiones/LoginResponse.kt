package com.example.proyectoemtaja.models.peticiones

import com.google.gson.annotations.SerializedName


data class LoginResponse (@SerializedName("token") var token: String)