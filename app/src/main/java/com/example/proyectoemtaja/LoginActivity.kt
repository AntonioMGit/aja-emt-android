package com.example.proyectoemtaja

import android.R.attr.password
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        binding.btnRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {

            if (etContrasenia.text.isNotBlank() && etEmail.text.isNotBlank()) {
                CoroutineScope(Dispatchers.IO).launch {
                    /*var request :RequestBody= MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("correo", etEmail.toString())
                        .addFormDataPart("password",etContrasenia.toString())
                        .build()
*/

                    var call = getRetrofit().create(APIService::class.java)
                        .login(LoginRequest(etEmail.toString(), etContrasenia.toString()))
                    //.login(etEmail.toString(), etContrasenia.toString())

                    runOnUiThread {
                        if (call.isSuccessful) {
                            textview.text = call.body().toString()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "fallo de conexion",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }


                }
            } else {
                Toast.makeText(this, "Faltan campos por rellenar.", Toast.LENGTH_LONG).show()
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