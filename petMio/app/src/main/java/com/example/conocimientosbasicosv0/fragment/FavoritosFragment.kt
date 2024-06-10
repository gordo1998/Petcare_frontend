package com.example.conocimientosbasicosv0.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.adapter.FavoritosAdapter
import com.example.conocimientosbasicosv0.api.RetrofitClient
import com.example.conocimientosbasicosv0.model.Cuidador
import com.example.conocimientosbasicosv0.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritosFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var favoritosAdapter: FavoritosAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favoritos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)

        val sessionManager = SessionManager(requireContext())
        val idCuenta = sessionManager.getLoggedInAccount()?.idCuenta

        idCuenta?.let { cargarCuidadoresFavoritos(it) }
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewFavoritos)
        recyclerView.layoutManager = LinearLayoutManager(context)
        favoritosAdapter = FavoritosAdapter(emptyList()) { cuidador ->
            eliminarCuidadorFavorito(cuidador)
        }
        recyclerView.adapter = favoritosAdapter
    }

    private fun cargarCuidadoresFavoritos(idCuenta: Int) {
        RetrofitClient.create().getCuidadoresFavoritos(idCuenta).enqueue(object : Callback<Map<String, Map<String, Any>>> {
            override fun onResponse(call: Call<Map<String, Map<String, Any>>>, response: Response<Map<String, Map<String, Any>>>) {
                if (response.isSuccessful) {
                    val cuidadoresFavoritos = response.body()?.map { entry ->
                        val data = entry.value
                        Cuidador(
                            idCuidador = (data["idCuidador"] as? Number)?.toInt() ?: 0,
                            nombre = data["nombre"] as? String ?: "",
                            username = data["usuario"] as? String ?: "",
                            descripcionCorta = data["descripcion corta"] as? String ?: "",
                            descripcionLarga = data["descripcion"] as? String ?: "",
                            apellido1 = "",  // Añadir campos predeterminados o modificar según sea necesario
                            apellido2 = "",  // Añadir campos predeterminados o modificar según sea necesario
                            mascotas = emptyList()  // Añadir campos predeterminados o modificar según sea necesario
                        )
                    } ?: emptyList()
                    favoritosAdapter.updateCuidadoresList(cuidadoresFavoritos)
                } else {
                    Toast.makeText(context, "Error al cargar cuidadores favoritos", Toast.LENGTH_SHORT).show()
                    Log.e("FavoritosFragment", "Error al cargar cuidadores favoritos: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, Any>>>, t: Throwable) {
                Toast.makeText(context, "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("FavoritosFragment", "Fallo en la conexión", t)
            }
        })
    }

    private fun eliminarCuidadorFavorito(cuidador: Cuidador) {
        val sessionManager = SessionManager(requireContext())
        val idDueño = sessionManager.getLoggedInAccount()?.idCuenta ?: return
        val idsCuidadoresFavoritos = mapOf("idCuidador" to cuidador.idCuidador, "idDueño" to idDueño)
        RetrofitClient.create().deleteCuidadorFavorito(idsCuidadoresFavoritos).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    cargarCuidadoresFavoritos(idDueño)
                } else {
                    Toast.makeText(context, "Error al eliminar cuidador favorito", Toast.LENGTH_SHORT).show()
                    Log.e("FavoritosFragment", "Error al eliminar cuidador favorito: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("FavoritosFragment", "Fallo en la conexión", t)
            }
        })
    }
}
