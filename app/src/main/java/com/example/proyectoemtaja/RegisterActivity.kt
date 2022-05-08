package com.example.proyectoemtaja

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.proyectoemtaja.config.MD5
import com.example.proyectoemtaja.databinding.ActivityRegisterBinding
import com.example.proyectoemtaja.models.usuario.Sexo
import com.example.proyectoemtaja.models.usuario.Usuario
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.utilities.Variables
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RegisterActivity : AppCompatActivity() {

    val FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private lateinit var spSexo: Spinner

    private lateinit var etFechaNacimiento: EditText
    private lateinit var sexo: Sexo
    private lateinit var fechaSeleccionada: LocalDate
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var correo: EditText
    private lateinit var password1: EditText
    private lateinit var password2: EditText
    private lateinit var nombre: EditText
    private lateinit var apellidos: EditText

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

        //Listener de la fecha
        etFechaNacimiento.setOnClickListener {
            showDatePickerDialog()
        }

        //lo pongo para probar que lo demas funciona
        //habria que quitarlo

        binding.button.setOnClickListener {
            startActivity(Intent(this, FavoritoActivity::class.java))
        }

        binding.btnVolverLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnRegistrar.setOnClickListener { registrar ()}
    }

    private fun registrar() {
        if (correo.text.isNotBlank() && password1.text.isNotBlank() && password2.text.isNotBlank() && nombre.text.isNotBlank() && apellidos.text.isNotBlank()) {
            if (password1.text.toString() == password2.text.toString()) {
                sendRequest()
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
                limpiarClaves()
            }
        } else {
            Toast.makeText(this, "Rellena todos lo campos", Toast.LENGTH_LONG)
        }
    }

    private fun sendRequest() {
        CoroutineScope(Dispatchers.IO).launch {

            //Request
            val call = getRetrofit().create(APIService::class.java).insertUsuario(
                Usuario(correo = correo.text.toString(), clave = MD5.encriptar(password1.text.toString()),
                    nombre = nombre.text.toString(), apellidos = apellidos.text.toString(),
                    fechaNacimiento = etFechaNacimiento.text.toString(), sexo = sexo
                )
            )

            runOnUiThread {
                if (call.isSuccessful) {
                    Toast.makeText(applicationContext,"Insertado correctamente.", Toast.LENGTH_LONG).show()

                    //coge el usuario y la contraseña ENCRIPTADA para evitarse tener que meter el usuario y la contraseña all t tiempo
                    //y asi loguear directamente hasta que de al boton de cerrar sesion
                    val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.apply {
                        putString("email", correo.text.toString())
                        putString("pass", MD5.encriptar(password1.text.toString()))
                    }.apply()

                    //QUITAR
                    Toast.makeText(
                        applicationContext,
                        MD5.encriptar(password1.text.toString()) + "",
                        Toast.LENGTH_LONG
                    ).show()

                    //tras insertar el usuario loguea directamente
                    //DESCOMENTAR LA LINEA DE ABAJO
                    //startActivity(Intent(applicationContext, FavoritoActivity::class.java))

                } else {
                    var codigoError = call.code()

                    if (codigoError == 400) { //error 400 lo tira el servidor cuando ya esta insertado ese usuario.
                        Toast.makeText(
                            applicationContext,
                            "El usuario ya exite.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else { //si tira otro por la razon que sea
                        Toast.makeText(
                            applicationContext,
                            "No se ha podido realizar la inserción",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun limpiarClaves() {
        password2.text.clear()
        password1.text.clear()
    }

    private fun getRetrofit(): Retrofit {

        // val gson = GsonBuilder().setDateFormat("yyyy-MM-dd").create()
        return Retrofit.Builder().baseUrl(Variables.URL_BASE).addConverterFactory(
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
        etFechaNacimiento.setText(fechaSeleccionada.format(FORMATO_FECHA).toString())

        Toast.makeText(this, "Fecha: ${fechaSeleccionada.format(FORMATO_FECHA).toString()}", Toast.LENGTH_LONG).show()
    }

    private fun initSpinner() {
        spSexo = binding.spSexo//findViewById(R.id.spSexo)
        val list = Sexo.values()
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        spSexo.adapter = adaptador

        spSexo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sexo = list[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                sexo = Sexo.MUJER
            }

        }
    }


}

