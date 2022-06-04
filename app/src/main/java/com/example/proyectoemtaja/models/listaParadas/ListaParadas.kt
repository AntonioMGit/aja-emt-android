package com.example.proyectoemtaja.models.listaParadas

import com.google.gson.annotations.SerializedName
import java.util.ArrayList


/**
 * Datos lista paradas
 */
data class ListaParadas (
    var code: String,

    var description:String,

    var datetime:String,

    @SerializedName("data")
    var data : ArrayList<DataListaParadas>
) {
    constructor(l: ListaParadas) : this(l.code, l.description, l.datetime, l.data)
}