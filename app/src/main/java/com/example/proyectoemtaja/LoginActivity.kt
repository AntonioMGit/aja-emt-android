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
import com.example.proyectoemtaja.utilities.NullOnEmptyConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.HashMap

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
/*
                     var params:HashMap<String,String>
                     params= HashMap()
                    params.put("user",etUser.toString())
                    params.put("password",etPassword.toString())
*/

                    try {

                        var call = getRetrofit().create(APIService::class.java)//.login(params)
                            .login(etUser.toString(), etPassword.toString())

                            if (call.isSuccessful) {
                                runOnUiThread {
                                    textview.text = call.body().toString()
                                }
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "fallo de conexion",
                                    Toast.LENGTH_LONG
                                )
                            }

                    }catch (e:Exception){
                        e.printStackTrace()


                    }
                }
            }

        }


    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("http://192.168.1.34:8081/").addConverterFactory(NullOnEmptyConverterFactory()).addConverterFactory(
            GsonConverterFactory.create()).build()

    }
}