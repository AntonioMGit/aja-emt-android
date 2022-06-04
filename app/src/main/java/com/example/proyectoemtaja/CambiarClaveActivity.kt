package com.example.proyectoemtaja

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.proyectoemtaja.config.MD5
import com.example.proyectoemtaja.databinding.ActivityCambiarClaveBinding
import com.example.proyectoemtaja.models.peticiones.CambiarClaveRequest
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.utilities.Constantes
import com.example.proyectoemtaja.utilities.UrlServidor
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Activity de cambio de clave
 */
class CambiarClaveActivity : AppCompatActivity() {

    /**
     * Campos de la actividad
     */
    private lateinit var etCorreo: TextInputLayout
    private lateinit var etCodigo: TextInputLayout
    private lateinit var etClave: TextInputLayout

    /**
     * View de peticion de correo
     */
    private lateinit var viewCorreo: View

    /**
     * View de peticion de clave
     */
    private lateinit var viewClave: View

    private lateinit var binding: ActivityCambiarClaveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCambiarClaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        etCorreo = binding.etCorreo
        etClave = binding.etClave
        etCodigo = binding.etCodigo

        viewCorreo = binding.viewSolicitarCorreo
        viewClave = binding.viewSolicitarClave

        viewCorreo.visibility = View.VISIBLE
        viewClave.visibility = View.GONE

        binding.btnConfirmarCorreo.setOnClickListener {
            if (etCorreo.editText!!.text.isNotEmpty()) {
                enviarCorreo()
            }
            else {
                Toast.makeText(this, "Faltan campos por rellenar", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnConfirmarCambio.setOnClickListener {
            if (etClave.editText!!.text.isNotBlank() && etCodigo.editText!!.text.isNotBlank()) {
                accionBtnCambiarClave()
            }
            else {
                Toast.makeText(this, "Faltan campos por rellenar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Accion del boton de cambiar clave
     */
    private fun accionBtnCambiarClave() {
        if (Constantes.regexClave.matches(etClave.editText!!.text.toString())) {
            cambiarClave()
        }
        else {
            Toast.makeText(this, "La contraseña no sigue los requisitos", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * Manda la peticion de cambiar clave
     */
    private fun cambiarClave() {
        CoroutineScope(Dispatchers.IO).launch {

            runOnUiThread {
                binding.btnConfirmarCambio.setEnabled(false)
            }

            val msg = try {
                val call = UrlServidor.getRetrofit().create(APIService::class.java).cambiarClave(
                    CambiarClaveRequest(
                        idUsuario = etCorreo.editText!!.text.toString(),
                        clave = MD5.encriptar(etClave.editText!!.text.toString()),
                        token = MD5.encriptar(etCodigo.editText!!.text.toString())
                    )
                )

                when (call.code()) {
                    200 -> {
                        finish()
                        "La contraseña se ha cambiado correctamente"
                    }
                    400 -> "El token introducido no existe"
                    404 -> "No se puede cambiar la clave"
                    else -> "Error al cambiar la clave"
                }
            }
            catch (e: Exception) {
                "No se ha podido procesar la operación"
            }

            runOnUiThread {
                Toast.makeText(this@CambiarClaveActivity, msg, Toast.LENGTH_SHORT).show()
                binding.btnConfirmarCambio.setEnabled(true)
            }
        }
    }

    /**
     * Peticion para solicitar token de cambio de vlave
     */
    private fun enviarCorreo() {
        CoroutineScope(Dispatchers.IO).launch {

            runOnUiThread {
                binding.btnConfirmarCorreo.setEnabled(false)
            }

            val msg = try {
                val call = UrlServidor.getRetrofit().create(APIService::class.java).pedirCodigo(
                    correo = etCorreo.editText!!.text.toString()
                )

                when (call.code()) {
                    200 -> {
                        runOnUiThread {
                            ocultarView()
                        }
                        "Se ha mandado el codigo"
                    }
                    404 -> "El correo introducido no existe"
                    else -> "Error al cambiar la clave"
                }
            }
            catch (e: Exception) {
                "No se ha podido procesar la operación"
            }

            runOnUiThread {
                Toast.makeText(this@CambiarClaveActivity, msg, Toast.LENGTH_SHORT).show()
                binding.btnConfirmarCorreo.setEnabled(true)
            }
        }
    }

    /**
     * Cambia las view
     */
    private fun ocultarView() {
        viewCorreo.visibility = View.GONE
        viewClave.visibility = View.VISIBLE
    }
}