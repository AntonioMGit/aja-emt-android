package com.example.proyectoemtaja

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.proyectoemtaja.utilities.DatePickerFragment
import com.example.proyectoemtaja.utilities.UrlServidor
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.time.LocalDate

/**
 * Actividad de actializacion de datos de usuario
 */
class ActualizarActivity : AppCompatActivity() {

    /**
     * Binding de la interfaz
     */
    private lateinit var binding:ActivityActualizarBinding

    /**
     * Campos de la actividad
     */
    private lateinit var etAcNombre:TextInputLayout
    private lateinit var etAcApellidos:TextInputLayout
    private lateinit var etAcFecha:TextInputLayout
    private lateinit var spSexo:TextInputLayout
    private lateinit var etAcPasswordActual: TextInputLayout
    private lateinit var etAcPasswordNueva: TextInputLayout

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

    /**
     * Hace la peticion al servidor para rellenar los campos con los datos actuales.
     */
    private fun rellenarFormulario() {
        CoroutineScope(Dispatchers.IO).launch {
            var msg: String = ""
            val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            //Request
            val call = UrlServidor.getRetrofit().create(APIService::class.java).buscarUsuario("Bearer ${sharedPreferences.getString(Constantes.ACCESS_TOKEN_SHARED_PREFERENCES,"").toString()}")

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

    /**
     * Comprueba los datos y si son correctos, actualiza.
     */
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

    /**
     * Comprueba si los datos siguen los requisitos
     * @return {@true} si es valido
     *         {@false} si no es valido
     */
    private fun todoValido(): Boolean {

        if (etAcNombre.editText!!.text.length > 20) {
            Toast.makeText(this, "El tama??o del nombre supera los 20 caracteres", Toast.LENGTH_SHORT).show()
            return false;
        }

        if (etAcApellidos.editText!!.text.length > 30) {
            Toast.makeText(this, "El tama??o de los apellidos supera los 30 caracteres", Toast.LENGTH_SHORT).show()
            return false;
        }

        if (!valeFecha(etAcFecha.editText!!.text.toString())) {
            Toast.makeText(this, "La fecha no es v??lida", Toast.LENGTH_SHORT).show()
            return false;
        }

        val nuevaClave = etAcPasswordNueva.editText!!.text

        if (nuevaClave.isNotBlank() && !Constantes.regexClave.matches(nuevaClave)) {
            Toast.makeText(this, "La contrase??a no sigue los requisitos.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true

    }

    /**
     * Mira si la feha es valida
     * @param text fecha a comprobar
     * @return {@true} si la fecha es valida
     *         {@false} si la fecha no es valida
     */
    private fun valeFecha(text: String): Boolean {
        return try {
            LocalDate.parse(text, Constantes.FORMATO_FECHA_MOSTRAR).isBefore(LocalDate.now())
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Mira si todo esta relleno
     * @return {@true} si esta todo relleno
     *         {@false} si no esta todo relleno
     */
    private fun todoRelleno(): Boolean {
        return etAcNombre.editText!!.text.isNotBlank() &&
                etAcApellidos.editText!!.text.isNotBlank() &&
                etAcFecha.editText!!.text.isNotBlank() &&
                etAcPasswordActual.editText!!.text.isNotBlank() &&
                spSexo.editText!!.text.isNotBlank()
    }

    /**
     * Envia la request de actualizar datos
     */
    private fun sendRequest() {
        CoroutineScope(Dispatchers.IO).launch {
            val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            //Request
            val call = UrlServidor.getRetrofit().create(APIService::class.java).actualizarUsuario(
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

    /**
     * Limplia la seccion de claves
     */
    private fun limpiarClaves() {
        etAcPasswordActual.editText!!.text.clear()
        etAcPasswordNueva.editText!!.text.clear()
    }


    /**
     * Inicializar el dialogo creado en onCreateDialog de la clase DatePickerDialog
     */
    private fun showDatePickerDialog() {
        //Crear un objeto de la clase DatePickerDialog
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    /**
     * Funcion que se le pasa a DatePickerDialog al crearlo. Cuando ya se ha seleccionado una fecha, se llama a este metodo
     */
    private fun onDateSelected(day: Int, month: Int, year: Int) {
        etAcFecha.editText!!.setText(LocalDate.of(year, (month + 1), day, ).format(Constantes.FORMATO_FECHA_MOSTRAR).toString())
    }

    /**
     * Inicializa el spinner de sexo
     */
    private fun initSpinner() {
        spSexo = binding.spSexo
        val adapter = ArrayAdapter(this, R.layout.list_item, Sexo.getSexoLista())
        (spSexo.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    /**
     * Devuelve al login
     * @return mensaje a mostrar por pantalla
     */
    private fun volverAlLogin(): String {
        startActivity(Intent(this, LoginActivity::class.java))
        return "La sesi??n ha expirado. Vuelve a iniciar sesi??n."
    }

}