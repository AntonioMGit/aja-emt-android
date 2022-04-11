package com.example.proyectoemtaja.models.listaParadas

import com.example.proyectoemtaja.models.Data
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

data class ListaParadas (
    var code:String,

    var description:String,

    var datetime:String,

    @SerializedName("data")
    var data : ArrayList<DataListaParadas>
    ) {
}