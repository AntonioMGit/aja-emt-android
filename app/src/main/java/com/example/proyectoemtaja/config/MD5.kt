package com.example.proyectoemtaja.config

import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


object MD5 {
    /**
     * Encripta la password
     * @param input password
     * @return password encriptada
     */
    fun encriptar(input: String): String? {
        var hashtext: String? = null
        try {
            val md = MessageDigest.getInstance("MD5")
            val messageDigest = md.digest(input.toByteArray())
            val number = BigInteger(1, messageDigest)
            hashtext = number.toString(16)
            while (hashtext!!.length < 32) {
                hashtext = "0$hashtext"
            }
        } catch (e: NoSuchAlgorithmException) {
            System.err.println("Error en el encriptado de la password")
        }
        return hashtext
    }
}
