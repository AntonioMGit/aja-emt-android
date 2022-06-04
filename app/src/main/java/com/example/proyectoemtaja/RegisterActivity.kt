package com.example.proyectoemtaja

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.example.proyectoemtaja.config.MD5
import com.example.proyectoemtaja.databinding.ActivityRegisterBinding
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
 * Actividad de registro de usuarios
 */
class RegisterActivity : AppCompatActivity() {

    /**
     * Binding de la interfaz
     */
    private lateinit var binding: ActivityRegisterBinding

    //private lateinit var spSexo: Spinner
    private lateinit var spSexo: TextInputLayout
    private lateinit var etFechaNacimiento: TextInputLayout
    private lateinit var fechaSeleccionada: LocalDate
    private lateinit var correo: TextInputLayout
    private lateinit var password1: TextInputLayout
    private lateinit var password2: TextInputLayout
    private lateinit var nombre: TextInputLayout
    private lateinit var apellidos: TextInputLayout


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSpinner()
        etFechaNacimiento = binding.etFechaNacimiento

        correo = binding.etCorreo
        password1 = binding.etPassword
        password2 = binding.etPassword2
        nombre = binding.etNombre
        apellidos = binding.etApellidos

        etFechaNacimiento.editText!!.inputType = InputType.TYPE_NULL
        etFechaNacimiento.editText!!.keyListener = null

        //Listener de la fecha
        etFechaNacimiento.editText!!.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                if (motionEvent!!.action == MotionEvent.ACTION_UP) {
                    showDatePickerDialog()
                }
                return false
            }

        })

        binding.btnVolverLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnRegistrar.setOnClickListener {
            registrar ()
        }
    }

    /**
     * Comprueba si son datos que se pueden registrar
     */
    private fun registrar() {
        if (todosLlenos()) {
            if (todosValidos()) {
                sendRequest()
            }
        } else {
            Toast.makeText(this, "Rellena todos lo campos", Toast.LENGTH_LONG)
        }
    }

    /**
     * Comprueba los datos
     */
    private fun todosValidos(): Boolean {

        if (!Constantes.regexCorreo.matches(correo.editText!!.text.toString())) {
            Toast.makeText(this, "Formato de correo no válido", Toast.LENGTH_SHORT).show()
            return false
        }

        if (nombre.editText!!.text.length > 20) {
            Toast.makeText(this, "El tamaño del nombre supera los 20 caracteres", Toast.LENGTH_SHORT).show()
            return false;
        }

        if (apellidos.editText!!.text.length > 30) {
            Toast.makeText(this, "El tamaño de los apellidos supera los 30 caracteres", Toast.LENGTH_SHORT).show()
            return false;
        }

        if (!valeFecha(etFechaNacimiento.editText!!.text.toString())) {
            Toast.makeText(this, "La fecha no es válida", Toast.LENGTH_SHORT).show()
            return false;
        }

        if (!(password1.editText!!.text.toString() == password2.editText!!.text.toString())) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
            limpiarClaves()
            return false
        }

        if (!Constantes.regexClave.matches(password1.editText!!.text.toString())) {
            Toast.makeText(this, "La contraseña no sigue los requisitos", Toast.LENGTH_SHORT).show()
            limpiarClaves()
            return false
        }


        return true

    }

    /**
     * Prueba la fecha
     * @return {@true} si vale
     *         {@false} si no vale
     */
    private fun valeFecha(text: String): Boolean {
        return try {
            LocalDate.parse(text, Constantes.FORMATO_FECHA_MOSTRAR).isBefore(LocalDate.now())
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Mira si todos los campos estan llenos
     * @return {@true} si estan llenos
     *         {@false} si hay alguno vacio
     */
    private fun todosLlenos(): Boolean {
        return correo.editText!!.text.isNotBlank() &&
                password1.editText!!.text.isNotBlank() &&
                password2.editText!!.text.isNotBlank() &&
                nombre.editText!!.text.isNotBlank() &&
                apellidos.editText!!.text.isNotBlank()
    }

    /**
     * Manda la peticion de registro al servidor
      */
    private fun sendRequest() {
        CoroutineScope(Dispatchers.IO).launch {
            var msg: String = ""

            //Request
            val call = UrlServidor.getRetrofit().create(APIService::class.java).insertUsuario(
                Usuario(
                    correo = correo.editText!!.text.toString(),
                    clave = MD5.encriptar(password1.editText!!.text.toString()),
                    nombre = nombre.editText!!.text.toString(),
                    apellidos = apellidos.editText!!.text.toString(),
                    fechaNacimiento = LocalDate.parse(etFechaNacimiento.editText!!.text.toString(), Constantes.FORMATO_FECHA_MOSTRAR).format(Constantes.FORMATO_FECHA_ENVIAR),
                    sexo = Sexo.sacarSexo(spSexo.editText!!.text.toString())
                )
            )

            msg = when (call.code()) {
                201 -> "Insertado correctamente"
                400 -> "El usuario ya existe"
                else -> "No se ha podido realizar la inserción"
            }

            runOnUiThread {
                Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Limpia las claves
     */
    private fun limpiarClaves() {
        password2.editText!!.text.clear()
        password1.editText!!.text.clear()
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

        fechaSeleccionada = LocalDate.of(year, (month + 1), day)
        etFechaNacimiento.editText!!.setText(fechaSeleccionada.format(Constantes.FORMATO_FECHA_MOSTRAR).toString())

    }

    /**
     * Inicializa el sponner
     */
    private fun initSpinner() {
        spSexo = binding.spSexo

        val items = listOf(Sexo.STRING_HOMRBE, Sexo.STRING_MUJER, Sexo.STRING_NO_ESPECIFICADO)
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        (spSexo.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }


}

