package com.example.proyectoemtaja

import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.proyectoemtaja.databinding.ActivityMenuPrincipalBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView

class MenuPrincipalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_menu_principal)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_favoritos, R.id.navigation_mapa, R.id.navigation_ajustes
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        exitByBackKey()
    }

    private fun exitByBackKey() {
        MaterialAlertDialogBuilder(this)
            .setMessage("¿Quieres volver al login?\nSe cerrará sesión.")
            .setPositiveButton("Aceptar", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    finish()
                }
            }).setNegativeButton("Cancelar", null)
            .show()
    }

}