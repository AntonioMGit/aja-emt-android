package com.example.proyectoemtaja

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoemtaja.databinding.ActivityMapsBinding
import com.example.proyectoemtaja.models.listaParadas.ListaParadas
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.utilities.Variables
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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

        // adding on click listener to marker of google maps.
        mMap.setOnMarkerClickListener { marker -> // on marker click we are getting the title of our marker
            // which is clicked and displaying it in a toast message.
            val titulo = marker.title.toString()

            val cosas : List<String> =  titulo.split("-")
            Toast.makeText(this@MapsActivity, "Número de parada: ${cosas.get(2).toString()}", Toast.LENGTH_SHORT)
                .show()
            var p = cosas.get(2).toString().trim()
            buscarParada(p)

            true //?¿?
        }
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
                        var nParada = cosa?.node.toString()
                        mMap.addMarker(MarkerOptions().position(parada).title(cosa?.name + " - " + nParada))
                        //Log.d("Debug", "snippet:" + nParada.toString())
                        //mMap.addMarker(MarkerOptions().snippet(nParada.toString()))
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

    private fun buscarParada(nParada: String?) {
        val intent = Intent(this,MainActivity::class.java);
        intent.putExtra("nParada", nParada)

        startActivity(intent);
    }
}