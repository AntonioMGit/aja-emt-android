package com.example.proyectoemtaja.utilities

import java.time.format.DateTimeFormatter

/**
 * Constantes utilizadas en la app
 */
class Constantes {
    companion object {

        /**
         * Nombre del fichero de las shared preferences
         */
        const val NOMBRE_FICHERO_SHARED_PREFERENCES: String = "sharedPrefs"

        /**
         * nombre del campo email en las shared preferences
         */
        const val EMAIL_SHARED_PREFERENCES: String = "email"

        /**
         * nombre del campo password en las shared preferences
         */
        const val PASSWORD_SHARED_PREFERENCES: String = "pass"

        /**
         * nombre del campo accessToken en las shared preferences
         */
        const val ACCESS_TOKEN_SHARED_PREFERENCES: String = "accessToken"

        /**
         * Formato de fecha a mostrar al usuario
         */
        val FORMATO_FECHA_MOSTRAR: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        /**
         * Formato de fecha a enviar al servidor
         */
        val FORMATO_FECHA_ENVIAR:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        /**
         * Expresion regular de correo: xxxxxx@xxxx.xxxx
         */
        val regexCorreo: Regex = Regex("^[^@]+@[^@]+\\.[a-zA-Z]{2,}$")

        /**
         * Expresion regular de clave
         * Entre 8 y 20 caracteres, una mayuscula, una minuscula, un numero y un caracter especial
         */
        val regexClave: Regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$")

    }
}