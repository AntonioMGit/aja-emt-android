package com.example.proyectoemtaja.models.usuario

/**
 * Sexos posibles del usuario
 */
enum class Sexo {
    HOMBRE, MUJER, NO_ESPECIFICADO;

    companion object {
        val STRING_HOMRBE = "Hombre"
        val STRING_MUJER = "Mujer"
        val STRING_NO_ESPECIFICADO = "No especificado"

        /**
         * Devielve una lista con todos los sexos
         * @return lista de sexos
         */
        fun getSexoLista(): List<String> {
            return listOf<String>(STRING_HOMRBE, STRING_MUJER, STRING_NO_ESPECIFICADO)
        }

        /**
         * Saca el sexo en base a un string
         * @param sexo string
         * @return sexo transformado
         */
        fun sacarSexo(sexo: String): Sexo {
            return when (sexo) {
                STRING_HOMRBE -> HOMBRE
                STRING_MUJER -> MUJER
                STRING_NO_ESPECIFICADO -> NO_ESPECIFICADO
                else -> HOMBRE
            }
        }

        /**
         * Convierte un sexo a string
         * @param sexo sexo a transformar
         * @return String con el sexo transformado
         */
        fun fromSexo(sexo: Sexo): String {
            return when (sexo) {
                HOMBRE  -> STRING_HOMRBE
                MUJER -> STRING_MUJER
                NO_ESPECIFICADO -> STRING_NO_ESPECIFICADO
            }
        }
    }



}