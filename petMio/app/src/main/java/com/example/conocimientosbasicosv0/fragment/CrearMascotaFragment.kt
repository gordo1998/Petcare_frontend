
package com.example.conocimientosbasicosv0.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.api.RetrofitClient

import com.example.conocimientosbasicosv0.model.Raza
import com.example.conocimientosbasicosv0.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CrearMascotaFragment : Fragment() {

    private var animal: String? = null
    private var idAnimal: Int? = null
    private lateinit var razaSpinner: Spinner
    private lateinit var nombreEditText: EditText
    private lateinit var edadEditText: EditText
    private lateinit var pesoEditText: EditText
    private lateinit var descEnfermedadesEditText: EditText
    private lateinit var descSobreMascotaEditText: EditText
    private lateinit var agregarButton: Button
    private lateinit var sessionManager: SessionManager
    private var razas: List<Raza> = listOf()

    val apiService = RetrofitClient.create()

    companion object {
        private const val ARG_ANIMAL = "animal"
        private const val ARG_ID_ANIMAL = "idAnimal"

        fun newInstance(animal: String, idAnimal: Int): CrearMascotaFragment {
            val fragment = CrearMascotaFragment()
            val args = Bundle()
            args.putString(ARG_ANIMAL, animal)
            args.putInt(ARG_ID_ANIMAL, idAnimal)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            animal = it.getString(ARG_ANIMAL)
            idAnimal = it.getInt(ARG_ID_ANIMAL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crear_mascota, container, false)

        sessionManager = SessionManager(requireContext())

        razaSpinner = view.findViewById(R.id.spinnerRazas)
        nombreEditText = view.findViewById(R.id.nombreEditText)
        edadEditText = view.findViewById(R.id.edadEditText)
        pesoEditText = view.findViewById(R.id.pesoEditText)
        descEnfermedadesEditText = view.findViewById(R.id.descEnfermedadesEditText)
        descSobreMascotaEditText = view.findViewById(R.id.descSobreMascotaEditText)
        agregarButton = view.findViewById(R.id.agregarButton)

        // Cargar la lista de razas
        loadRazas(idAnimal)

        agregarButton.setOnClickListener {
            agregarMascota()
        }

        return view
    }

    private fun loadRazas(idAnimal: Int?) {
        if (idAnimal == null) return

        apiService.getRazas(idAnimal).enqueue(object : Callback<Map<String, Raza>> {
            override fun onResponse(call: Call<Map<String, Raza>>, response: Response<Map<String, Raza>>) {
                if (response.isSuccessful) {
                    val razasMap = response.body() ?: emptyMap()
                    razas = razasMap.values.toList() // Convertir los valores del mapa a una lista de razas
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, razas.map { it.nombre })
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    razaSpinner.adapter = adapter
                } else {
                    Toast.makeText(context, "Error al cargar razas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Raza>>, t: Throwable) {
                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun agregarMascota() {
        val nombre = nombreEditText.text.toString()
        val edad = edadEditText.text.toString().toIntOrNull() ?: 0
        val peso = pesoEditText.text.toString().toFloatOrNull() ?: 0f
        val descEnfermedades = descEnfermedadesEditText.text.toString()
        val descSobreMascota = descSobreMascotaEditText.text.toString()
        val razaId = razas[razaSpinner.selectedItemPosition].id  // Obtener el ID de la raza seleccionada
        val duenyoId = sessionManager.getIdCuenta()

        val mascotaInfo = arrayListOf(
            nombre,
            edad.toString(),
            peso.toString(),
            descEnfermedades,
            descSobreMascota,
            razaId.toString(),
            duenyoId.toString()
        )

        //Log.d("CrearMascotaFragment", "Enviando mascota: $mascotaInfo")

        apiService.addMascota(mascotaInfo).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Mascota agregada exitosamente", Toast.LENGTH_SHORT).show()
                    fragmentManager?.popBackStack()
                } else {
                    Toast.makeText(context, "Error al agregar mascota", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
