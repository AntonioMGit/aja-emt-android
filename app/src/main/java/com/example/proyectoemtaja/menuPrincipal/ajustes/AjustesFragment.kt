package com.example.proyectoemtaja.menuPrincipal.ajustes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.proyectoemtaja.ActualizarActivity
import com.example.proyectoemtaja.LoginActivity
import com.example.proyectoemtaja.databinding.FragmentAjustesBinding
import com.example.proyectoemtaja.utilities.Constantes

class AjustesFragment : Fragment() {

    private var _binding: FragmentAjustesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAjustesBinding.inflate(inflater, container, false)
        val root: View = binding.root


        _binding!!.btnCerrarSesion.setOnClickListener {
            irLogin()
        }
        _binding!!.btnIrActualizar.setOnClickListener {
            irActualizar()
        }
        return root
    }

    private fun irActualizar() {
        startActivity(Intent(context,ActualizarActivity::class.java))
    }

    private fun irLogin() {
        startActivity(Intent(context,LoginActivity::class.java))
        val sharedPreferences = requireActivity().getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        //FIXME:No se si funciona
        sharedPreferences.edit().clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}