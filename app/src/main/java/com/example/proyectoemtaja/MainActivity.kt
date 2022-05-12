package com.example.proyectoemtaja

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.databinding.ActivityMainBinding
import com.example.proyectoemtaja.models.timeArrival.Arrive
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
    private lateinit var btnBuscar: Button
    private lateinit var btnMaps: Button
    private lateinit var etParada: EditText
    private lateinit var rvBuses: RecyclerView
    private lateinit var btnAFav: Button

    val lista = ArrayList<Map.Entry<String, List<Arrive>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // binding.btnBuscarParada=findViewById(R.id.btnBuscarParada)
        btnBuscar = binding.btnBuscarParada
        etParada = binding.etParada//findViewById(R.id.etParada)
        rvBuses = binding.rvBusesParada//findViewById(R.id.rvBusesParada)
        rvBuses.layoutManager = LinearLayoutManager(this)
        rvBuses.adapter = BusParadaAdapter(lista)

        btnMaps = binding.btnMaps
        btnAFav = binding.btnAFav

        var nParada: String? = intent.getStringExtra("nParada")

        if (!nParada.isNullOrEmpty())
            searchParada(nParada.toString())

        btnBuscar.setOnClickListener {
            accionBoton()
        }

        btnMaps.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        btnAFav.setOnClickListener {
            startActivity(Intent(this, FavoritoActivity::class.java))
        }

    }

    private fun accionBoton() {
        val parada = etParada.text.toString().trim()
        //rvBuses.adapter = null
        searchParada(parada = parada)
        etParada.text.clear()
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
                    mapa?.forEach {
                        if (it.value.size > 0) {
                            lista.add(it)
                        }
                    }
                    lista.sortWith(Comparator { entry, entry2 ->
                        entry.value.get(0).estimateArrive - (entry2.value.get(0).estimateArrive)
                    })
                    Log.d("Debug", "Datos actualizados")
                } catch (e: Exception) {
                    Log.e("Error", "Error al actializar datos")
                }
                Log.d("Debug", "RV actualizados")
            } else {
                Log.e("Debug", "Error al buscar")
                Log.e("Debug", call.message())
            }

            runOnUiThread {
                rvBuses.adapter?.notifyDataSetChanged()
            }
        }

    }
}






