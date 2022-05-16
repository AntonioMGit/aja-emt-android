package com.example.proyectoemtaja

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoemtaja.databinding.ActivityListaParadasLineaBinding
import com.example.proyectoemtaja.databinding.ActivityMainBinding
import com.example.proyectoemtaja.models.paradasLinea.StopsParadasLinea
import com.example.proyectoemtaja.models.timeArrival.Arrive
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.utilities.Constantes
import com.example.proyectoemtaja.utilities.UrlServidor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.stream.Collectors

class ListaParadasLinea : AppCompatActivity() {

    private lateinit var binding: ActivityListaParadasLineaBinding
    private lateinit var rvListaParadasLinea: RecyclerView
    private lateinit var tvBuscarLineaId: TextView
    private lateinit var tvBuscarLineaDir: TextView

    val lista = ArrayList<StopsParadasLinea>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_paradas_linea)
        binding = ActivityListaParadasLineaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var nLinea: String = intent.getStringExtra("nLinea")!!
        var dirLetra: String = intent.getStringExtra("dir")!!

        var dir = "1"
        if(dirLetra.equals("B"))
            dir = "2"

        Toast.makeText(this, "ha llegado el numero: " + nLinea + " Dir: " + dir , Toast.LENGTH_SHORT).show()

        var adapter = ParadasLineaAdapter(lista)

        adapter.setOnItemClickListener(object : ParadasLineaAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                buscarParada(position)
            }
        })

        rvListaParadasLinea = binding.rvListaParadasLinea
        rvListaParadasLinea.layoutManager = LinearLayoutManager(this)
        rvListaParadasLinea.adapter = adapter


        tvBuscarLineaId = binding.tvBuscarLineaId
        tvBuscarLineaDir = binding.tvBuscarLineaDir

        searchLinea(nLinea.toString(), dir.toString())
    }

    fun buscarParada(pos: Int){
        var nParada = lista.get(pos).stop

        Toast.makeText(this@ListaParadasLinea, nParada.toString(), Toast.LENGTH_SHORT).show()

        var intent = Intent(this, MainActivity::class.java)
        intent.putExtra("nParada", nParada.toString())
        startActivity(intent)
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(UrlServidor.URL_BASE)
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()
    }

    private fun searchLinea(nLinea: String, dir: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)

            val call = getRetrofit().create(APIService::class.java).getParadasLinea(
                url = UrlServidor.urlParadasLinea(nLinea, dir),
                token = "Bearer " + sharedPreferences.getString(Constantes.ACCESS_TOKEN_SHARED_PREFERENCES, "").toString(),
                idUsuario = sharedPreferences.getString(Constantes.EMAIL_SHARED_PREFERENCES, "").toString()
            )

            if (call.isSuccessful) {
                Log.d("Debug", "Entramos a buscar paradas de linea " + nLinea + " Dir: " + dir)

                try {
                    val todoParadasLinea = call.body() //excetcion

                    val stopsLinea = todoParadasLinea?.data?.get(0)?.stops

                    lista.clear()

                    stopsLinea?.forEach {
                        lista.add(it)
                    }

                    //Se tiene que cambiar
                    runOnUiThread {
                        tvBuscarLineaId.text = "Paradas de la línea " + nLinea
                        tvBuscarLineaDir.text = "De: " + lista.get(0).name + " A: " + lista.get(lista.size-1).name
                    }

                    Log.d("Debug", "Lista paradas cargada")

                } catch (e: Exception) {
                    Log.e("Error", "Error al actializar datos")
                }
                Log.d("Debug", "RV actualizados")
            } else {
                Log.e("Debug", "Error al buscar paradas de una linea")
                Toast.makeText(this@ListaParadasLinea, "No se han podido buscar las paradas.", Toast.LENGTH_SHORT).show()
                Log.e("Debug", call.message())
            }

            runOnUiThread {
                rvListaParadasLinea.adapter?.notifyDataSetChanged()
            }
        }
    }

}