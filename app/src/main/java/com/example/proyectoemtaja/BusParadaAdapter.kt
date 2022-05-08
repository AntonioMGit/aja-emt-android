package com.example.proyectoemtaja

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoemtaja.models.timeArrival.Arrive
import java.util.stream.IntStream

class BusParadaAdapter(private val datos: ArrayList<Map.Entry<String, List<Arrive>>>) :
    RecyclerView.Adapter<BusParadaAdapter.ViewHolderDatos>() {

    private var listDatos = datos

    //Clase encargada de asignarle los datos al recycleviewer
    class ViewHolderDatos(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tercero = itemView.findViewById(R.id.tvTercerBusParadas) as TextView
        val segundo = itemView.findViewById(R.id.tvSegundoBusParadas) as TextView
        val primero = itemView.findViewById(R.id.tvPrimerBusParadas) as TextView

        val lista = listOf(primero, segundo, tercero)

        val direccionBus = itemView.findViewById<TextView>(R.id.tvDireccionBusParadas)
        val nombreBus = itemView.findViewById<TextView>(R.id.tvNombreBusParadas)

        //Asigna los datos a cada seccion del recycleviewer
        fun asignarDatos(tupla: Map.Entry<String, List<Arrive>>) {
            val nombre = tupla.key
            val values = tupla.value

            nombreBus.text = nombre

            direccionBus.text = values.get(0).destination

            //Ordenacion de tiempos
            //values.stream().sorted { arrive, arrive2 -> arrive.estimateArrive.compareTo(arrive2.estimateArrive) }

            //Una especide de bucle for que está limitado si o si a 3 pero que recorre de 0 al size de la coleccion
            IntStream.range(0, if (values.size > 3) 3 else values.size).forEach {
                lista[it].text = "${((values[it].estimateArrive / 60) as Int)} min"
            }

        }
    }

    override fun onBindViewHolder(holder: ViewHolderDatos, position: Int) {
        holder.asignarDatos(listDatos[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDatos {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_bus_parada, null, false)
        return ViewHolderDatos(view)
    }

    override fun getItemCount(): Int {
        return listDatos.size
    }
}