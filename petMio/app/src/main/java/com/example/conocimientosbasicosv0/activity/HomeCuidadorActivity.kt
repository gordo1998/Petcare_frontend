package com.example.conocimientosbasicosv0.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.fragment.UpdateCuidadorFragment

import com.example.conocimientosbasicosv0.fragment.PerfilFragment
import com.example.conocimientosbasicosv0.fragment.ReservasCuidadorFragment
import com.example.conocimientosbasicosv0.fragment.ReservasFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeCuidadorActivity : AppCompatActivity() {

    lateinit var navegationCuidador: BottomNavigationView
    private val onNavMenuCuidador = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.iconoReservas -> {
                supportFragmentManager.commit {
                    replace<ReservasCuidadorFragment>(R.id.frameContainerCuidador)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }

           /* R.id.iconoChat -> {
                supportFragmentManager.commit {
                    replace<ChatFragment>(R.id.frameContainerCuidador)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }*/

            R.id.iconoConfiguracion -> {
                supportFragmentManager.commit {
                    replace<UpdateCuidadorFragment>(R.id.frameContainerCuidador)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }

            R.id.iconoPerfil -> {
                supportFragmentManager.commit {
                    replace<PerfilFragment>(R.id.frameContainerCuidador)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_cuidador)

        navegationCuidador = findViewById(R.id.navMenuCuidador)
        navegationCuidador.setOnNavigationItemSelectedListener(this.onNavMenuCuidador)

        // Cargar fragmento por defecto
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace<ReservasFragment>(R.id.frameContainerCuidador)
                setReorderingAllowed(true)
                addToBackStack("default")
            }
            navegationCuidador.selectedItemId = R.id.iconoReservas // Actualizar la selección del menú
        }
    }
}