package com.example.proyectoemtaja

import android.Manifest
import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.proyectoemtaja.databinding.ActivityMenuPrincipalBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView

/**
 * Actividad con navegador inferior.
 * Tiene tres fragmentos asociados:
 *  - AjustesFragment.kt
 *  - FavoritoFragment.kt
 *  - MapaFragment.kt
 */
class MenuPrincipalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPrincipalBinding


    /**
     * Permisos de localizacion
     */
    private val REQUEST_LOCATION_PERMISSION = 1

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

        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    override fun onBackPressed() {
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