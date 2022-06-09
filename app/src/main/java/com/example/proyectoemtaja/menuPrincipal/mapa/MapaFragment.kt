package com.example.proyectoemtaja.menuPrincipal.mapa

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.proyectoemtaja.MainActivity
import com.example.proyectoemtaja.utilities.Paradas
import com.example.proyectoemtaja.R
import com.example.proyectoemtaja.databinding.FragmentMapaBinding
import com.example.proyectoemtaja.models.listaParadas.ListaParadas
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

class MapaFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,GoogleMap.OnMyLocationClickListener {

    /**
     * Google map
     */
    private lateinit var mMap: GoogleMap

    /**
     * Contexto de la aplicacipn
     */
    private lateinit var appContext: Context

    /**
     * Permisos de localizacion
     */
    private val REQUEST_LOCATION_PERMISSION = 1

    /**
     * Binding de la interfaz
     */
    private var _binding: FragmentMapaBinding? = null
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

        mMap.setMinZoomPreference(15f)
        mMap.setMaxZoomPreference(17f)

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

        try {
            mMap.setOnMyLocationButtonClickListener(this)
            mMap.setOnMyLocationClickListener(this)

            enableMyLocation()

            Log.d("Error", "Antes de localizacion")
            var location = getMyLocation()
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location!!.latitude, location.longitude), 17f))


            Log.d("Error", "Despues de localizacion")
        }
        catch (e: Exception) {
            Log.d("error", "Antes de mensaje")
            e.message?.let { Log.e("Error", it) }
            Log.d("error", "despues de mesage")
            //Log.e("Error", "Algo salio mal")
        }


    }

    /**
     * Coge la localización del usuario
     */
    @SuppressLint("MissingPermission")
    private fun getMyLocation(): Location? {

        enableMyLocation()

        if(isPermissionGranted()) {
            val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var myLocation: Location? = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (myLocation == null) {
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_COARSE
                val provider = lm.getBestProvider(criteria, true)
                myLocation = lm.getLastKnownLocation(provider!!)
            }
            return myLocation
        }else {
            //si no se dan permisos se pone la camara en madrid centro
            var loc = Location("")
            loc.latitude = 40.4165
            loc.longitude = -3.70256

            return loc
        }
    }

    /**
     * Busca las paradas en el servidor y las pinta en el mapa.
     * Esta dividido en dos corrutinas, una crea marcadores y otra los pinta.
     * Al hacer esto, triplica su velocidad, pues pasa de tardar 4 segundos en cargar el mapa a
     * cargarlo en 0.8 - 1.3 segundos.
     */
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
                }
            } catch (e: Exception) {
                Log.e("Error", "Error cargar datos en el mapa")
                Log.e("Error", e.stackTraceToString())
            }
        }
    }

    /**
     * Pone las paradas en el mapa
     * @param marcadores Lista de marcadores a poner en el mapa
     */
    private fun pintarParadas(marcadores: ArrayList<MarkerOptions>) {
        CoroutineScope(Dispatchers.Main).launch {
            marcadores.forEach {
                mMap.addMarker(it)
            }
        }
    }

    /**
     * Inicia la MainActivity
     * @param nParada Numero de parada a buscar.
     */
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

    /**
     * Mira si los permisos de localizacion estan activados.
     * @return {@true} si tiene permisos
     *         {@false} si no tiene permisos
     */
    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Pide permisos de localizacion
     */
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //enableMyLocation()
                mMap.isMyLocationEnabled=true
            }
            mMap.isMyLocationEnabled = true

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(appContext,"LLevando a mi localizacion", Toast.LENGTH_LONG).show()

        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(appContext,"Estas en ${p0.latitude} y ${p0.longitude} ", Toast.LENGTH_LONG).show()
    }
}