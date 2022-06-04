package com.example.proyectoemtaja.models.usuario

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

/**
 * Objeto usuario
 */
class Usuario(
    @SerializedName("correo")
    var correo:String,

    @SerializedName("clave")
    var clave:String,

    @SerializedName("nombre")
    var nombre:String,

    @SerializedName("apellidos")
    var apellidos:String,

    @SerializedName("fechaNacimiento")
    var fechaNacimiento:String,

    @SerializedName("sexo")
    var sexo:Sexo
) {
}