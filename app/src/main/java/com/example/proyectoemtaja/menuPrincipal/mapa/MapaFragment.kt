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
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.utilities.Constantes
import com.example.proyectoemtaja.utilities.UrlServidor
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



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

        //datos centro de madrid
        var latitud = 40.4165
        var longitud = -3.70256
        val madrid = LatLng(latitud, longitud)

        mMap.addMarker(MarkerOptions().position(madrid).title("Madrid"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(madrid, 13f))

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
        try {
            if (Paradas.listaParadas != null) {
                var paradas: ListaParadas = ListaParadas(Paradas.listaParadas!!)

                for (i in 0..9) { //prueba con los 9 primeros
                    var cosa = paradas.data.get(i)

                    Log.d("Debug", i.toString() + ": " + cosa.toString())

                    var latitud = cosa.geometry.coordinates.get(1)
                    var longitud = cosa.geometry.coordinates.get(0)

                    val parada = LatLng(latitud, longitud)
                    var nParada = cosa.node.toString()
                    mMap.addMarker(
                        MarkerOptions().position(parada).title(cosa.name + " - " + nParada)
                            .snippet("Pulsa para ver información.")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.mini_pin_bus_azul_claro))
                    )
                }
            }else{
                Log.e("Error", "No hay lista de paradas")
                //cargarla?
            }
        } catch (e: Exception) {
            Log.e("Error", "Error cargar datos en el mapa")
            Log.e("Error", e.stackTraceToString())
        }
        Log.d("Debug", "RV actualizados")
    }

    /*
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(UrlServidor.URL_BASE)
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()

    }


    private fun listarParadas() {

        CoroutineScope(Dispatchers.Main).launch {
            val sharedPreferences = requireActivity().getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            val call = getRetrofit().create(APIService::class.java).getListaParadas(
                "Bearer " + sharedPreferences.getString("accessToken", "").toString()
            )

            if (call.isSuccessful) {

                try {
                    val paradas = call.body() //exceptioon

                    for (i in 0..9) { //prueba con los 9 primeros
                        var cosa = paradas?.data?.get(i)

                        Log.d("Debug", i.toString() + ": " + cosa.toString())

                        var latitud = cosa?.geometry?.coordinates?.get(1)
                        var longitud = cosa?.geometry?.coordinates?.get(0)

                        val parada = LatLng(latitud!!, longitud!!)
                        var nParada = cosa?.node.toString()
                        mMap.addMarker(
                            MarkerOptions().position(parada).title(cosa?.name + " - " + nParada)
                                .snippet("Pulsa para ver información.")
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.mini_pin_bus_azul_claro))
                        )

                        //Log.d("Debug", "snippet:" + nParada.toString())
                        //mMap.addMarker(MarkerOptions().snippet(nParada.toString()))
                    }
                    Log.d("Debug", "Datos actualizados")

                } catch (e: Exception) {
                    Log.e("Error", "Error al actializar datos")
                    Log.e("Error", e.stackTraceToString())
                }
                Log.d("Debug", "RV actualizados")
            } else {
                Log.e("Debug", "Error al buscar")
            }
        }
    }*/

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