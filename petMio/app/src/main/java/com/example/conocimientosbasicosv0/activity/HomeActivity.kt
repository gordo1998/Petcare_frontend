package com.example.conocimientosbasicosv0.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.fragment.FavoritosFragment
import com.example.conocimientosbasicosv0.fragment.MascotasDuenoFragment
import com.example.conocimientosbasicosv0.fragment.MascotasFragment
import com.example.conocimientosbasicosv0.fragment.PerfilFragment
import com.example.conocimientosbasicosv0.fragment.ReservasFragment
import com.example.conocimientosbasicosv0.fragment.ServiciosFragment
import com.example.conocimientosbasicosv0.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    lateinit var navegation: BottomNavigationView
    private val onNavMenu = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.iconoServicios -> {
                supportFragmentManager.commit {
                    replace<ServiciosFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }

            R.id.iconoMascotas -> {
                supportFragmentManager.commit {
                    replace<MascotasDuenoFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }

            R.id.iconoReservas -> {
                supportFragmentManager.commit {
                    replace<ReservasFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }

            R.id.iconoFavoritos -> {
                supportFragmentManager.commit {
                    replace<FavoritosFragment>(R.id.frameContainer)
                    setReorderingAllowed(true)
                    addToBackStack("replacement")
                }
                return@OnNavigationItemSelectedListener true
            }

            R.id.iconoPerfil -> {
                supportFragmentManager.commit {
                    replace<PerfilFragment>(R.id.frameContainer)
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

        // Verificar si el usuario está logueado
        val sessionManager = SessionManager(this)
        Log.d("SESSION MANAGER", "${sessionManager.getIdCuenta()}")
        if (!sessionManager.isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Redirigir a HomeCuidadorActivity si el usuario es cuidador
        val cuenta = sessionManager.getLoggedInAccount()
        if (cuenta?.tipoPerfil == 1.toByte()) {
            val intent = Intent(this, HomeCuidadorActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_home)

        navegation = findViewById(R.id.navMenu)
        navegation.setOnNavigationItemSelectedListener(this.onNavMenu)

        // Verifica si se debe mostrar el fragmento de creación de mascota
        if (intent.getBooleanExtra("SHOW_CREAR_MASCOTA", false)) {
            supportFragmentManager.commit {
                replace<MascotasFragment>(R.id.frameContainer)
                setReorderingAllowed(true)
                addToBackStack("crear_mascota")
            }
            navegation.selectedItemId = R.id.iconoMascotas // Actualizar la selección del menú si lo deseas
        } else if (savedInstanceState == null) {
            // Cargar fragmento por defecto
            supportFragmentManager.commit {
                replace<ServiciosFragment>(R.id.frameContainer)
                setReorderingAllowed(true)
                addToBackStack("default")
            }
            navegation.selectedItemId = R.id.iconoServicios // Actualizar la selección del menú
        }
    }


}
