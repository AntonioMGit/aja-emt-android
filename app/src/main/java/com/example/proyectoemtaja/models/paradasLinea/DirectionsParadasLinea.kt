package com.example.proyectoemtaja.models.paradasLinea

/**
 * Data paradas linea
 */
data class DirectionsParadasLinea (
    var Direction1: Direction,
    var Direction2: Direction,
    var DayType: String
){
    constructor(l: DirectionsParadasLinea) : this(l.Direction1, l.Direction2, l.DayType)

}