package com.example.proyectoemtaja

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoemtaja.databinding.ActivityMainBinding
import com.example.proyectoemtaja.models.peticiones.BorrarFavoritoRequest
import com.example.proyectoemtaja.models.timeArrival.Arrive
import com.example.proyectoemtaja.models.timeArrival.IncidentData
import com.example.proyectoemtaja.models.timeArrival.Line
import com.example.proyectoemtaja.models.timeArrival.TimeArrivalBus
import com.example.proyectoemtaja.models.usuario.Favorito
import com.example.proyectoemtaja.recyclerview_adapter.BusParadaAdapter
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.utilities.Constantes
import com.example.proyectoemtaja.utilities.Paradas
import com.example.proyectoemtaja.utilities.UrlServidor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.stream.Collectors

/**
 * Activity para ver la informacion de las paradas
 */
class MainActivity : AppCompatActivity() {

    /**
     * Binding de la interfaz
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * Recycler view de info
     */
    private lateinit var rvBuses: RecyclerView

    /**
     * Campos
     */
    private lateinit var tvNombre: TextView
    private lateinit var tvIdParada: TextView
    private lateinit var imgBus: ImageView

    /**
     * View en caso de error
     */
    private lateinit var imgError: View

    /**
     * Dice si se puede interactuar con los botones de guardar/actualizar/borrar favorito
     */
    private var botonActivo: Boolean = true

    /**
     * Datos de la parada
     */
    private var timeArrivalBus: TimeArrivalBus? = null

    //Dialog
    private lateinit var btnGuardar: Button
    private lateinit var etNombre: TextInputLayout

    //Dialog alerta
    private lateinit var tvTitulo: TextView
    private lateinit var tvDesde: TextView
    private lateinit var tvHasta: TextView
    private lateinit var tvCausa: TextView
    private lateinit var tvContenido: TextView
    private lateinit var tvContador: TextView
    private lateinit var btnAnterior: ImageButton
    private lateinit var btnPosterior: ImageButton

    /**
     * Posicion de incidente en dialogo de alertas
     */
    private var posIncidente = 0

    /**
     * Lista de datos del recyclerview
     */
    val lista = ArrayList<Map.Entry<String, List<Arrive>>>()

    /**
     * Lista de direcciones asociadas a los autobuses
     */
    val listaDirecciones = ArrayList<String>()

    /**
     * Lista de codigos asociados a los autobuses
     */
    val listaCodigosLineas = ArrayList<String>()

    /**
     * Numero de parada
     */
    var nParada: String = ""

    /**
     * Booleano que permite controlar cuando se realiza el refresco de los tiempos
     * Será falso cuando esta Activity no esté activa
     * Será verdadero cuando esta Activity esté activa
     */
    var refresco = true

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
        imgError = binding.imgError


        nParada = intent.getStringExtra("nParada")!!

        //searchParada(nParada.toString())

        Log.d("Debug", "OnCreate fin")
    }

    override fun onPause() {
        super.onPause()
        refresco = false
    }

    override fun onResume() {
        super.onResume()
        refresco = true
        refrescarTiempos()
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

    /**
     * Dialogo de borrar favorito
     */
    private fun dialogoBorrarFavorito() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Alerta")
            .setMessage("Se borrará el favorito. ¿Quieres continuar?")
            .setPositiveButton("Aceptar", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    borrarFavorito(nParada)
                }
            })
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Dialogo actualizarFavorito
     */
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

    /**
     * Dialogo guardar favorito
     */
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

    /**
     * Aplica los cambios del menu
     */
    private fun aplicarCambiosMenu() {
        invalidateOptionsMenu()
    }

    /**
     * Llama al servidor para guardar un favorito
     * @param favorito favorito a guardar
     */
    private fun guardarFavorito(favorito: Favorito) {
        CoroutineScope(Dispatchers.IO).launch {

            botonActivo = false

            var msg: String = try {
                val sharedPreferences = getSharedPreferences(
                    Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                var call = UrlServidor.getRetrofit().create(APIService::class.java).insertarFavorito(
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
                    403 -> {
                        volverAlLogin()
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

    /**
     * Llama al servidor para actualizar un favorito del usuario
     */
    private fun actualizarFavorito(favorito: Favorito) {
        CoroutineScope(Dispatchers.IO).launch {
            botonActivo = false

            var msg = try {
                val sharedPreferences = getSharedPreferences(
                    Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                var call = UrlServidor.getRetrofit().create(APIService::class.java).actualizarFavorito(
                    favorito = favorito,
                    token = "Bearer " + sharedPreferences.getString(
                        Constantes.ACCESS_TOKEN_SHARED_PREFERENCES,
                        ""
                    ).toString()
                )


                when (call.code()) {
                    202 -> "Actualizado correctamente"
                    400 -> "El favorito no existe"
                    403 -> {
                        volverAlLogin()
                    }
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

    /**
     * Manda de vuelta al login
     */
    private fun volverAlLogin(): String {
        startActivity(Intent(this, LoginActivity::class.java))
        return "La sesión ha expirado. Vuelve a iniciar sesión."
    }

    /**
     * Llama al servidor para borrar a un favorito
     * @param idParada id de favorito a borrar
     */
    private fun borrarFavorito(idParada: String) {
        CoroutineScope(Dispatchers.IO).launch {
            botonActivo = false

            var msg = try {
                val sharedPreferences = getSharedPreferences(
                    Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES,
                    Context.MODE_PRIVATE
                )
                var call = UrlServidor.getRetrofit().create(APIService::class.java).borrarFavorito(
                    favorito = BorrarFavoritoRequest(idParada),
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
                    403 -> {
                        volverAlLogin()
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

    /**
     * Busca las paradas de una linea de autobuses
     * @param pos posicion en la lista
     */
    private fun buscarLinea(pos: Int) {
        var lin = lista[pos]
        var dir = listaDirecciones[pos]
        var codLineas = listaCodigosLineas[pos]

        //Toast.makeText(this@MainActivity, codLineas, Toast.LENGTH_SHORT).show()

        var intent = Intent(this, ListaParadasLineaActivity::class.java)
        intent.putExtra("nLinea", lin.key.toString())
        intent.putExtra("codLinea", codLineas)
        intent.putExtra("dir", dir)
        startActivity(intent)

    }

    /**
     * Busca los datos de la parada
     * @param parada numero de parada
     */
    private fun searchParada(parada: String) {

        CoroutineScope(Dispatchers.IO).launch {

            var runnable: Runnable = try {
                val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)

                val call = UrlServidor.getRetrofit().create(APIService::class.java).getTimeArrivalBus(
                    url = UrlServidor.urlTiempoAutobus(parada),
                    token = "Bearer " + sharedPreferences.getString(Constantes.ACCESS_TOKEN_SHARED_PREFERENCES, "").toString()
                )

                when (call.code()) {
                    200 -> {
                        timeArrivalBus = call.body()
                        modificarRV()
                    }
                    403 -> {
                        Runnable {
                            volverAlLogin()
                        }
                    }
                    else -> {
                        Runnable {
                            imgError.visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", "Error al actualizar datos")

                Runnable {
                    imgError.visibility = View.VISIBLE
                }
            }

            runOnUiThread(runnable)
        }
    }

    /**
     * Modifica los datos del recyclerviewer
     * @returm Datos a ejecutar en el hilo principal
     */
    private fun modificarRV(): Runnable {
        return if (timeArrivalBus!!.data.get(0).arrive.size > 0) {
            var mapa = timeArrivalBus?.data?.get(0)?.arrive?.stream()
                ?.collect(Collectors.groupingBy { it.line })
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
            Runnable {
                tvIdParada.text = "Parada de buses EMT ${nParada}"
                tvNombre.text = timeArrivalBus!!.data[0].stopInfo[0].description

                if (timeArrivalBus!!.data[0].incident.listaIncident.data.size > 0) {
                    imgBus.setImageResource(R.drawable.ic_baseline_bus_alert_24)
                    imgBus.setOnClickListener {
                        mostrarAlertasParada()
                    }
                }
                rvBuses.adapter?.notifyDataSetChanged()
            }
        } else {
            Log.e("Debug", "Error al buscar parada")
            Runnable {
                tvIdParada.text = "Parada de buses EMT ${nParada}"
                tvNombre.text = timeArrivalBus!!.data[0].stopInfo[0].description

                if (timeArrivalBus!!.data[0].incident.listaIncident.data.size > 0) {
                    imgBus.setImageResource(R.drawable.ic_baseline_bus_alert_24)
                    imgBus.setOnClickListener {
                        mostrarAlertasParada()
                    }
                }
                imgError.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Muestra las alertas de una parada
     */
    private fun mostrarAlertasParada() {

        val builder = AlertDialog.Builder(this@MainActivity)
        val view = layoutInflater.inflate(R.layout.dialog_alerta_parada, null)

        builder.setView(view)

        val dialog = builder.create()
        dialog.show()

        tvTitulo = view.findViewById<TextView>(R.id.tvTituloAlerta)
        tvDesde = view.findViewById<TextView>(R.id.tvFechaInicioAlerta)
        tvHasta = view.findViewById<TextView>(R.id.tvFechaFinAlerta)
        tvContenido = view.findViewById<TextView>(R.id.tvContenidoAlerta)
        tvCausa = view.findViewById<TextView>(R.id.tvCausaAlerta)
        tvContador = view.findViewById<TextView>(R.id.tvContador)
        btnAnterior = view.findViewById<ImageButton>(R.id.btnIzqAlerta)
        btnPosterior = view.findViewById<ImageButton>(R.id.btnDchaAlerta)


        if (timeArrivalBus!!.data[0].incident.listaIncident.data.size < 2) {
            btnPosterior.visibility = View.INVISIBLE
            btnAnterior.visibility = View.INVISIBLE
        }

        asignarDatosAlerta(posIncidente)

        btnAnterior.setOnClickListener {

            if (posIncidente > 0) {
                posIncidente--
            }
            else {
                posIncidente = timeArrivalBus!!.data[0].incident.listaIncident.data.size - 1
            }

            asignarDatosAlerta(posIncidente)

        }

        btnPosterior.setOnClickListener {

            if (posIncidente < timeArrivalBus!!.data[0].incident.listaIncident.data.size - 1) {
                posIncidente++
            }
            else {
                posIncidente = 0
            }

            asignarDatosAlerta(posIncidente)

        }

    }

    /**
     * Asigna los datos a la alerta
     * @param posIncidente Posicion del incidente actual
     */
    private fun asignarDatosAlerta(posIncidente: Int) {

        val  incidentData: IncidentData = timeArrivalBus!!.data[0].incident.listaIncident.data[posIncidente]

        tvTitulo.text = incidentData.title
        tvDesde.text = "Desde: ${incidentData.rssFrom}"
        tvHasta.text = "Hasta: ${incidentData.rssTo}"
        tvCausa.text = "Causa: ${incidentData.cause}"
        //Sabemos que es una solucion fea, pero no sabemos como tratarlo si no. Siempre tienen ese formato. Lo que hay en la posicion 1 es una imagen en HTML.
        tvContenido.text = incidentData.description.split(" Ver más detalle en documento adjunto")[0]
        tvContador.text = "${ posIncidente + 1 }/${ timeArrivalBus!!.data[0].incident.listaIncident.data.size }"


    }

    /**
     * Buscar codigo parada
     * @param numParada Numero de parada
     * @param stopLines array de lineas
     * @return devuelve un string con la linea
     */
    private fun buscarCodigoParada(numParada: String, stopLines: ArrayList<Line>?): String {
        var cod = ""

        stopLines?.forEach {
            if (it.label.equals(numParada)) {
                cod = it.line.toString()
            }
        }

        return cod
    }
    /**
     * Coroutine que permite llamar a la función de searchParada() para actualizar los tiempos
     * Tiene una pausa de 10 segundo entre cada llamada
     * Mientras la variable refresco sea true se ejecutará
     * Cuando la variable refresco sea false dejará de ejecutarse
     */
    fun refrescarTiempos(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            while(refresco) {
                searchParada(nParada.toString())
                //Log.e("Tiempos", "Hola")
                delay(10000)
            }
        }
    }
}