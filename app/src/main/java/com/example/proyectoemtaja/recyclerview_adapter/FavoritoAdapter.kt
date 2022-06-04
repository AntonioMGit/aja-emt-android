package com.example.proyectoemtaja.recyclerview_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoemtaja.R
import com.example.proyectoemtaja.models.usuario.Favorito


class FavoritoAdapter(private val datos: ArrayList<Favorito>) : RecyclerView.Adapter<FavoritoAdapter.ViewHolderDatos>() {

    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    private var listDatos = datos

    //Clase encargada de asignarle los datos al recycleviewer
    class ViewHolderDatos(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val idParada = itemView.findViewById<TextView>(R.id.tvFavoritoCodigoParada)
        val nombreParada = itemView.findViewById<TextView>(R.id.tvFavoritoNombreParada)

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
        //Asigna los datos a cada seccion del recycleviewer
        fun asignarDatos(favorito: Favorito) {
            idParada.text = favorito.idParada
            nombreParada.text = favorito.nombreParada
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDatos {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorito, null, false)
        return ViewHolderDatos(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolderDatos, position: Int) {
        holder.asignarDatos(listDatos[position])
    }

    override fun getItemCount(): Int {
        return listDatos.size
    }
}