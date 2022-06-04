package com.example.proyectoemtaja.models.paradasLinea

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

/**
 * Data paradas linea
 */
data class ParadasLinea (
    var code: String,

    var description:String,

    var datetime:String,

    @SerializedName("data")
    var data : ArrayList<DataParadasLinea>
) {
    constructor(l: ParadasLinea) : this(l.code, l.description, l.datetime, l.data)
}

