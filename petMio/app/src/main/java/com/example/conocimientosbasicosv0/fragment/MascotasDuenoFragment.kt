package com.example.conocimientosbasicosv0.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.api.RetrofitClient
import com.example.conocimientosbasicosv0.model.MascotaInfo
import com.example.conocimientosbasicosv0.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MascotasDuenoFragment : Fragment() {

    private lateinit var mascotasContainer: LinearLayout
    private lateinit var addMascotaButton: Button
    private lateinit var sessionManager: SessionManager

    val apiService = RetrofitClient.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mascotas_dueno, container, false)
        mascotasContainer = view.findViewById(R.id.mascotasContainer)
        addMascotaButton = view.findViewById(R.id.addMascotaButton)
        sessionManager = SessionManager(requireContext())

        // Cargar la lista de mascotas
        loadMascotas()

        // Configurar el botón para añadir mascota
        addMascotaButton.setOnClickListener {
            val fragment = MascotasFragment() // Fragmento para añadir una nueva mascota
            fragmentManager?.beginTransaction()
                ?.replace(R.id.frameContainer, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }

        return view
    }

    private fun loadMascotas() {
        val idDueno = sessionManager.getIdCuenta()
        apiService.getMascotasByDueño(idDueno).enqueue(object : Callback<Map<String, MascotaInfo>> {
            override fun onResponse(call: Call<Map<String, MascotaInfo>>, response: Response<Map<String, MascotaInfo>>) {
                if (response.isSuccessful) {
                    val mascotas = response.body() ?: emptyMap()
                    mostrarMascotas(mascotas)
                } else {
                    Toast.makeText(context, "Error al cargar mascotas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, MascotaInfo>>, t: Throwable) {
                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun mostrarMascotas(mascotas: Map<String, MascotaInfo>) {
        mascotasContainer.removeAllViews() // Limpiar el contenedor antes de agregar nuevas vistas

        mascotas.forEach { (key, mascota) ->
            val view = LayoutInflater.from(context).inflate(R.layout.item_mascota, mascotasContainer, false)
            val nombreTextView = view.findViewById<TextView>(R.id.nombreTextView)
            val razaTextView = view.findViewById<TextView>(R.id.razaTextView)
            val animalImageView = view.findViewById<ImageView>(R.id.animalImageView)

            nombreTextView.text = "${mascota.nombre}"
            razaTextView.text = " ${mascota.raza}" // Asume que quieres mostrar el idRaza aquí

            mascota.animal?.let { setAnimalImage(animalImageView, it) }

            view.setOnClickListener {
                val fragment = MascotaUnicaFragment.newInstance(mascota)
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.frameContainer, fragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }

            mascotasContainer.addView(view)
        }
    }


    private fun setAnimalImage(imageView: ImageView, animal: String) {
        when (animal) {
            "Perro" -> imageView.setImageResource(R.drawable.perro)
            "Gato" -> imageView.setImageResource(R.drawable.gato)
            "Conejo" -> imageView.setImageResource(R.drawable.conejo)
            "Hámster" -> imageView.setImageResource(R.drawable.hamster)
            "Cobaya" -> imageView.setImageResource(R.drawable.cobaya)
            "Pez" -> imageView.setImageResource(R.drawable.pez)
            "Pájaro" -> imageView.setImageResource(R.drawable.pajaro)
            "Hurón" -> imageView.setImageResource(R.drawable.huron)
            "Tortuga" -> imageView.setImageResource(R.drawable.tortuga)
            "Serpiente" -> imageView.setImageResource(R.drawable.serpiente)
            else -> imageView.setImageResource(android.R.color.transparent)
        }
    }
}
