package com.example.proyectoemtaja.utilities

import java.time.format.DateTimeFormatter

class Constantes {
    companion object {

        const val NOMBRE_FICHERO_SHARED_PREFERENCES: String = "sharedPrefs"

        const val EMAIL_SHARED_PREFERENCES: String = "email"

        const val PASSWORD_SHARED_PREFERENCES: String = "pass"

        const val ACCESS_TOKEN_SHARED_PREFERENCES: String = "accessToken"

        val FORMATO_FECHA:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")


    }
}