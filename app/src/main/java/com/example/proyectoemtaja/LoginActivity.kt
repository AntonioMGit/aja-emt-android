package com.example.proyectoemtaja

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectoemtaja.config.MD5
import com.example.proyectoemtaja.databinding.ActivityLoginBinding
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.utilities.Variables
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding


    private lateinit var etEmail: EditText
    private lateinit var etContrasenia: EditText
    private lateinit var textview: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        etContrasenia = binding.etContrasenia
        etEmail = binding.etEmail
        textview = binding.textView

        //funcion que busca si hay datos del usuario guardados para saltarse el login
        //si no hay datos no hace nada
        //si hay datos loguea con los datos directamente
        //buscarDatos()

        binding.btnRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            if (etContrasenia.text.isNotBlank() && etEmail.text.isNotBlank()) {
                loguear(etEmail.text.toString(), MD5.encriptar(etContrasenia.text.toString()))

            } else {
                Toast.makeText(this, "Faltan campos por rellenar.", Toast.LENGTH_LONG).show()
            }
        }
    }

    /*
    private fun buscarDatos() {
        val sharedPreferences = getSharedPreferences(Variables.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
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
                    //textview.text = call.body().toString()

                    //Guardar las cosas en shared preferences
                    val sharedPreferences = getSharedPreferences(Variables.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.apply {
                        putString(Variables.EMAIL_SHARED_PREFERENCES, email)
                        putString(Variables.PASSWORD_SHARED_PREFERENCES, MD5.encriptar(pass))
                        putString(Variables.ACCESS_TOKEN_SHARED_PREFERENCES, call.body()?.token.toString())
                        //putString("refreshToken", call.body()?.token.toString())
                    }.apply()

                    msg = "Bienvenido $email"

                    startActivity(Intent(applicationContext, FavoritoActivity::class.java))

                } else if (call.code() == 401){
                    msg = "Usuario o contraseña incorrectos."
                    etContrasenia.text.clear()
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

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(Variables.URL_BASE)
        /*.addConverterFactory(
            NullOnEmptyConverterFactory()
        )
        */
        .addConverterFactory(
            GsonConverterFactory.create()
        ).build()

    }
}