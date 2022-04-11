package com.example.proyectoemtaja

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.databinding.ActivityMainBinding
import com.example.proyectoemtaja.models.Arrive
import com.example.proyectoemtaja.models.TimeArrivalBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.*
import java.util.stream.Collectors
import kotlin.Comparator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var btnBuscar: Button
    private lateinit var etParada: EditText
    private lateinit var rvBuses: RecyclerView

    private lateinit var btnMaps: Button

    val lista = ArrayList<Map.Entry<String, List<Arrive>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnBuscar = findViewById(R.id.btnBuscarParada)
        etParada = findViewById(R.id.etParada)
        rvBuses = findViewById(R.id.rvBusesParada)
        rvBuses.layoutManager = LinearLayoutManager(this)
        rvBuses.adapter = BusParadaAdapter(lista)

        btnMaps = findViewById(R.id.btnMaps)

        btnBuscar.setOnClickListener{
            accionBoton()
        }
        btnMaps.setOnClickListener{
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    private fun accionBoton() {
        val parada = etParada.text.toString().trim()
        //rvBuses.adapter = null
        searchParada(parada = parada)
        etParada.text.clear()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("http://192.168.1.41:8080/prueba/").addConverterFactory(
            GsonConverterFactory.create()).build()

    }

    private fun searchParada(parada:String){

        CoroutineScope(Dispatchers.IO).launch {

            val call= getRetrofit().create(APIService::class.java).getTimeArrivalBus("consultar/$parada/")

            if (call.isSuccessful){

                Log.d("Debug", "Entramos a actualizar datos")

                try {
                    val timeArrivalBus = call.body() //exceptioon
                    var mapa = timeArrivalBus?.data?.get(0)?.arrive?.stream()?.collect(Collectors.groupingBy { it.line })

                    lista.clear()

                    mapa?.forEach {
                        if (it.value.size > 0) {
                            lista.add(it)
                        }
                    }

                    lista.sortWith(Comparator { entry, entry2 ->
                        entry.value.get(0).estimateArrive - (entry2.value.get(0).estimateArrive)
                    })

                    Log.d("Debug", "Datos actualizados")

                }catch (e: Exception) {
                    Log.e("Error", "Error al actializar datos")
                }

                Log.d("Debug", "RV actualizados")
            }
            else{
                Log.e("Debug","Error al buscar")
            }

            runOnUiThread {
                rvBuses.adapter?.notifyDataSetChanged()
            }
        }

    }
}