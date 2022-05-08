package com.example.proyectoemtaja

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.proyectoemtaja.databinding.ActivityFavoritoBinding
import com.example.proyectoemtaja.databinding.ActivityMainBinding
import com.example.proyectoemtaja.databinding.ActivityMapsBinding

class FavoritoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritoBinding
    private lateinit var btnBuscarParadaFav: Button
    private lateinit var etNumParadaFav: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorito)

        binding = ActivityFavoritoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnBuscarParadaFav = binding.btnBuscarParadaFav
        etNumParadaFav = binding.etNumParadaFav

        btnBuscarParadaFav.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java);
            var nParada = etNumParadaFav.text.toString()
            intent.putExtra("nParada", nParada)

            startActivity(intent);
        }
    }
}