package com.example.conocimientosbasicosv0.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.adapter.MascotasAdapter
import com.example.conocimientosbasicosv0.api.RetrofitClient
import com.example.conocimientosbasicosv0.model.MascotaId
import com.example.conocimientosbasicosv0.model.MascotaInfo
import com.example.conocimientosbasicosv0.model.ReservaMascotaRequest
import com.example.conocimientosbasicosv0.model.ReservaRequest
import com.example.conocimientosbasicosv0.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CuidadorProfileActivity : AppCompatActivity() {

    private var cuidadorId: Int = 0
    private lateinit var sessionManager: SessionManager
    private lateinit var mascotasRecyclerView: RecyclerView
    private lateinit var mascotasAdapter: MascotasAdapter
    private lateinit var solicitarReservaButton: Button
    private lateinit var addToFavoritesButton: ImageButton
    private var esFavorito: Boolean = false
    private var selectedMascotas = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuidador_profile)

        sessionManager = SessionManager(this)
        val dueñoId = sessionManager.getIdCuenta()

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { finish() }

        val username = intent.getStringExtra("username")
        val nombre = intent.getStringExtra("nombre")
        val apellido1 = intent.getStringExtra("apellido1")
        val apellido2 = intent.getStringExtra("apellido2")
        val descripcionLarga = intent.getStringExtra("descripcionLarga")
        var servicioId = intent.getIntExtra("servicioId", 0)
        Log.e("CuidadorProfileActivity", "SERVICIO ${servicioId}")


        cuidadorId = intent.getIntExtra("id", 0)

        findViewById<TextView>(R.id.usernameTextView).text = username
        findViewById<TextView>(R.id.nombreApellidoTextView).text = "$nombre $apellido1 $apellido2"
        findViewById<TextView>(R.id.descripcionLargaTextView).text = descripcionLarga

        mascotasRecyclerView = findViewById(R.id.mascotasRecyclerView)
        mascotasRecyclerView.layoutManager = LinearLayoutManager(this)


        solicitarReservaButton = findViewById(R.id.solicitarReservaButton)
        addToFavoritesButton = findViewById(R.id.addToFavoritesButton)


        cargarMascotas(dueñoId)
        verificarSiCuidadorEsFavorito(dueñoId, cuidadorId)

        solicitarReservaButton.setOnClickListener {
            Log.e("CuidadorProfileActivity", "has pulsado solicitar reservas button")

            selectedMascotas = mascotasAdapter.getSelectedMascotas().toMutableList()
            if (selectedMascotas.isEmpty()) {
                Log.e("CuidadorProfileActivity", "MASCOTAS VACIAS ${selectedMascotas}")
                Toast.makeText(this, "Selecciona al menos una mascota", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("CuidadorProfileActivity", "MASCOTAS ${selectedMascotas}")
                Log.e("CuidadorProfileActivity", "SERVICIO ${servicioId}")
                if (servicioId != null) {
                    solicitarReserva(cuidadorId, dueñoId, servicioId.toInt(), selectedMascotas)
                }

            }
        }

        addToFavoritesButton.setOnClickListener {
            addOrRemoveCuidadorFavorito(dueñoId, cuidadorId)
        }
    }

    private fun cargarMascotas(dueñoId: Int) {
        RetrofitClient.create().getMascotasByDueño(dueñoId).enqueue(object : Callback<Map<String, MascotaInfo>> {
            override fun onResponse(call: Call<Map<String, MascotaInfo>>, response: Response<Map<String, MascotaInfo>>) {
                if (response.isSuccessful) {
                    val mascotas = response.body()?.values?.toList() ?: emptyList()
                    mascotasAdapter = MascotasAdapter(mascotas) {
                        solicitarReservaButton.isEnabled = mascotasAdapter.getSelectedMascotas().isNotEmpty()
                    }

                    // Configurando GridLayoutManager con 3 columnas
                    mascotasRecyclerView.layoutManager = GridLayoutManager(this@CuidadorProfileActivity, 3)
                    mascotasRecyclerView.adapter = mascotasAdapter
                } else {
                    Toast.makeText(this@CuidadorProfileActivity, "Error al cargar mascotas", Toast.LENGTH_SHORT).show()
                    Log.e("CuidadorProfileActivity", "Error al cargar mascotas: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Map<String, MascotaInfo>>, t: Throwable) {
                Toast.makeText(this@CuidadorProfileActivity, "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("CuidadorProfileActivity", "Fallo en la conexión", t)
            }
        })
    }


    private fun addOrRemoveCuidadorFavorito(dueñoId: Int, cuidadorId: Int) {
        if (esFavorito) {
            eliminarCuidadorFavorito(dueñoId, cuidadorId)
        } else {
            añadirACuidadorFavorito(dueñoId, cuidadorId)
        }
    }

    private fun añadirACuidadorFavorito(idDueño: Int, idCuidador: Int) {
        val idsCuidadores = mapOf("idCuidador" to idCuidador, "idDueño" to idDueño)
        Log.d("añadirACuidadorFavorito", "Enviando datos al endpoint: $idsCuidadores")
        val call = RetrofitClient.create().setCuidadorFavorito(idsCuidadores)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CuidadorProfileActivity, "Cuidador añadido a favoritos", Toast.LENGTH_SHORT).show()
                    esFavorito = true
                    actualizarUIFavoritos(esFavorito)
                } else {
                    Toast.makeText(this@CuidadorProfileActivity, "Error al añadir a favoritos", Toast.LENGTH_SHORT).show()
                    Log.e("añadirACuidadorFavorito", "Error al añadir a favoritos: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@CuidadorProfileActivity, "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("añadirACuidadorFavorito", "Fallo en la conexión", t)
            }
        })
    }

    private fun eliminarCuidadorFavorito(idDueño: Int, idCuidador: Int) {
        val idsCuidadoresFavoritos = mapOf("idCuidador" to idCuidador, "idDueño" to idDueño)
        RetrofitClient.create().deleteCuidadorFavorito(idsCuidadoresFavoritos).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CuidadorProfileActivity, "Cuidador eliminado de favoritos", Toast.LENGTH_SHORT).show()
                    esFavorito = false
                    actualizarUIFavoritos(esFavorito)
                } else {
                    Toast.makeText(this@CuidadorProfileActivity, "Error al eliminar de favoritos", Toast.LENGTH_SHORT).show()
                    Log.e("CuidadorProfileActivity", "Error al eliminar de favoritos: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@CuidadorProfileActivity, "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("CuidadorProfileActivity", "Fallo en la conexión", t)
            }
        })
    }

    private fun actualizarUIFavoritos(esFavorito: Boolean) {
        if (esFavorito) {
            addToFavoritesButton.setImageResource(R.drawable.ic_favoritos_red)
        } else {
            addToFavoritesButton.setImageResource(R.drawable.ic_favoritos_blanco)
        }
    }


    private fun solicitarReserva(idCuidador: Int, idDueño: Int, idServicio: Int, selectedMascotas: List<Int>) {
        val datosMascotas = selectedMascotas.map { MascotaId(it) }
        val datosReserva = ReservaRequest(
            idCuidador = idCuidador,
            idDueño = idDueño,
            idServicio = idServicio,
            mascotas = datosMascotas
        )
        Log.e("CuidadorProfileActivity", "SERVICIO ${idServicio}")
        val call = RetrofitClient.create().addReserva(datosReserva)

        call.enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    val idReserva = response.body() ?: return
                    setMascotaReservada(idReserva, selectedMascotas)
                } else {
                    Toast.makeText(this@CuidadorProfileActivity, "Error al realizar la reserva", Toast.LENGTH_SHORT).show()
                    Log.e("CuidadorProfileActivity", "Error al realizar la reserva: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Toast.makeText(this@CuidadorProfileActivity, "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("CuidadorProfileActivity", "Fallo en la conexión", t)
            }
        })
    }



    private fun mascotaReservadaConExito() {
        mascotasAdapter.clearSelectedMascotas()
    }
    private fun setMascotaReservada(idReserva: Int, selectedMascotas: List<Int>) {
        val idsReservaMascotas = ReservaMascotaRequest(idReserva, selectedMascotas)
        Log.d("setMascotaReservada", "Enviando datos al endpoint: $idsReservaMascotas")
        val call = RetrofitClient.create().setMascotaReservada(idsReservaMascotas)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CuidadorProfileActivity, "Reserva y asignación de mascotas realizadas con éxito", Toast.LENGTH_SHORT).show()
                    mascotaReservadaConExito()
                    finish()
                } else {
                    Toast.makeText(this@CuidadorProfileActivity, "Error al asignar las mascotas a la reserva", Toast.LENGTH_SHORT).show()
                    Log.e("setMascotaReservada", "Error al asignar las mascotas a la reserva: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@CuidadorProfileActivity, "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("setMascotaReservada", "Fallo en la conexión", t)
            }
        })
    }





    private fun verificarSiCuidadorEsFavorito(dueñoId: Int, cuidadorId: Int) {
        RetrofitClient.create().getCuidadoresFavoritos(dueñoId).enqueue(object : Callback<Map<String, Map<String, Any>>> {
            override fun onResponse(call: Call<Map<String, Map<String, Any>>>, response: Response<Map<String, Map<String, Any>>>) {
                if (response.isSuccessful) {
                    val cuidadoresFavoritos = response.body()
                    Log.d("CuidadorProfileActivity", "Favoritos: $cuidadoresFavoritos")
                    esFavorito = cuidadoresFavoritos?.values?.any {
                        val idCuidadorFavorito = (it["idCuidador"] as? Double)?.toInt() ?: -1
                        idCuidadorFavorito == cuidadorId
                    } == true
                    Log.e("CuidadorProfileActivity", "RESULTADO ESFAVORITO $esFavorito")
                    actualizarUIFavoritos(esFavorito)
                } else {
                    Toast.makeText(this@CuidadorProfileActivity, "Error al cargar favoritos", Toast.LENGTH_SHORT).show()
                    Log.e("CuidadorProfileActivity", "Error al cargar favoritos: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, Any>>>, t: Throwable) {
                Toast.makeText(this@CuidadorProfileActivity, "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("CuidadorProfileActivity", "Fallo en la conexión", t)
            }
        })
    }


}
