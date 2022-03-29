package com.example.proyectoemtaja

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.proyectoemtaja.service.APIService
import com.example.proyectoemtaja.databinding.ActivityMainBinding
import com.example.proyectoemtaja.models.TimeArrivalBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        searchParada("5111")
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("http://noMeDoxeesPLS:8080/prueba/").addConverterFactory(
            GsonConverterFactory.create()).build()

    }

    private fun searchParada(parada:String){

        CoroutineScope(Dispatchers.IO).launch {

            val call= getRetrofit().create(APIService::class.java).getTimeArrivalBus("consultar/$parada/")
            val timeArrivalBus = call.body()

            if (call.isSuccessful){
                runOnUiThread {
                    binding.textw.text = timeArrivalBus.toString()
                }

            }else{
                Log.d("pruebaaaaaaa","errorrrrr")
            }
        }

    }
}