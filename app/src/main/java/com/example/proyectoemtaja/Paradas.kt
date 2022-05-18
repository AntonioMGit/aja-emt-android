package com.example.proyectoemtaja

import com.example.proyectoemtaja.models.listaParadas.ListaParadas
import com.example.proyectoemtaja.models.peticiones.Favorito
import java.util.ArrayList

class Paradas {

    companion object {
        var listaParadas: ListaParadas? = null
        val listaFavoritos: ArrayList<Favorito> = ArrayList<Favorito>()
    }

}