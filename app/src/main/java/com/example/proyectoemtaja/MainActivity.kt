package com.example.proyectoemtaja

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.databinding.ActivityMainBinding
import com.example.proyectoemtaja.models.timeArrival.Arrive
import com.example.proyectoemtaja.models.timeArrival.Line
import com.example.proyectoemtaja.utilities.Constantes
import com.example.proyectoemtaja.utilities.UrlServidor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.stream.Collectors
import kotlin.Comparator
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var rvBuses: RecyclerView
    private lateinit var tvNombre: TextView
    private lateinit var tvIdParada: TextView
    private lateinit var imgBus: ImageView

    val lista = ArrayList<Map.Entry<String, List<Arrive>>>()
    val listaDirecciones = ArrayList<String>()
    val listaCodigosLineas = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvBuses = binding.rvBusesParada
        rvBuses.layoutManager = LinearLayoutManager(this)

        var adapter = BusParadaAdapter(lista)

        adapter.setOnItemClickListener(object : BusParadaAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                buscarLinea(position)
            }
        })

        rvBuses.adapter = adapter

        tvIdParada = binding.tvBuscarParadaId
        tvNombre = binding.tvBuscarParadaNombre
        imgBus = binding.imgFotoBus


        var nParada: String = intent.getStringExtra("nParada")!!

        searchParada(nParada.toString())

    }

    fun buscarLinea(pos: Int) {
        var lin = lista[pos]
        var dir = listaDirecciones[pos]
        var codLineas = listaCodigosLineas[pos]

        Toast.makeText(this@MainActivity, codLineas, Toast.LENGTH_SHORT).show()

        var intent = Intent(this, ListaParadasLinea::class.java)
        intent.putExtra("nLinea", lin.key.toString())
        intent.putExtra("codLinea", codLineas)
        intent.putExtra("dir", dir)
        startActivity(intent)

    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(UrlServidor.URL_BASE)
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()
    }

    private fun searchParada(parada: String) {

        CoroutineScope(Dispatchers.IO).launch {
            val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)

            val call = getRetrofit().create(APIService::class.java).getTimeArrivalBus(
                url = UrlServidor.urlTiempoAutobus(parada),
                token = "Bearer " + sharedPreferences.getString(Constantes.ACCESS_TOKEN_SHARED_PREFERENCES, "").toString(),
                idUsuario = sharedPreferences.getString(Constantes.EMAIL_SHARED_PREFERENCES, "").toString()
            )

            if (call.isSuccessful) {
                Log.d("Debug", "Entramos a actualizar datos")
                try {
                    val timeArrivalBus = call.body() //exceptioon
                    var mapa = timeArrivalBus?.data?.get(0)?.arrive?.stream()?.collect(Collectors.groupingBy { it.line })
                    lista.clear()
                    var numParada = ""
                    var i = 0
                    var stopLines = timeArrivalBus?.data?.get(0)?.stopInfo?.get(0)?.stopLines?.data
                    mapa?.forEach {
                        if (it.value.size > 0) {
                            lista.add(it)
                            //para coger las direcciones a la que va (A o B)
                            listaDirecciones.add(stopLines?.get(i)?.to.toString())

                            //algunos buses como el E3 su codigo y el numero que se muestra es distinto
                            //E3 por ejemplo es 403
                            numParada = stopLines?.get(i)?.label.toString()
                            listaCodigosLineas.add(buscarCodigoParada(numParada, stopLines))
                            i++
                        }
                    }
                    lista.sortWith(Comparator { entry, entry2 ->
                        entry.value.get(0).estimateArrive - (entry2.value.get(0).estimateArrive)
                    })

                    //FIXME: Se tiene que cambiar
                    runOnUiThread {
                        tvIdParada.text = "Parada de buses EMT ${timeArrivalBus!!.data[0].stopInfo[0].label}"
                        tvNombre.text = timeArrivalBus.data[0].stopInfo[0].description

                        if (timeArrivalBus.data[0].incident.listaIncident.data.size > 0) {
                            imgBus.setImageResource(R.drawable.ic_baseline_bus_alert_24)
                            imgBus.setOnClickListener{
                                Toast.makeText(this@MainActivity, "Eu", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    Log.d("Debug", "Datos actualizados")
                } catch (e: Exception) {
                    Log.e("Error", "Error al actializar datos")
                }
                Log.d("Debug", "RV actualizados")
            } else {
                Log.e("Debug", "Error al buscar parada")
                //Toast.makeText(this@MainActivity, "No se ha podido buscar la parada.", Toast.LENGTH_SHORT).show()
                Log.e("Debug", call.message())
            }

            runOnUiThread {
                rvBuses.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun buscarCodigoParada(numParada: String, stopLines: ArrayList<Line>?): String {
        var cod = ""

        stopLines?.forEach {
            if(it.label.equals(numParada)){
                cod = it.line.toString()
            }
        }

        return cod
    }
}






