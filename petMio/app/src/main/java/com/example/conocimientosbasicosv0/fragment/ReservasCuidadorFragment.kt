package com.example.conocimientosbasicosv0.fragment

import android.os.Bundle
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
import com.example.conocimientosbasicosv0.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservasCuidadorFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var reservasAdapter: ReservasAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reservas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)

        // Obtener el ID del cuidador desde el SessionManager u otro método adecuado
        val idCuidador = SessionManager(requireContext()).getIdCuenta()

        // Si el ID del cuidador no es nulo, cargar las reservas
        if (idCuidador != 0) {
            cargarReservas(idCuidador)
        }
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewReservas)
        recyclerView.layoutManager = LinearLayoutManager(context)
        reservasAdapter = ReservasAdapter(emptyList())
        recyclerView.adapter = reservasAdapter
    }

    private fun cargarReservas(idCuidador: Int) {
        RetrofitClient.create().getReservasCuidador(idCuidador).enqueue(object : Callback<Map<String, Map<String, Any>>> {
            override fun onResponse(call: Call<Map<String, Map<String, Any>>>, response: Response<Map<String, Map<String, Any>>>) {
                if (response.isSuccessful) {
                    val reservasList = response.body()?.map { entry ->
                        val reservaMap = entry.value
                        Reserva(
                            idReserva = entry.key.substringAfter("Reserva: "),
                            nombreCuidador = reservaMap["Nombre: "] as? String ?: "",
                            apellidoUno = reservaMap["Apellido uno: "] as? String ?: "",
                            apellidoDos = reservaMap["Apellido dos: "] as? String ?: "",
                            servicio = reservaMap["Servicio"] as? String ?: "",
                            mascotas = parseMascotas(reservaMap["Mascotas"] as Map<String, Map<String, Any>>)
                        )
                    } ?: emptyList()
                    reservasAdapter.updateReservas(reservasList)
                } else {
                    // Manejar respuesta no exitosa
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, Any>>>, t: Throwable) {
                // Manejar fallo en la llamada
            }
        })
    }

    private fun parseMascotas(mascotasMap: Map<String, Map<String, Any>>): List<MascotaReserva> {
        return mascotasMap.map { (_, v) ->
            MascotaReserva(
                idMascota = v["idMascota"] as? Int ?: 0,
                nombre = v["nombre"] as? String ?: "Desconocido",
                animal = v["animal"] as? String ?: "Desconocido",
                raza = v["raza"] as? String ?: "Desconocido"
            )
        }
    }

}
