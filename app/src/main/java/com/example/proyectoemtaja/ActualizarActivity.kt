package com.example.proyectoemtaja

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoemtaja.config.MD5
import com.example.proyectoemtaja.databinding.ActivityActualizarBinding
import com.example.proyectoemtaja.models.peticiones.ActualizarUsuarioRequest
import com.example.proyectoemtaja.models.usuario.Sexo
import com.example.proyectoemtaja.models.usuario.Usuario
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.utilities.Constantes
import com.example.proyectoemtaja.utilities.UrlServidor
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.time.LocalDate

class ActualizarActivity : AppCompatActivity() {
    private lateinit var binding:ActivityActualizarBinding

    private lateinit var etAcNombre:TextInputLayout
    private lateinit var etAcApellidos:TextInputLayout
    private lateinit var etAcFecha:TextInputLayout
    private lateinit var spSexo:TextInputLayout
    private lateinit var etAcPasswordActual: TextInputLayout
    private lateinit var etAcPasswordNueva: TextInputLayout

    private lateinit var fechaSeleccionada: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        etAcNombre = binding.etAcNombre
        etAcApellidos = binding.etAcApellidos
        etAcFecha = binding.etFechaNacimiento
        spSexo = binding.spSexo
        etAcPasswordActual = binding.etPasswordActual
        etAcPasswordNueva = binding.etPasswordNueva

        initSpinner()
        rellenarFormulario ()

        etAcFecha.editText!!.inputType = InputType.TYPE_NULL
        etAcFecha.editText!!.keyListener = null

        //Listener de la fecha
        etAcFecha.editText!!.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                if (motionEvent!!.action == MotionEvent.ACTION_UP) {
                    showDatePickerDialog()
                }
                return false
            }

        })

        binding.btnIrMenuPrincipal.setOnClickListener {
            startActivity(Intent(this,MenuPrincipalActivity::class.java))
        }

        binding.btnActualizar.setOnClickListener {
            actualizar()
        }
    }

    private fun rellenarFormulario() {
        CoroutineScope(Dispatchers.IO).launch {
            var msg: String = ""
            val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            //Request
            val call = getRetrofit().create(APIService::class.java).buscarUsuario("Bearer ${sharedPreferences.getString(Constantes.ACCESS_TOKEN_SHARED_PREFERENCES,"").toString()}")

            if (call.isSuccessful) {
                val usuario: Usuario = call.body()!!

                runOnUiThread {
                    etAcNombre.editText!!.setText(usuario.nombre)
                    etAcApellidos.editText!!.setText(usuario.apellidos)
                    etAcFecha.editText!!.setText(LocalDate.parse(usuario.fechaNacimiento, Constantes.FORMATO_FECHA_ENVIAR).format(Constantes.FORMATO_FECHA_MOSTRAR))
                    spSexo.editText!!.setText(Sexo.fromSexo(usuario.sexo))
                }

                msg = "Datos encontrados"

            } else {
                msg = "Error al buscar datos"
            }

            runOnUiThread {
                Toast.makeText(this@ActualizarActivity, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun actualizar() {
        if(todoRelleno()){
            if (todoValido()) {
                sendRequest()
            }
        }
        else {
            Toast.makeText(this, "Rellena todos lo campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun todoValido(): Boolean {

        if (etAcNombre.editText!!.text.length > 20) {
            Toast.makeText(this, "El tamaño del nombre supera los 20 caracteres", Toast.LENGTH_SHORT).show()
            return false;
        }

        if (etAcApellidos.editText!!.text.length > 30) {
            Toast.makeText(this, "El tamaño de los apellidos supera los 30 caracteres", Toast.LENGTH_SHORT).show()
            return false;
        }

        if (!valeFecha(etAcFecha.editText!!.text.toString())) {
            Toast.makeText(this, "La fecha no es válida", Toast.LENGTH_SHORT).show()
            return false;
        }

        val nuevaClave = etAcPasswordNueva.editText!!.text

        if (nuevaClave.isNotBlank() && !Constantes.regexClave.matches(nuevaClave)) {
            Toast.makeText(this, "La contraseña no sigue los requisitos.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true

    }

    private fun valeFecha(text: String): Boolean {
        return try {
            LocalDate.parse(text, Constantes.FORMATO_FECHA_MOSTRAR).isBefore(LocalDate.now())
        } catch (e: Exception) {
            false
        }
    }

    private fun todoRelleno(): Boolean {
        return etAcNombre.editText!!.text.isNotBlank() &&
                etAcApellidos.editText!!.text.isNotBlank() &&
                etAcFecha.editText!!.text.isNotBlank() &&
                etAcPasswordActual.editText!!.text.isNotBlank() &&
                spSexo.editText!!.text.isNotBlank()
    }

    private fun sendRequest() {
        CoroutineScope(Dispatchers.IO).launch {
            val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            //Request
            val call = getRetrofit().create(APIService::class.java).actualizarUsuario(
                "Bearer ${sharedPreferences.getString(Constantes.ACCESS_TOKEN_SHARED_PREFERENCES,"").toString()}",
                ActualizarUsuarioRequest(
                    clave = MD5.encriptar(etAcPasswordActual.editText!!.text.toString()),
                    nuevaClave = MD5.encriptar(etAcPasswordNueva.editText!!.text.toString()),
                    nombre = etAcNombre.editText!!.text.toString(),
                    apellidos = etAcApellidos.editText!!.text.toString(),
                    fechaNacimiento = LocalDate.parse(etAcFecha.editText!!.text.toString(), Constantes.FORMATO_FECHA_MOSTRAR).format(Constantes.FORMATO_FECHA_ENVIAR),
                    sexo = Sexo.sacarSexo(spSexo.editText!!.text.toString())
                )
            )

            val msg =
                when (call.code()) {
                    202 -> "Actualizado correctamente"
                    406 -> "Clave incorrecta"
                    400 -> "Usuario inexistente"
                    403 -> volverAlLogin()
                    500 -> "Error al actualizar"
                    else -> "Error al actualizar"
                }


            runOnUiThread {
                Toast.makeText(this@ActualizarActivity, msg, Toast.LENGTH_SHORT).show()
                limpiarClaves ()
            }
        }
    }

    private fun limpiarClaves() {
        etAcPasswordActual.editText!!.text.clear()
        etAcPasswordNueva.editText!!.text.clear()
    }

    private fun getRetrofit(): Retrofit {

        // val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        return Retrofit.Builder().baseUrl(UrlServidor.URL_BASE).addConverterFactory(
            GsonConverterFactory.create(/*gson*/)
        ).build()
    }


    //Inicializar el dialogo creado en onCreateDialog de la clase DatePickerDialog
    private fun showDatePickerDialog() {
        //Crear un objeto de la clase DatePickerDialog
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    //Funcion que se le pasa a DatePickerDialog al crearlo. Cuando ya se ha seleccionado una fecha, se llama a este metodo
    private fun onDateSelected(day: Int, month: Int, year: Int) {

        fechaSeleccionada = LocalDate.of(year, (month + 1), day)
        etAcFecha.editText!!.setText(fechaSeleccionada.format(Constantes.FORMATO_FECHA_MOSTRAR).toString())

        //Toast.makeText(this, "Fecha: ${fechaSeleccionada.format(Constantes.FORMATO_FECHA_MOSTRAR).toString()}", Toast.LENGTH_LONG).show()
    }

    private fun initSpinner() {
        spSexo = binding.spSexo

        val items = listOf(Sexo.STRING_HOMRBE, Sexo.STRING_MUJER, Sexo.STRING_NO_ESPECIFICADO)
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        (spSexo.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun volverAlLogin(): String {
        startActivity(Intent(this, LoginActivity::class.java))
        return "La sesión ha expirado. Vuelve a iniciar sesión."
    }

}