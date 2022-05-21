package com.example.proyectoemtaja.menuPrincipal.mapa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.proyectoemtaja.R
import com.example.proyectoemtaja.databinding.FragmentMapaBinding
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.proyectoemtaja.MainActivity
import com.example.proyectoemtaja.Paradas
import com.example.proyectoemtaja.models.listaParadas.ListaParadas
import com.example.proyectoemtaja.utilities.UrlServidor
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.ArrayList

class MapaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    val lista = ArrayList<Map.Entry<String, List<ListaParadas>>>()

    private var _binding: FragmentMapaBinding? = null

    private lateinit var appContext: Context

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        appContext = requireContext().applicationContext

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(appContext, R.raw.style_json));

        mMap = googleMap

        //TODO: poner aqui los datos del usuario
        var latitud = 40.4165
        var longitud = -3.70256
        val madrid = LatLng(latitud, longitud)

        mMap.addMarker(MarkerOptions().position(madrid).title("Madrid"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madrid, 17f))

        mMap.setMinZoomPreference(15f)
        mMap.setMaxZoomPreference(17f)

        //mMap.maxZoomLevel(20f)

        listarParadas()

        // adding on click listener to marker of google maps.
        mMap.setOnInfoWindowClickListener { marker -> // on marker click we are getting the title of our marker
            // which is clicked and displaying it in a toast message.
            val titulo = marker.title.toString()

            val cosas: List<String> = titulo.split(" - ")
            Toast.makeText(
                context,
                "Número de parada: ${cosas.get(1).toString()}",
                Toast.LENGTH_SHORT
            )
                .show()
            var p = cosas.get(1).toString().trim()
            buscarParada(p)

        }

        //mMap.setOnCameraMoveListener { marker ->
        //    marker.isVisible = mMap.cameraPosition.zoom > 19
        //}
    }

    private fun listarParadas() {
        CoroutineScope(Dispatchers.Main).launch {

            try {
                if (Paradas.listaParadas != null) {
                    val marcadores = ArrayList<MarkerOptions>()
                    Paradas.listaParadas!!.data.stream().forEach {
                        marcadores.add(
                            MarkerOptions().position(
                                LatLng(
                                    it.geometry.coordinates.get(1),
                                    it.geometry.coordinates.get(0)
                                )
                                ).title(it.name + " - " + it.node.toString())
                                    .snippet("Pulsa para ver información.")
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.mini_pin_bus_azul_claro))
                        )
                    }

                    pintarParadas(marcadores)
                }
                else{
                    Log.e("Error", "No hay lista de paradas")
                    //cargarla?
                }
            } catch (e: Exception) {
                Log.e("Error", "Error cargar datos en el mapa")
                Log.e("Error", e.stackTraceToString())
            }
        }
    }

    private fun pintarParadas(marcadores: ArrayList<MarkerOptions>) {
        CoroutineScope(Dispatchers.Main).launch {
            marcadores.forEach {
                mMap.addMarker(it)
            }
        }
    }


    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(UrlServidor.URL_BASE)
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()

    }

    private fun buscarParada(nParada: String?) {
        val intent = Intent(requireContext(), MainActivity::class.java);
        intent.putExtra("nParada", nParada)

        startActivity(intent);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}