package com.example.proyectoemtaja.models.peticiones


/**
 * Peticion de cambio de clave
 */
data class CambiarClaveRequest(
    var idUsuario: String,
    var clave: String,
    var token: String
)
