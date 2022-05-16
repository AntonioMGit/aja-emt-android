package com.example.proyectoemtaja

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoemtaja.models.paradasLinea.StopsParadasLinea

class ParadasLineaAdapter (private val datos: ArrayList<StopsParadasLinea>) : RecyclerView.Adapter<ParadasLineaAdapter.ViewHolderDatos>() {

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    private var listDatos = datos

    class ViewHolderDatos(itemView: View, listener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {

        //numParada
        val nombrePada = itemView.findViewById<TextView>(R.id.tvNombreBusParadas2)
        val direccionBus = itemView.findViewById<TextView>(R.id.tvDireccionLineasParadas)
        var busesQuePasan = itemView.findViewById<TextView>(R.id.tvLineasParda)

        //val idParada = itemView.findViewById<TextView>(R.id.tvBuscarLineaId)
        //val nombreParada = itemView.findViewById<TextView>(R.id.tvFavoritoNombreParada)

        init {
            itemView.setOnClickListener{
                if (listener != null) {
                    listener.onItemClick(adapterPosition)
                }else{
                    Log.e("Error", "No coge el listener")
                }
            }
        }

        //Asigna los datos a cada seccion del recycleviewer
        fun asignarDatos(paradas: StopsParadasLinea) {
            nombrePada.text = paradas.stop
            direccionBus.text = paradas.name

            var listaBusesQuePasan = ""
            paradas.dataLine.forEach{
                //TODO: poner de otra forma?
                //lista de lineas de bus que pasan por una parada
                listaBusesQuePasan +=  it.toString() + " | "
            }
            busesQuePasan.text = listaBusesQuePasan
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParadasLineaAdapter.ViewHolderDatos {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_linea_parada, null, false)
        return ViewHolderDatos(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolderDatos, position: Int) {
        holder.asignarDatos(listDatos[position])
    }

    override fun getItemCount(): Int {
        return listDatos.size
    }
}