package com.example.proyectoemtaja

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.proyectoemtaja.config.MD5
import com.example.proyectoemtaja.databinding.ActivityLoginBinding
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.utilities.Constantes
import com.example.proyectoemtaja.utilities.Paradas
import com.example.proyectoemtaja.utilities.UrlServidor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Activity login
 */
class LoginActivity : AppCompatActivity() {

    /**
     * Binding de la interfaz
     */
    private lateinit var binding: ActivityLoginBinding

    /**
     * Campos
     */
    private lateinit var etEmail: TextInputLayout
    private lateinit var etContrasenia: TextInputLayout
    private lateinit var ckbxGuardarCredenciales: CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        etContrasenia = binding.etContrasenia
        etEmail = binding.etEmail
        buscarDatos()
        probarToken()

        ckbxGuardarCredenciales = binding.ckbxGuardarCredenciales

        binding.btnRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            accionBotonLogin()
        }

        binding.btnRecuperarClave.setOnClickListener {
            startActivity(Intent(this, CambiarClaveActivity::class.java))
        }
    }

    /**
     * Prueba el token mediante una llamada al servidor
     */
    private fun probarToken() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                val token = sharedPreferences.getString(Constantes.ACCESS_TOKEN_SHARED_PREFERENCES, "")!!

                val call = UrlServidor.getRetrofit().create(APIService::class.java).probarToken(token = "Bearer " + token)

                if (call.isSuccessful) {
                    Log.d("Debug", "Token aceptado")
                    irMenuPrincipal()
                } else {
                    Log.d("Debug", "Token no valido")
                }
            }
            catch (e: Exception) {
                e.message?.let { Log.e("Error", it) }
                e.printStackTrace()
            }

            Log.d("Debug", "He probado el token")

        }

    }

    /**
     * Accion del boton loggin
     */
    private fun accionBotonLogin() {
        if (etContrasenia.editText!!.text.isNotBlank() && etEmail.editText!!.text.isNotBlank()) {
            loguear(etEmail.editText!!.text.toString(), MD5.encriptar(etContrasenia.editText!!.text.toString()))
        } else {
            Toast.makeText(this, "Faltan campos por rellenar.", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Busca si se han guardado datos con anterioridad
     */
    private fun buscarDatos() {
        try {
            val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            val emailGuardado = sharedPreferences.getString(Constantes.EMAIL_SHARED_PREFERENCES, "")

            if (emailGuardado != null && emailGuardado != "") {
                etEmail.editText!!.setText(emailGuardado.toString())
            }
        }
        catch (e: Exception) {
            Log.e("Error", "Error al sacar datos de SharedPreferences ${e.message}")
        }
    }

    /**
     * Hace login
     * @param email correo del usuario
     * @param pass clave cifrada por MD5 del usuario
     */
    private fun loguear(email: String, pass: String) {
        CoroutineScope(Dispatchers.IO).launch {
            var msg: String  = ""
            try {
                val call = UrlServidor.getRetrofit().create(APIService::class.java).login(email, pass)
                if (call.isSuccessful) {
                    val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    editor.apply {
                        if (ckbxGuardarCredenciales.isChecked) {
                            putString(Constantes.EMAIL_SHARED_PREFERENCES, email)
                        }

                        putString(Constantes.ACCESS_TOKEN_SHARED_PREFERENCES, call.body()?.token.toString())
                    }.apply()

                    msg = "Bienvenido $email"

                    irMenuPrincipal()

                }
                else {
                    msg = "Usuario o contraseña incorrectos."
                    etContrasenia.editText!!.text.clear()
                }
            }
            catch (e: Exception) {
                msg = "Error de acceso."
                Log.e("Error", "${e.message}")
                e.printStackTrace()
            }
            runOnUiThread {
                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Lleva al menu principal
     */
    private fun irMenuPrincipal() {
        cargarParadas()
        startActivity(Intent(applicationContext, MenuPrincipalActivity::class.java))
    }

    /**
     * Carga las paradas de la app
     */
    private fun cargarParadas() {

        CoroutineScope(Dispatchers.Main).launch {
            val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            val call = UrlServidor.getRetrofit().create(APIService::class.java).getListaParadas(
                "Bearer " + sharedPreferences.getString("accessToken", "").toString()
            )

            if (call.isSuccessful) {
                try {
                    Paradas.listaParadas = call.body()
                    Log.d("Debug", "Paradas cargadas")
                } catch (e: Exception) {
                    Log.e("Error", "No se han podido cargar las paradas")
                }
                Log.d("Debug", "RV actualizados")
            } else {
                Log.e("Debug", "Error al buscar cargar paradas")
            }
        }
    }

    /**
     * Muestra mensaje a la hora de darle al botón de atrás
     */
    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setMessage("¿Quieres cerrar la aplicación?")
            .setPositiveButton("Aceptar", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    finishAffinity()
                }
            }).setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Limpia los editText
     */
    private fun limpiarEditText() {
        etEmail.editText!!.text.clear()
        etContrasenia.editText!!.text.clear()
    }
}