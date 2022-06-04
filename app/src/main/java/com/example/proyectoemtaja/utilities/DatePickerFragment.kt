package com.example.proyectoemtaja.utilities

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

/**
 * Recibe una funcion con tres parametros. Extiende de DialogFragment (Crea una vista de ese tipo).
 * Implementa OnDateSetListener (avisa cuando se selecciona una fecha en el calendario)
 */
class DatePickerFragment(val listener: (day: Int, month: Int, year: Int) -> Unit) :
    DialogFragment(), DatePickerDialog.OnDateSetListener {

    /**
     * Crear el dialogo que tiene que ver el usuario con la fecha (el calendario)
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance() //Para obtener la fecha actual
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        //Se le pasa el contexto, el listener, anio, mes y dia
        val picker = DatePickerDialog(activity as Context, this, year, month, day)


        //Para que empiece en lunes (1:Domingo, 2:Lunes, 3:Martes, 4:Miercoles, 5:Jueves, 6:Viernes, 7:Sabado)
        picker.datePicker.firstDayOfWeek = 2

        return picker
    }

    /**
     * Cuando el usuario seleccione una fecha se va a llamar a este metodo
     */
    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        //Este listener ejecutara el codigo de la funcion showDatePickerDialog del MainActivity
        listener(dayOfMonth, month, year );
    }

}