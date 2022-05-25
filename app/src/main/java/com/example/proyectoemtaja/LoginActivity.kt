package com.example.proyectoemtaja

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoemtaja.config.MD5
import com.example.proyectoemtaja.databinding.ActivityLoginBinding
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.utilities.Constantes
import com.example.proyectoemtaja.utilities.UrlServidor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.system.exitProcess


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding


    private lateinit var etEmail: TextInputLayout
    private lateinit var etContrasenia: TextInputLayout

    private lateinit var ckbxGuardarCredenciales: CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        probarToken()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        etContrasenia = binding.etContrasenia
        etEmail = binding.etEmail

        ckbxGuardarCredenciales = binding.ckbxGuardarCredenciales

        //funcion que busca si hay datos del usuario guardados para saltarse el login
        //si no hay datos no hace nada
        //si hay datos loguea con los datos directamente
        //buscarDatos()

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

    private fun probarToken() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                val token = sharedPreferences.getString(Constantes.ACCESS_TOKEN_SHARED_PREFERENCES, "")!!

                val call = getRetrofit().create(APIService::class.java).probarToken(token = "Bearer " + token)

                if (call.isSuccessful) {
                    Log.d("Debug", "Token aceptado")
                    irMenuPrincipal()
                } else {
                    Log.d("Debug", "Token no valido")
                    //buscarDatos()
                }
            }
            catch (e: Exception) {
                e.message?.let { Log.e("Error", it) }
                e.printStackTrace()
            }

            Log.d("Debug", "He probado el token")

        }

    }

    private fun accionBotonLogin() {
        if (etContrasenia.editText!!.text.isNotBlank() && etEmail.editText!!.text.isNotBlank()) {
            loguear(etEmail.editText!!.text.toString(), MD5.encriptar(etContrasenia.editText!!.text.toString()))
        } else {
            Toast.makeText(this, "Faltan campos por rellenar.", Toast.LENGTH_LONG).show()
        }
    }

    /*
    private fun buscarDatos() {
        val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val emailGuardado = sharedPreferences.getString("email", "")
        val passGuardada = sharedPreferences.getString("pass", "")

        if (emailGuardado != null && passGuardada != null && emailGuardado != "" && passGuardada != "" ) {
            loguear(emailGuardado, passGuardada)
        }
    }
    */
    private fun loguear(email: String, pass: String) {
        CoroutineScope(Dispatchers.IO).launch {
            var msg: String  = ""
            try {
                val call = getRetrofit().create(APIService::class.java).login(email, pass)
                if (call.isSuccessful) {

                    val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    editor.apply {
                        putString(Constantes.EMAIL_SHARED_PREFERENCES, email)

                        putString(Constantes.PASSWORD_SHARED_PREFERENCES,
                            if (ckbxGuardarCredenciales.isChecked) {
                                MD5.encriptar(pass)
                            }
                            else {
                                ""
                            })
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

    private fun irMenuPrincipal() {
        cargarParadas()
        startActivity(Intent(applicationContext, MenuPrincipalActivity::class.java))
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(UrlServidor.URL_BASE)/*.addConverterFactory(NullOnEmptyConverterFactory()) */
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    private fun cargarParadas() {

        CoroutineScope(Dispatchers.Main).launch {
            val sharedPreferences = getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            val call = getRetrofit().create(APIService::class.java).getListaParadas(
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

    override fun onBackPressed() {
        exitByBackKey()
    }

    private fun exitByBackKey() {
        MaterialAlertDialogBuilder(this)
            .setMessage("¿Quieres cerrar la aplicación?")
            .setPositiveButton("Aceptar", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    finish()
                }
            }).setNegativeButton("Cancelar", null)
            .show()
    }

    private fun limpiarEditText() {
        etEmail.editText!!.text.clear()
        etContrasenia.editText!!.text.clear()
    }
}