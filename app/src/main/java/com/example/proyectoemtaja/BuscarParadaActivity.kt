package com.example.proyectoemtaja

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoemtaja.databinding.ActivityBuscarParadaBinding
import com.example.proyectoemtaja.models.peticiones.Favorito
import java.util.*

class BuscarParadaActivity : AppCompatActivity() {

    val listaTemporal: ArrayList<Favorito> = ArrayList<Favorito>()
    private lateinit var rvBuscarParadas: RecyclerView
    private lateinit var view: View

    private lateinit var _binding: ActivityBuscarParadaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBuscarParadaBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        rvBuscarParadas = _binding.rvBuscarParada
        rvBuscarParadas.layoutManager = LinearLayoutManager(this)
        val adapter = FavoritoAdapter(listaTemporal)
        adapter.setOnItemClickListener(object : FavoritoAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                buscarParada(position)
            }
        })
        rvBuscarParadas.adapter = adapter

        view = _binding.imgError
    }

    private fun buscarParada(pos: Int) {
        val parada = listaTemporal[pos]

        val intent = Intent(this, MainActivity::class.java)

        intent.putExtra("nParada", parada.idParada)

        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_search_bar, menu)
        val item = menu?.findItem(R.id.search_action)
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                listaTemporal.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())

                if (searchText.isNotEmpty()) {
                    if (searchText[0].isDigit()) {
                        Paradas.listaParadas!!.data.forEach {
                            if (it.node.contains(searchText)){
                                listaTemporal.add(Favorito(it.node, it.name))
                            }
                        }
                    }
                    else {
                        Paradas.listaParadas!!.data.forEach {
                            if (it.name.lowercase(Locale.getDefault()).contains(searchText)){
                                listaTemporal.add(Favorito(it.node, it.name))
                            }
                        }
                    }
                }
                else {
                    listaTemporal.clear()
                }
                rvBuscarParadas.adapter!!.notifyDataSetChanged()

                view.visibility = if (listaTemporal.isEmpty()) {
                    View.VISIBLE
                }
                else {
                    View.INVISIBLE
                }

                return false
            }


        })

        return super.onCreateOptionsMenu(menu)
    }
}