package com.example.proyectoemtaja

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.databinding.ActivityMainBinding
import com.example.proyectoemtaja.models.peticiones.Favorito
import com.example.proyectoemtaja.models.timeArrival.Arrive
import com.example.proyectoemtaja.models.timeArrival.Line
import com.example.proyectoemtaja.utilities.Constantes
import com.example.proyectoemtaja.utilities.UrlServidor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
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
    private lateinit var rvBuses: RecyclerView
    private lateinit var tvNombre: TextView
    private lateinit var tvIdParada: TextView
    private lateinit var imgBus: ImageView

    private var botonActivo: Boolean = true

    //Dialog
    private lateinit var btnGuardar: Button
    private lateinit var etNombre: TextInputLayout

    val lista = ArrayList<Map.Entry<String, List<Arrive>>>()
    val listaDirecciones = ArrayList<String>()
    val listaCodigosLineas = ArrayList<String>()

    var nParada: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Debug", "OnCreate ini")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvBuses = binding.rvBusesParada
        rvBuses.layoutManager = LinearLayoutManager(this)

        var adapter = BusParadaAdapter(lista)

        adapter.setOnItemClickListener(object : BusParadaAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                buscarLinea(position)
            }
        })

        rvBuses.adapter = adapter

        tvIdParada = binding.tvBuscarParadaId
        tvNombre = binding.tvBuscarParadaNombre
        imgBus = binding.imgFotoBus


        nParada = intent.getStringExtra("nParada")!!

        searchParada(nParada.toString())
        Log.d("Debug", "OnCreate fin")
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        var inflater: MenuInflater = menuInflater
        var esFavorito =
            Paradas.listaFavoritos.stream().filter { it.idParada.equals(nParada) }.count() > 0

        if (esFavorito) {
            inflater.inflate(R.menu.menu_favorito_guardado, menu)
        } else {
            inflater.inflate(R.menu.menu_favorito, menu)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (botonActivo) {
            when (item.itemId) {
                R.id.itemGuardarFavorito -> {
                    dialogoGuardarFavorito()
                    true
                }

                R.id.itemActualizarFavorito -> {
                    dialogoActualizarFavorito()
                    true
                }

                R.id.itemBorrarFavorito -> {
                    dialogoBorrarFavorito()
                    true
                }

                else -> super.onOptionsItemSelected(item)
            }
        } else {
            Toast.makeText(this, "Procesando petición...", Toast.LENGTH_SHORT).show()
            super.onOptionsItemSelected(item)
        }
    }

    private fun dialogoBorrarFavorito() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Alerta")
            .setMessage("Se borrará el favorito. ¿Quieres continuar?")
            .setPositiveButton("Aceptar", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    borrarFavorito(nParada)
                    Toast.makeText(this@MainActivity, "Borrar", Toast.LENGTH_SHORT).show()
                }
            })
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun dialogoActualizarFavorito() {
        val builder = AlertDialog.Builder(this@MainActivity)
        val view = layoutInflater.inflate(R.layout.dialog_actualizar_favorito, null)

        builder.setView(view)

        val dialog = builder.create()
        dialog.show()

        etNombre = view.findViewById<TextInputLayout>(R.id.etActualizarFavorito)
        btnGuardar = view.findViewById<Button>(R.id.btnActualizarFavorito)

        btnGuardar.setOnClickListener {
            var nombre = etNombre.editText!!.text.toString()
            if (nombre.length < 30) {
                actualizarFavorito(Favorito(idParada = nParada, nombreParada = nombre))
            } else {
                Toast.makeText(this, "El nombre es demasiado largo", Toast.LENGTH_SHORT).show()
            }
            dialog.hide()
        }
    }

    private fun dialogoGuardarFavorito() {
        val builder = AlertDialog.Builder(this@MainActivity)
        val view = layoutInflater.inflate(R.layout.dialog_guardar_favorito, null)

        builder.setView(view)

        val dialog = builder.create()
        dialog.show()

        etNombre = view.findViewById<TextInputLayout>(R.id.etGuardarFavorito)
        btnGuardar = view.findViewById<Button>(R.id.btnGuardarFavorito)

        btnGuardar.setOnClickListener {
            var nombre = etNombre.editText!!.text.toString()
            if (nombre.length < 30) {
                guardarFavorito(Favorito(idParada = nParada, nombreParada = nombre))
            } else {
                Toast.makeText(this, "El nombre es demasiado largo", Toast.LENGTH_SHORT).show()
            }
            dialog.hide()
        }
    }

    private fun aplicarCambiosMenu() {
        invalidateOptionsMenu()
    }

    private fun guardarFavorito(favorito: Favorito) {
        CoroutineScope(Dispatchers.IO).launch {

            botonActivo = false

            var msg = try {
                val sharedPreferences = getSharedPreferences(
                    Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                var call = getRetrofit().create(APIService::class.java).insertarFavorito(
                    favorito = favorito,
                    token = "Bearer " + sharedPreferences.getString(
                        Constantes.ACCESS_TOKEN_SHARED_PREFERENCES,
                        ""
                    ).toString()
                )

                when (call.code()) {
                    201 -> {
                        Paradas.listaFavoritos.add(call.body()!!)
                        aplicarCambiosMenu()
                        "Guardado correctamente"
                    }
                    400 -> "El favorito ya existe"
                    else -> "Error al guardar favorito"
                }

            } catch (e: Exception) {
                "Error al guardar favorito"
            }

            runOnUiThread {
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            }

            botonActivo = true
        }
    }

    private fun actualizarFavorito(favorito: Favorito) {
        CoroutineScope(Dispatchers.IO).launch {
            botonActivo = false

            var msg = try {
                val sharedPreferences = getSharedPreferences(
                    Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                var call = getRetrofit().create(APIService::class.java).actualizarFavorito(
                    favorito = favorito,
                    token = "Bearer " + sharedPreferences.getString(
                        Constantes.ACCESS_TOKEN_SHARED_PREFERENCES,
                        ""
                    ).toString()
                )

                when (call.code()) {
                    202 -> "Actualizado correctamente"
                    400 -> "El favorito no existe"
                    else -> "Error al actualizar favorito"
                }
            } catch (e: Exception) {
                "Error al actualizar favorito"
            }

            runOnUiThread {
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            }

            botonActivo = true
        }
    }

    private fun borrarFavorito(idParada: String) {
        CoroutineScope(Dispatchers.IO).launch {
            botonActivo = false

            var msg = try {
                val sharedPreferences = getSharedPreferences(
                    Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                var call = getRetrofit().create(APIService::class.java).borrarFavorito(
                    idParada = idParada,
                    token = "Bearer " + sharedPreferences.getString(
                        Constantes.ACCESS_TOKEN_SHARED_PREFERENCES,
                        ""
                    ).toString()
                )

                when (call.code()) {
                    202 -> {
                        Paradas.listaFavoritos.removeIf { it.idParada == idParada }
                        aplicarCambiosMenu()
                        "Borrado correctamente"
                    }
                    400 -> "El favorito no existe"
                    else -> "Error al borrar favorito"
                }
            } catch (e: Exception) {
                Log.e("Error", "Error al borrar favorito ${e.message}")

                "Error al borrar favorito"
            }

            runOnUiThread {
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            }

            botonActivo = true
        }
    }

    private fun buscarLinea(pos: Int) {
        var lin = lista[pos]
        var dir = listaDirecciones[pos]
        var codLineas = listaCodigosLineas[pos]

        Toast.makeText(this@MainActivity, codLineas, Toast.LENGTH_SHORT).show()

        var intent = Intent(this, ListaParadasLinea::class.java)
        intent.putExtra("nLinea", lin.key.toString())
        intent.putExtra("codLinea", codLineas)
        intent.putExtra("dir", dir)
        startActivity(intent)

    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(UrlServidor.URL_BASE)
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()
    }

    private fun searchParada(parada: String) {

        CoroutineScope(Dispatchers.IO).launch {

            try {
                val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE )

                val call = getRetrofit().create(APIService::class.java).getTimeArrivalBus(
                    url = UrlServidor.urlTiempoAutobus(parada),
                    token = "Bearer " + sharedPreferences.getString(Constantes.ACCESS_TOKEN_SHARED_PREFERENCES, "").toString()
                )

                if (call.isSuccessful) {

                    val timeArrivalBus = call.body() //exceptioon
                    var mapa = timeArrivalBus?.data?.get(0)?.arrive?.stream()?.collect(Collectors.groupingBy { it.line })

                    lista.clear()
                    var numParada = ""
                    var i = 0
                    var stopLines = timeArrivalBus?.data?.get(0)?.stopInfo?.get(0)?.stopLines?.data
                    mapa?.forEach {
                        if (it.value.size > 0) {
                            lista.add(it)
                            //para coger las direcciones a la que va (A o B)
                            listaDirecciones.add(stopLines?.get(i)?.to.toString())

                            //algunos buses como el E3 su codigo y el numero que se muestra es distinto
                            //E3 por ejemplo es 403
                            numParada = stopLines?.get(i)?.label.toString()
                            listaCodigosLineas.add(buscarCodigoParada(numParada, stopLines))
                            i++
                        }
                    }
                    lista.sortWith(Comparator { entry, entry2 ->
                        entry.value.get(0).estimateArrive - (entry2.value.get(0).estimateArrive)
                    })

                    //FIXME: Se tiene que cambiar
                    runOnUiThread {
                        tvIdParada.text =
                            "Parada de buses EMT ${timeArrivalBus!!.data[0].stopInfo[0].label}"
                        tvNombre.text = timeArrivalBus.data[0].stopInfo[0].description

                        if (timeArrivalBus.data[0].incident.listaIncident.data.size > 0) {
                            imgBus.setImageResource(R.drawable.ic_baseline_bus_alert_24)
                            imgBus.setOnClickListener {
                                Toast.makeText(this@MainActivity, "Eu", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    Log.d("Debug", "Datos actualizados")
                } else {
                    Log.e("Debug", "Error al buscar parada")
                }

            } catch (e: Exception) {
                Log.e("Error", "Error al actualizar datos")
            }

            runOnUiThread {
                rvBuses.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun buscarCodigoParada(numParada: String, stopLines: ArrayList<Line>?): String {
        var cod = ""

        stopLines?.forEach {
            if (it.label.equals(numParada)) {
                cod = it.line.toString()
            }
        }

        return cod
    }
}