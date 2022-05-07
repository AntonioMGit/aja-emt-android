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
import com.example.proyectoemtaja.utilities.Variables
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

        binding.btnRegistrarse.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            if (etContrasenia.text.isNotBlank() && etEmail.text.isNotBlank()) {
                loguear(etEmail.text.toString(),  MD5.encriptar(etContrasenia.text.toString()))
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
                    //QUITAR
                    Toast.makeText(applicationContext, "Bearer " + sharedPreferences.getString("accessToken", ""), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loguear(email: String, pass: String) {
        CoroutineScope(Dispatchers.IO).launch {
            /*var request :RequestBody= MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("correo", etEmail.toString())
                .addFormDataPart("password",etContrasenia.toString())
                .build()
            */

           var call = getRetrofit().create(APIService::class.java)
                .login(email, pass)

            runOnUiThread {
                if (call.isSuccessful) {

                    if(call.code() == 200) {
                        textview.text = call.body().toString()

                        //hace lo que tenga que hacer con el servidor

                        //coge el usuario y la contraseña ENCRIPTADA para evitarse tener que meter el usuario y la contraseña todo el tiempo
                        //y asi loguear directamente hasta que de al boton de cerrar sesion
                        val sharedPreferences =
                            getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.apply {
                            putString("email", email)
                            putString("pass", MD5.encriptar(pass))
                            putString("accessToken", call.body()?.token.toString())
                            putString("refreshToken", call.body()?.token.toString())
                        }.apply()

                        Toast.makeText(applicationContext, "Bienvenido " + email, Toast.LENGTH_SHORT).show()

                    }else{
                        Toast.makeText(applicationContext, "Usuario o contraseña no válidos", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error de conexión",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(Variables.urlBase)/*.addConverterFactory(
            NullOnEmptyConverterFactory()
        )*/.addConverterFactory(
            GsonConverterFactory.create()
        ).build()

    }
}