package com.example.proyectoemtaja.models.paradasLinea

/**
 * Data paradas linea
 */
data class Direction (
    var StartTime: String,
    var StopTime: String,
    var MinimumFrequency: String,
    var MaximunFrequency: String,
    var Frequency: String
){
    constructor(l: Direction) : this(l.StartTime, l.StopTime, l.MinimumFrequency, l.MaximunFrequency, l.Frequency)

}