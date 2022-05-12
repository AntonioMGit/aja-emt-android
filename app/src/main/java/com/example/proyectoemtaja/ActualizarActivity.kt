package com.example.proyectoemtaja

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.example.proyectoemtaja.config.MD5
import com.example.proyectoemtaja.databinding.ActivityActualizarBinding
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
import java.time.LocalDate

class ActualizarActivity : AppCompatActivity() {
    private lateinit var binding:ActivityActualizarBinding

    lateinit var etAcNombre:TextInputLayout
    lateinit var etAcApellidos:TextInputLayout
    lateinit var etAcFecha:TextInputLayout
    lateinit var spSexo:TextInputLayout

    private lateinit var fechaSeleccionada: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar)

        etAcNombre = binding.etAcNombre
        etAcApellidos = binding.etAcApellidos
        etAcFecha = binding.etFechaNacimiento
        spSexo = binding.spSexo

        initSpinner()
        binding.btnIrMenuPrincipal.setOnClickListener {
            startActivity(Intent(this,MenuPrincipalActivity::class.java))
        }

        binding.btnActualizar.setOnClickListener {
            actualizar()
        }
    }

    private fun actualizar() {
        if(etAcNombre.editText!!.text.isNotBlank() && etAcApellidos.editText!!.text.isNotBlank() ){
            sendRequest()
        }else {
            Toast.makeText(this, "Rellena todos lo campos", Toast.LENGTH_LONG)
        }
    }

    private fun sendRequest() {
        CoroutineScope(Dispatchers.IO).launch {
            var msg: String = ""
            val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            //Request
            val call = getRetrofit().create(APIService::class.java).actualizarUsuario(sharedPreferences.getString(Constantes.ACCESS_TOKEN_SHARED_PREFERENCES,"").toString(),
                Usuario(
                    correo = sharedPreferences.getString(Constantes.EMAIL_SHARED_PREFERENCES,"").toString(),
                    clave = sharedPreferences.getString(Constantes.PASSWORD_SHARED_PREFERENCES,"").toString(),
                    nombre = etAcNombre.editText!!.text.toString(),
                    apellidos = etAcApellidos.editText!!.text.toString(),
                    fechaNacimiento = etAcFecha.editText!!.text.toString(),
                    sexo = Sexo.sacarSexo(spSexo.editText!!.text.toString())
                )
            )

            if (call.isSuccessful) {
                msg = "Actualizado correctamente."


            } else {
                var codigoError = call.code()
                //FIXME MIRAR LO QUE DEVUELVE EL SERVIDOR
                if (codigoError == 400) { //error 400 lo tira el servidor cuando ya esta insertado ese usuario.
                    msg = "Error  ya exite."
                } else { //si tira otro por la razon que sea
                    msg = "No se ha podido realizar la actualizacion"
                }
            }

            runOnUiThread {
                Toast.makeText(this@ActualizarActivity, msg, Toast.LENGTH_SHORT).show()
            }
        }
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
        etAcFecha.editText!!.setText(fechaSeleccionada.format(Constantes.FORMATO_FECHA).toString())

        Toast.makeText(this, "Fecha: ${fechaSeleccionada.format(Constantes.FORMATO_FECHA).toString()}", Toast.LENGTH_LONG).show()
    }

    private fun initSpinner() {
        spSexo = binding.spSexo

        val items = listOf(Sexo.STRING_HOMRBE, Sexo.STRING_MUJER, Sexo.STRING_NO_ESPECIFICADO)
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        (spSexo.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

}