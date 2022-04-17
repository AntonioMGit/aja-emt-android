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
import com.example.proyectoemtaja.models.listaParadas.ListaParadas
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.utilities.Variables
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.ArrayList

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

        //datos centro de madrid
        var latitud = 40.4165
        var longitud = -3.70256
        val madrid = LatLng(latitud,longitud)

        mMap.addMarker(MarkerOptions().position(madrid).title("Madrid"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madrid,13f))

        listarParadas()
    }


    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("${Variables.urlBase}/prueba/").addConverterFactory(
            GsonConverterFactory.create()).build()

    }
    private fun listarParadas(){

        CoroutineScope(Dispatchers.Main).launch {

            val call= getRetrofit().create(APIService::class.java).getListaParadas("listar-paradas/")

            if (call.isSuccessful){

                Log.d("Debug", "Entramos a actualizar datos")

                try {
                    val paradas = call.body() //exceptioon

                    var i = 0
                    for (i in 0..9){ //prueba con los 9 primeros
                        var cosa = paradas?.data?.get(i)

                        Log.d("Debug", i.toString() + ": " + cosa.toString())

                        var latitud = cosa?.geometry?.coordinates?.get(1)
                        var longitud = cosa?.geometry?.coordinates?.get(0)

                        val parada = LatLng(latitud!!,longitud!!)
                        mMap.addMarker(MarkerOptions().position(parada).title(cosa?.name))
                    }
                    Log.d("Debug", "Datos actualizados")

                }catch (e: Exception) {
                    Log.e("Error", "Error al actializar datos")
                    Log.e("Error", e.stackTraceToString())
                }
                Log.d("Debug", "RV actualizados")
            }
            else{
                Log.e("Debug","Error al buscar")
            }
        }
    }
}