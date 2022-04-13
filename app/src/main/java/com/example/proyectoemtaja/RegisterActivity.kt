package com.example.proyectoemtaja

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.proyectoemtaja.config.MD5
import com.example.proyectoemtaja.databinding.ActivityRegisterBinding
import com.example.proyectoemtaja.models.usuario.Sexo
import com.example.proyectoemtaja.models.usuario.Usuario
import com.example.proyectoemtaja.service.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate

class RegisterActivity : AppCompatActivity() {

    private lateinit var spSexo: Spinner

    private lateinit var etFechaNacimiento: EditText
    private lateinit var sexo:Sexo
    private lateinit var fechaSeleccionada: LocalDate
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var  correo:EditText
    private lateinit var  password1:EditText
    private lateinit var  password2:EditText
    private lateinit var  nombre:EditText
    private lateinit var  apellidos:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSpinner()



        etFechaNacimiento = binding.etFechaNacimiento//findViewById(R.id.etFechaNacimiento)

       correo= binding.etCorreo
        password1=binding.etPassword
        password2=binding.etPassword2
        nombre=binding.etNombre
        apellidos=binding.etApellidos
/*
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
*/
        //fechaSeleccionada = LocalDate.now()

        //Listener de la fecha
        etFechaNacimiento.setOnClickListener { showDatePickerDialog() }

        //lo pongo para probar que lo demas funciona
        //habria que quitarlo

        binding.button.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.btnRegistrar.setOnClickListener {

            if(correo.text.isNotBlank()&&password1.text.isNotBlank()&&password2.text.isNotBlank()&&nombre.text.isNotBlank()&&apellidos.text.isNotBlank()){
                if(password1.text.equals(password2.text)){
                    CoroutineScope(Dispatchers.IO).launch {
                       val call= getRetrofit().create(APIService::class.java).insertUsuario(
                            Usuario(
                                correo.text.toString(),
                                MD5.encriptar(password1.text.toString()),
                                nombre.text.toString(),
                                apellidos.text.toString(),
                                LocalDate.now(),
                                sexo
                            )
                        )
                        runOnUiThread {
                            if (call.isSuccessful) {

                                Toast.makeText(applicationContext, "INSERTADO CORECTAMENTE", Toast.LENGTH_LONG)

                            }else{
                                Toast.makeText(applicationContext, "INSERTADO CORECTAMENTE", Toast.LENGTH_LONG)

                            }
                        }
                    }
                }else{
                    Toast.makeText(this,"Las contraseñas no coinciden",Toast.LENGTH_LONG)
                    //TODO: poner a vacio contrasenias
                }

            }else{
                Toast.makeText(this,"Rellena todos lo campos",Toast.LENGTH_LONG)
            }

        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("http://192.168.1.41:8080/").addConverterFactory(
            GsonConverterFactory.create()).build()

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

    fun initSpinner() {
        spSexo = binding.spSexo//findViewById(R.id.spSexo)
        val list= Sexo.values()
        val adaptador=ArrayAdapter(this,android.R.layout.simple_spinner_item,list)
        spSexo.adapter=adaptador

        spSexo.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sexo= list[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                sexo = Sexo.MUJER
            }

        }
    }


}