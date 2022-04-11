package com.example.proyectoemtaja

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.proyectoemtaja.databinding.ActivityMapsBinding
import com.example.proyectoemtaja.models.Arrive
import com.example.proyectoemtaja.models.listaParadas.DataListaParadas
import com.example.proyectoemtaja.models.listaParadas.ListaParadas
import com.example.proyectoemtaja.service.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.ArrayList
import java.util.stream.Collectors

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    val lista = ArrayList<Map.Entry<String, List<ListaParadas>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        listarParadas()
    }


    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("http://192.168.1.41:8080/prueba/").addConverterFactory(
            GsonConverterFactory.create()).build()

    }
    private fun listarParadas(){

        CoroutineScope(Dispatchers.IO).launch {

            val call= getRetrofit().create(APIService::class.java).getListaParadas("listar-paradas/")

            if (call.isSuccessful){

                Log.d("Debug", "Entramos a actualizar datos")

                try {
                    val paradas = call.body() //exceptioon

                    var cosa = paradas?.data?.get(0)

                    Log.d("Debug", cosa.toString())

                    /*
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

                     */

                    Log.d("Debug", "Datos actualizados")

                }catch (e: Exception) {
                    Log.e("Error", "Error al actializar datos")
                }

                Log.d("Debug", "RV actualizados")
            }
            else{
                Log.e("Debug","Error al buscar")
            }

        }
    }

}