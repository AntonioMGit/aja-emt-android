package com.example.proyectoemtaja

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoemtaja.models.peticiones.FavoritoResponse
import com.example.proyectoemtaja.models.timeArrival.Arrive
import java.util.stream.IntStream


class FavoritoAdapter(private val datos: ArrayList<FavoritoResponse>) : RecyclerView.Adapter<FavoritoAdapter.ViewHolderDatos>() {

    private var listDatos = datos

    //Clase encargada de asignarle los datos al recycleviewer
    class ViewHolderDatos(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val idParada = itemView.findViewById<TextView>(R.id.tvFavoritoCodigoParada)
        val nombreParada = itemView.findViewById<TextView>(R.id.tvFavoritoNombreParada)

        //Asigna los datos a cada seccion del recycleviewer
        fun asignarDatos(favorito: FavoritoResponse) {
            idParada.text = favorito.idParada
            nombreParada.text = favorito.nombreParada
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDatos {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorito, null, false)
        return ViewHolderDatos(view)
    }

    override fun onBindViewHolder(holder: ViewHolderDatos, position: Int) {
        holder.asignarDatos(listDatos[position])
    }

    override fun getItemCount(): Int {
        return listDatos.size
    }
}