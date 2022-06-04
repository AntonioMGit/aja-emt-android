package com.example.proyectoemtaja.recyclerview_adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoemtaja.R
import com.example.proyectoemtaja.models.timeArrival.Arrive
import java.util.stream.IntStream
/**
 * Adaptador de datos para recyclerviewers
 * (Tiene que tener una lista ordenada, por eso creamos un ArrayList)
 * @param datos Datos a pintar
 */
class BusParadaAdapter(

    /**
     * Datos a pintar
     */
    private val datos: ArrayList<Map.Entry<String, List<Arrive>>>

) : RecyclerView.Adapter<BusParadaAdapter.ViewHolderDatos>() {

    /**
     * OnItemClickListener del recyclerview
     */
    private var listener: OnItemClickListener? =  null

    /**
     * Interfaz encargada de recoger el evento de click listener
     */
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    /**
     * Setter on itemClickListener
     */
    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    /**
     *  Clase encargada de asignarle los datos al recycleviewer
     */
    class ViewHolderDatos(itemView: View, listener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.findViewById<CardView>(R.id.cardViewNumBus).setOnClickListener{
                if (listener != null) {
                    listener.onItemClick(adapterPosition)
                }else{
                    Log.e("Error", "No coge el listener")
                }
            }
        }

        val tercero = itemView.findViewById(R.id.tvTercerBusParadas) as TextView
        val segundo = itemView.findViewById(R.id.tvSegundoBusParadas) as TextView
        val primero = itemView.findViewById(R.id.tvPrimerBusParadas) as TextView

        val lista = listOf(primero, segundo, tercero)

        val direccionBus = itemView.findViewById<TextView>(R.id.tvDireccionBusParadas)
        val nombreBus = itemView.findViewById<TextView>(R.id.tvNombreBusParadas)

        //val cvNumBus = itemView.findViewById(R.id.cardViewNumBus) as CardView

        //Asigna los datos a cada seccion del recycleviewer
        fun asignarDatos(tupla: Map.Entry<String, List<Arrive>>) {
            val nombre = tupla.key
            val values = tupla.value

            nombreBus.text = nombre

            direccionBus.text = values.get(0).destination

            //Una especide de bucle for que está limitado si o si a 3 pero que recorre de 0 al size de la coleccion
            IntStream.range(0, if (values.size > 3) 3 else values.size).forEach {
                var tiempo: Int = (values[it].estimateArrive / 60)

                lista[it].text = "${if (tiempo > 90) {"+ 90"} else { tiempo.toString() }} min"
            }
        }
    }


    override fun onBindViewHolder(holder: ViewHolderDatos, position: Int) {
        holder.asignarDatos(datos[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDatos {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_bus_parada, null, false)
        return ViewHolderDatos(view, listener)
    }

    override fun getItemCount(): Int {
        return datos.size
    }
}

