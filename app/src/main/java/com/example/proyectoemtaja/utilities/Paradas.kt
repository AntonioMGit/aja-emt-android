package com.example.proyectoemtaja.utilities

import com.example.proyectoemtaja.models.listaParadas.ListaParadas
import com.example.proyectoemtaja.models.usuario.Favorito
import java.util.ArrayList

/**
 * Variables que se usan en la aplicacion.
 * Relativo a paradas
 */
class Paradas {

    companion object {
        /**
         * Lista de paradas de la EMT
         */
        var listaParadas: ListaParadas? = null

        /**
         * Lista de favoritos
         */
        val listaFavoritos: ArrayList<Favorito> = ArrayList<Favorito>()
    }

}