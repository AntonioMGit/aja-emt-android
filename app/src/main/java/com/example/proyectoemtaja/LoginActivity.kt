package com.example.proyectoemtaja

import android.R.attr.password
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
import com.example.proyectoemtaja.models.peticiones.LoginRequest
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.utilities.NullOnEmptyConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
        buscarDatos()

        binding.btnLogin.setOnClickListener {

            if (etContrasenia.text.isNotBlank() && etEmail.text.isNotBlank()) {
                loguear(etContrasenia.text.toString(), etEmail.text.toString())
            } else {
                Toast.makeText(this, "Faltan campos por rellenar.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun buscarDatos() {
        val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val emailGuardado = sharedPreferences.getString("email", "")
        val passGuardada = sharedPreferences.getString("pass", "")

        if(emailGuardado!="" && passGuardada!=""){
            if (emailGuardado != null) { //sino no deja¿?¿?
                if (passGuardada != null) { //sino no deja¿?¿?
                    loguear(emailGuardado, passGuardada)
                    Toast.makeText(applicationContext, "Bienvenido " + emailGuardado, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loguear(pass: String, email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            /*var request :RequestBody= MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("correo", etEmail.toString())
                .addFormDataPart("password",etContrasenia.toString())
                .build()
            */

            var call = getRetrofit().create(APIService::class.java)
                .login(LoginRequest(email, pass))

            runOnUiThread {
                if (call.isSuccessful) {
                    textview.text = call.body().toString()

                    //hace lo que tenga que hacer con el servidor

                    //coge el usuario y la contraseña ENCRIPTADA para evitarse tener que meter el usuario y la contraseña todo el tiempo
                    //y asi loguear directamente hasta que de al boton de cerrar sesion
                    val sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.apply{
                        putString("email", email)
                        putString("pass", MD5.encriptar(pass))
                    }.apply()

                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "fallo de conexion",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("http://192.168.1.34:8081/")/*.addConverterFactory(
            NullOnEmptyConverterFactory()
        )*/.addConverterFactory(
            GsonConverterFactory.create()
        ).build()

    }
}