package com.example.conocimientosbasicosv0.fragment

import com.example.conocimientosbasicosv0.utils.SessionManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.adapter.ReservasAdapter
import com.example.conocimientosbasicosv0.api.RetrofitClient
import com.example.conocimientosbasicosv0.model.MascotaReserva
import com.example.conocimientosbasicosv0.model.Reserva
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservasFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var reservasAdapter: ReservasAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reservas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)

        // Obtener el ID del dueño desde el SessionManager
        val sessionManager = SessionManager(requireContext())
        val idDueño = sessionManager.getIdCuenta()

        // Si el ID del dueño no es nulo, cargar las reservas
        if (idDueño != 0) {
            cargarReservas(idDueño)
        }
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewReservas)
        recyclerView.layoutManager = LinearLayoutManager(context)
        reservasAdapter = ReservasAdapter(emptyList())
        recyclerView.adapter = reservasAdapter
    }

    private fun cargarReservas(idDueño: Int) {
        RetrofitClient.create().getReservasDueño(idDueño).enqueue(object : Callback<Map<String, Map<String, Any>>> {
            override fun onResponse(call: Call<Map<String, Map<String, Any>>>, response: Response<Map<String, Map<String, Any>>>) {
                if (response.isSuccessful) {
                    // Transformar la respuesta en una lista de objetos Reserva
                    val reservasList = response.body()?.map { entry ->
                        val idReserva = entry.key.substringAfter("Reserva") // key es "ReservaX"
                        val reservaMap = entry.value
                        val mascotasMap = reservaMap["mascota"] as? Map<String, Map<String, Any>> ?: mapOf()
                        val mascotas = mascotasMap.map { mascota ->
                            MascotaReserva(
                                idMascota = mascota.value["idMascota"] as? Int ?: 0,
                                nombre = mascota.value["nombre"] as? String ?: "Desconocido",
                                animal = mascota.value["animal"] as? String ?: "Desconocido",
                                raza = mascota.value["raza"] as? String ?: "Desconocido"
                            )
                        }
                        Reserva(
                            idReserva = idReserva,
                            nombreCuidador = reservaMap["Nombre: "] as? String ?: "",
                            apellidoUno = reservaMap["Apellido uno"] as? String ?: "",
                            apellidoDos = reservaMap["Apellido dos"] as? String ?: "",
                            servicio = reservaMap["Servicio"] as? String ?: "",
                            mascotas = mascotas
                        )
                    } ?: emptyList()

                    // Actualizar el adapter del RecyclerView en el hilo principal
                    activity?.runOnUiThread {
                        reservasAdapter.updateReservas(reservasList)
                    }
                } else {
                    // Manejar respuesta no exitosa
                    Log.e("APIError", "Error en la respuesta del servidor: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, Any>>>, t: Throwable) {
                // Manejar fallo en la llamada
                Log.e("APIError", "Error al realizar la llamada a la API: ${t.message}")
            }
        })
    }


}
