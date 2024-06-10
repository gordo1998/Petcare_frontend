package com.example.conocimientosbasicosv0.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.conocimientosbasicosv0.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import android.widget.ImageButton
import com.example.conocimientosbasicosv0.adapter.CuidadoresAdapter
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.model.Cuidador

class CuidadoresActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuidadores)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Configuración inicial del RecyclerView con un adaptador vacío.
        val recyclerView = findViewById<RecyclerView>(R.id.cuidadoresRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val servicioId = intent.getIntExtra("EXTRA_SERVICIO_ID", -1)
        Log.e("CuidadorActivity", "SERVICIO ${servicioId}")
        recyclerView.adapter = CuidadoresAdapter(emptyList(), servicioId)



        if (servicioId != -1) {
            cargarCuidadores(servicioId)
        } else {
            Log.e("CuidadoresActivity", "Error: Servicio ID no encontrado")
        }
    }

    private fun cargarCuidadores(servicioId: Int) {
        RetrofitClient.create().getServiciosCuidador(servicioId).enqueue(object : Callback<Map<String, List<String>>> {
            override fun onResponse(call: Call<Map<String, List<String>>>, response: Response<Map<String, List<String>>>) {
                if (response.isSuccessful) {
                    val resultadoMap = response.body() ?: emptyMap()
                    val cuidadoresList = resultadoMap.map { (nombreCompleto, detalles) ->
                        Cuidador(
                            nombre = detalles.getOrNull(0) ?: "",
                            apellido1 = detalles.getOrNull(1) ?: "",
                            apellido2 = detalles.getOrNull(2) ?: "",
                            idCuidador = detalles.getOrNull(3)?.toIntOrNull() ?: 0,
                            username = detalles.getOrNull(4) ?: "",
                            descripcionCorta = detalles.getOrNull(5) ?: "",
                            descripcionLarga = detalles.getOrNull(6) ?: "",
                            mascotas = detalles.drop(7) // las mascotas empiezan desde el índice 7 en adelante
                        )
                    }

                    val recyclerView = findViewById<RecyclerView>(R.id.cuidadoresRecyclerView)
                    recyclerView.adapter = CuidadoresAdapter(cuidadoresList, servicioId)
                } else {
                    Log.e("CuidadoresActivity", "Error al obtener cuidadores, código de respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Map<String, List<String>>>, t: Throwable) {
                Log.e("CuidadoresActivity", "Fallo al obtener los cuidadores", t)
            }
        })
    }
}







