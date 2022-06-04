package com.example.proyectoemtaja

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoemtaja.databinding.ActivityListaParadasLineaBinding
import com.example.proyectoemtaja.models.paradasLinea.StopsParadasLinea
import com.example.proyectoemtaja.recyclerview_adapter.ParadasLineaAdapter
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.utilities.Constantes
import com.example.proyectoemtaja.utilities.ConversorCodigoEMT
import com.example.proyectoemtaja.utilities.UrlServidor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

/**
 * Actividad de listado de paradas de una linea
 */
class ListaParadasLineaActivity : AppCompatActivity() {

    /**
     * Bindng de la interfaz
     */
    private lateinit var binding: ActivityListaParadasLineaBinding

    /**
     * RecyclerView de lista de paradas
     */
    private lateinit var rvListaParadasLinea: RecyclerView

    /**
     * Campos
     */
    private lateinit var tvBuscarLineaId: TextView
    private lateinit var tvBuscarLineaDir: TextView

    /**
     * View a mostrar en caso de error
     */
    private lateinit var imgError: View

    /**
     * Lista de paradas de la linea
     */
    val lista = ArrayList<StopsParadasLinea>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_paradas_linea)
        binding = ActivityListaParadasLineaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var nLinea: String = intent.getStringExtra("nLinea")!!
        var dirLetra: String = intent.getStringExtra("dir")!!

        var dir = if (dirLetra.equals("B"))  "1" else  "2"

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
        imgError = binding.imgError

        searchLinea(nLinea.toString(), dir.toString())
    }

    /**
     * Accion del boton del recyclerview
     * @param pos Posicion en la lista
     */
    fun buscarParada(pos: Int){
        var nParada = lista.get(pos).stop

        Toast.makeText(this@ListaParadasLineaActivity, nParada.toString(), Toast.LENGTH_SHORT).show()

        var intent = Intent(this, MainActivity::class.java)
        intent.putExtra("nParada", nParada.toString())
        startActivity(intent)
    }

    /**
     * Llama al servidor para coger informacion de la lista de lineas
     * @param nLinea numero de lineas
     * @param dir direccion del bus
     */
    private fun searchLinea(nLinea: String, dir: String) {
        CoroutineScope(Dispatchers.IO).launch {
            var runnable: Runnable = try {
                val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                val call = UrlServidor.getRetrofit().create(APIService::class.java).getParadasLinea(
                    url = UrlServidor.urlParadasLinea(ConversorCodigoEMT.pasarANumeros(nLinea), dir),
                    token = "Bearer " + sharedPreferences.getString(Constantes.ACCESS_TOKEN_SHARED_PREFERENCES, "").toString()
                )

                when(call.code()) {
                    200 -> {
                        Log.d("Debug", "Entramos a buscar paradas de linea " + nLinea + " Dir: " + dir)
                        val todoParadasLinea = call.body() //excetcion
                        val stopsLinea = todoParadasLinea?.data?.get(0)?.stops
                        lista.clear()

                        stopsLinea?.forEach {
                            lista.add(it)
                        }

                        Runnable {
                            tvBuscarLineaId.text = "Paradas de la línea " + nLinea
                            tvBuscarLineaDir.text = "De: " + lista.get(0).name + " A: " + lista.get(lista.size-1).name
                            rvListaParadasLinea.adapter?.notifyDataSetChanged()
                        }
                    }
                    403 -> {
                        Runnable {
                            startActivity(Intent(this@ListaParadasLineaActivity, LoginActivity::class.java))
                            Toast.makeText(this@ListaParadasLineaActivity, "La sesión ha expirado. Vuelve a iniciar sesión.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    500 -> {
                        Runnable {
                            imgError.visibility = View.VISIBLE
                            Toast.makeText(this@ListaParadasLineaActivity, "Error interno.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> {
                        Runnable {
                            imgError.visibility = View.VISIBLE
                            Toast.makeText(this@ListaParadasLineaActivity, "Error interno.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            catch (e: Exception) {
                Runnable {
                    imgError.visibility = View.VISIBLE
                }
            }

            runOnUiThread(runnable)
        }
    }

    /**
     * Te hace volver al login
     */
    private fun volverAlLogin(): String {
        startActivity(Intent(this, LoginActivity::class.java))
        return "La sesión ha expirado. Vuelve a iniciar sesión."
    }

}