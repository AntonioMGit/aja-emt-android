package com.example.proyectoemtaja.models.usuario

enum class Sexo {
    HOMBRE, MUJER, NO_ESPECIFICADO;

    companion object {
        val STRING_HOMRBE = "Hombre"
        val STRING_MUJER = "Mujer"
        val STRING_NO_ESPECIFICADO = "No especificado"

        fun sacarSexo(sexo: String): Sexo {
            return when (sexo) {
                STRING_HOMRBE -> HOMBRE
                STRING_MUJER -> MUJER
                STRING_NO_ESPECIFICADO -> NO_ESPECIFICADO
                else -> HOMBRE
            }
        }

        fun fromSexo(sexo: Sexo): String {
            return when (sexo) {
                HOMBRE  -> STRING_HOMRBE
                MUJER -> STRING_MUJER
                NO_ESPECIFICADO -> STRING_NO_ESPECIFICADO
            }
        }
    }



}