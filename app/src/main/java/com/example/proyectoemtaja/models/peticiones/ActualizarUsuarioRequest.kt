package com.example.proyectoemtaja.models.peticiones

import com.example.proyectoemtaja.models.usuario.Sexo
import com.google.gson.annotations.SerializedName

/**
 * Peticion de actualizar usuario
 */
data class ActualizarUsuarioRequest(

    @SerializedName("clave")
    var clave:String,

    @SerializedName("nuevaClave")
    var nuevaClave: String,

    @SerializedName("nombre")
    var nombre:String,

    @SerializedName("apellidos")
    var apellidos:String,

    @SerializedName("fechaNacimiento")
    var fechaNacimiento:String,

    @SerializedName("sexo")
    var sexo: Sexo

)
