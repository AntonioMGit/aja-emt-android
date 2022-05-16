package com.example.proyectoemtaja.utilities

import java.time.format.DateTimeFormatter

class Constantes {
    companion object {

        const val NOMBRE_FICHERO_SHARED_PREFERENCES: String = "sharedPrefs"

        const val EMAIL_SHARED_PREFERENCES: String = "email"

        const val PASSWORD_SHARED_PREFERENCES: String = "pass"

        const val ACCESS_TOKEN_SHARED_PREFERENCES: String = "accessToken"

        val FORMATO_FECHA_MOSTRAR: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val FORMATO_FECHA_ENVIAR:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        //xxxxxx@xxxx.xxxx
        val regexCorreo: Regex = Regex("^[^@]+@[^@]+\\.[a-zA-Z]{2,}$")

        //Entre 8 y 20 caracteres, una mayuscula, una minuscula, un numero y un caracter especial
        val regexClave: Regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$")

    }
}