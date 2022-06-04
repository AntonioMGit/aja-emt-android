package com.example.proyectoemtaja.models.timeArrival

import java.util.ArrayList

/**
 * Datos a recoger para ver los tiempos de los autobuses.
 */
data class Point(
    var type: String,
    var coordinates: ArrayList<Double>

) {
}