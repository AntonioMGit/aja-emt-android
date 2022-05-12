package com.example.proyectoemtaja.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoemtaja.BusParadaAdapter
import com.example.proyectoemtaja.FavoritoAdapter
import com.example.proyectoemtaja.MainActivity
import com.example.proyectoemtaja.databinding.FragmentFavoritosBinding
import com.example.proyectoemtaja.models.peticiones.FavoritoResponse
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.utilities.Constantes
import com.example.proyectoemtaja.utilities.UrlServidor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.ArrayList

class FavoritoFragment : Fragment() {

    private var _binding: FragmentFavoritosBinding? = null
    private val favoritos = ArrayList<FavoritoResponse>()
    private lateinit var rvFavoritos: RecyclerView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavoritosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        favoritos.clear()

        rvFavoritos = binding.rvFavoritos
        rvFavoritos.layoutManager = LinearLayoutManager(requireContext())
        rvFavoritos.adapter = FavoritoAdapter(favoritos)

        buscarFavoritos()

        binding.btnBuscarParada.setOnClickListener {
            //mostrarFragmentBuscar()
            Intent(requireContext(), MainActivity::class.java)
        }

        return root
    }

    fun mostrarFragmentBuscar () {
        //TODO("Poner un fragment para mostrar un buscador de paradas o algo, no se")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(UrlServidor.URL_BASE)
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()

    }


    private fun buscarFavoritos() {
        CoroutineScope(Dispatchers.IO).launch {
            val sharedPreferences = requireActivity().getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)

            val call = getRetrofit().create(APIService::class.java).getFavoritos(
                token = "Bearer " + sharedPreferences.getString(Constantes.ACCESS_TOKEN_SHARED_PREFERENCES, "").toString(),
                idUsuario = sharedPreferences.getString(Constantes.EMAIL_SHARED_PREFERENCES, "").toString()
            )

            if (call.isSuccessful) {
                Log.d("Debug", "Entramos a buscar favoritos")
                try {
                    val favs = call.body()!!
                    favoritos.addAll(favs)

                    Log.d("Debug", "Favoritos actualizados")
                } catch (e: Exception) {
                    Log.e("Error", "Error al actializar datos")
                }
                Log.d("Debug", "RV actualizados")
            } else {
                Log.e("Debug", "Error al buscar")
                Log.e("Debug", call.toString())
            }

            requireActivity().runOnUiThread {
                rvFavoritos.adapter?.notifyDataSetChanged()
            }
        }

    }
}