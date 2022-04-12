package com.example.proyectoemtaja

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import java.time.LocalDate

class RegisterActivity : AppCompatActivity() {

    private lateinit var spSexo: Spinner
    private lateinit var btnRegistrar: Button
    private lateinit var boton: Button
    private lateinit var etFechaNacimiento: EditText

    private lateinit var fechaSeleccionada: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        spSexo = findViewById(R.id.spSexo)

        btnRegistrar = findViewById(R.id.btnRegistrar)

        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)

        boton = findViewById(R.id.button)

        ArrayAdapter.createFromResource(
            this,
            R.array.sexo,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spSexo.adapter = adapter
        }

        //fechaSeleccionada = LocalDate.now()

        //Listener de la fecha
        etFechaNacimiento.setOnClickListener { showDatePickerDialog() }

        //lo pongo para probar que lo demas funciona
        //habria que quitarlo
        boton.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    //Inicializar el dialogo creado en onCreateDialog de la clase DatePickerDialog
    private fun showDatePickerDialog() {
        //Crear un objeto de la clase DatePickerDialog
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    //Funcion que se le pasa a DatePickerDialog al crearlo. Cuando ya se ha seleccionado una fecha, se llama a este metodo
    private fun onDateSelected(day:Int, month:Int, year:Int){
        etFechaNacimiento.setText("$day/"+ (month+1) + "/$year")
        fechaSeleccionada = LocalDate.of(year, (month+1), day)

        Toast.makeText(this, "Fecha: " + etFechaNacimiento.text, Toast.LENGTH_LONG).show()
    }
}