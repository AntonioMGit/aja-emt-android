package com.example.proyectoemtaja.menuPrincipal.ajustes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.proyectoemtaja.ActualizarActivity
import com.example.proyectoemtaja.LoginActivity
import com.example.proyectoemtaja.config.MD5
import com.example.proyectoemtaja.databinding.FragmentAjustesBinding
import com.example.proyectoemtaja.utilities.Constantes

/**
 * Fragment de ajustes
 */
class AjustesFragment : Fragment() {

    /**
     * Binding de la interfaz
     */
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

        _binding!!.btnAyuda.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://edu-appemtaja.odoo.com/")))
        }
        return root
    }

    /**
     * Cambia a la actividad ActualizarActivity.kt
     */
    private fun irActualizar() {
        startActivity(Intent(context,ActualizarActivity::class.java))
    }

    /**
     * Cambia a la actividad LoginActivity.kt
     */
    private fun irLogin() {

        val sharedPreferences = requireActivity().getSharedPreferences(Constantes.NOMBRE_FICHERO_SHARED_PREFERENCES, Context.MODE_PRIVATE)

        sharedPreferences.edit().apply{
            putString(Constantes.ACCESS_TOKEN_SHARED_PREFERENCES, "")
        }.commit()

        startActivity(Intent(context,LoginActivity::class.java))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}