package com.example.conocimientosbasicosv0.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.api.RetrofitClient
import com.example.conocimientosbasicosv0.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MascotasFragment : Fragment() {

    private lateinit var animalesContainer: LinearLayout

    val apiService = RetrofitClient.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mascotas, container, false)
        animalesContainer = view.findViewById(R.id.animalesContainer)

        // Cargar la lista de animales
        loadAnimales()

        return view
    }

    private fun loadAnimales() {
        apiService.getAnimales().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val animales = response.body() ?: emptyList()
                    mostrarAnimales(animales)
                } else {
                    Toast.makeText(context, "Error al cargar animales", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarAnimales(animales: List<String>) {
        animales.forEachIndexed { index, animal ->
            val button = Button(context).apply {
                text = animal
                val drawable = resources.getDrawable(getAnimalImage(animal), null)
                drawable.setBounds(0, 0, 100, 100)  // Redimensionar imagen a 100x100 px
                setCompoundDrawables(null, drawable, null, null)
                setOnClickListener {
                    val fragment = CrearMascotaFragment.newInstance(animal, index + 1) // Pasar el ID del animal (asumiendo que los IDs son secuenciales y comienzan desde 1)
                    fragmentManager?.beginTransaction()
                        ?.replace(R.id.frameContainer, fragment)
                        ?.addToBackStack(null)
                        ?.commit()
                }
            }
            animalesContainer.addView(button)
        }
    }

    private fun getAnimalImage(animal: String): Int {
        return when (animal) {
            "Perro" -> R.drawable.perro
            "Gato" -> R.drawable.gato
            "Conejo" -> R.drawable.conejo
            "Hámster" -> R.drawable.hamster
            "Cobaya" -> R.drawable.cobaya
            "Pez" -> R.drawable.pez
            "Pájaro" -> R.drawable.pajaro
            "Hurón" -> R.drawable.huron
            "Tortuga" -> R.drawable.tortuga
            "Serpiente" -> R.drawable.serpiente
            else -> R.drawable.ic_paw
        }
    }
}