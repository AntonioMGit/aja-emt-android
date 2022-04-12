package com.example.proyectoemtaja

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.proyectoemtaja.databinding.ActivityLoginBinding
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.utilities.NullOnEmptyConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding


    private lateinit var etEmail:EditText
    private lateinit var etContrasenia:EditText
    private lateinit var textview:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        etContrasenia=binding.etContrasenia
        etEmail=binding.etEmail
        textview=binding.textView

        binding.btnRegistrarse.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {

            if(!etContrasenia.text.isBlank()&&!etEmail.text.isBlank()){
                CoroutineScope(Dispatchers.IO).launch {
                   var call= getRetrofit().create(APIService::class.java)
                        .login(etEmail.toString(), etContrasenia.toString())

                    runOnUiThread {
                        if (call.isSuccessful) {
                            textview.text = call.toString()
                        } else {
                            Toast.makeText(this@LoginActivity, "fallo de conexion", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }else{
                Toast.makeText(this, "Faltan campos por rellenar.", Toast.LENGTH_LONG).show()
            }

        }


    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("http://192.168.1.34:8081").addConverterFactory(NullOnEmptyConverterFactory()).addConverterFactory(
            GsonConverterFactory.create()).build()

    }
}