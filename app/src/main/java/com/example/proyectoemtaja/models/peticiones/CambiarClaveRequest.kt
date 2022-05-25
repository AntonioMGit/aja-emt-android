package com.example.proyectoemtaja.models.peticiones

data class CambiarClaveRequest(
    var idUsuario: String,
    var clave: String,
    var token: String
)
