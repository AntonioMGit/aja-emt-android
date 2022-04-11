package com.example.proyectoemtaja

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.proyectoemtaja.databinding.ActivityLoginBinding
import com.example.proyectoemtaja.databinding.ActivityMapsBinding
import com.example.proyectoemtaja.service.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var etUser:EditText
    private lateinit var etPassword:EditText
    private lateinit var textview:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        etPassword=binding.password
        etUser=binding.user
        textview=binding.textView

        binding.btnLogin.setOnClickListener {

            if(!etPassword.text.isBlank()&&!etUser.text.isBlank()){
                CoroutineScope(Dispatchers.IO).launch {
                   var call= getRetrofit().create(APIService::class.java)
                        .login(etUser.toString(), etPassword.toString())

                    runOnUiThread {
                        if (call.isSuccessful) {
                            textview.text = call.body()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "fallo de conexion",
                                Toast.LENGTH_LONG
                            )
                        }
                    }
                }
            }

        }


    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("http://192.168.1.34:8081/login/").addConverterFactory(
            GsonConverterFactory.create()).build()

    }
}