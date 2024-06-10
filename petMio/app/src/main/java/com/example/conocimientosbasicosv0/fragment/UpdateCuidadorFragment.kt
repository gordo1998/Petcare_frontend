package com.example.conocimientosbasicosv0.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.GridLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.api.RetrofitClient
import com.example.conocimientosbasicosv0.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateCuidadorFragment : Fragment() {

    private lateinit var editTextDescripcionLarga: EditText
    private lateinit var layoutServicios: GridLayout
    private lateinit var layoutAnimales: GridLayout
    private lateinit var buttonGuardar: Button
    private lateinit var sessionManager: SessionManager

    private val serviciosMap = mutableMapOf<String, String>()
    private val animalesList = mutableListOf<String>()
    private val selectedServicios = mutableListOf<Int>()
    private val selectedAnimales = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_cuidador, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val cuidadorId = sessionManager.getIdCuenta()

        editTextDescripcionLarga = view.findViewById(R.id.editTextDescripcionLarga)
        layoutServicios = view.findViewById(R.id.layoutServicios)
        layoutAnimales = view.findViewById(R.id.layoutAnimales)
        buttonGuardar = view.findViewById(R.id.buttonGuardar)

        cargarServicios()
        cargarAnimales()

        buttonGuardar.setOnClickListener {
            buttonGuardar.isEnabled = false;
            //actualizarCuidador(cuidadorId)
        }
    }

    private fun cargarServicios() {
        RetrofitClient.create().getServicios().enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    serviciosMap.putAll(response.body() ?: emptyMap())
                    mostrarServicios()
                } else {
                    Toast.makeText(requireContext(), "Error al cargar servicios", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarServicios() {
        layoutServicios.removeAllViews()  // Limpiar cualquier vista previa
        serviciosMap.forEach { (id, nombre) ->
            val checkBox = CheckBox(context)
            checkBox.text = nombre
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedServicios.add(id.toInt())
                } else {
                    selectedServicios.remove(id.toInt())
                }
            }
            layoutServicios.addView(checkBox)
        }
    }

    private fun cargarAnimales() {
        RetrofitClient.create().getAnimales().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    animalesList.addAll(response.body() ?: emptyList())
                    mostrarAnimales()
                } else {
                    Toast.makeText(requireContext(), "Error al cargar animales", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarAnimales() {
        layoutAnimales.removeAllViews()  // Limpiar cualquier vista previa
        animalesList.forEachIndexed { index, animal ->
            val checkBox = CheckBox(context)
            checkBox.text = animal
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedAnimales.add(index + 1) // Asumimos que los IDs de animales empiezan en 1
                } else {
                    selectedAnimales.remove(index + 1)
                }
            }
            layoutAnimales.addView(checkBox)
        }
    }

    private fun actualizarCuidador(cuidadorId: Int) {
        val descripcionLarga = editTextDescripcionLarga.text.toString()

        val datosCuidador = mutableMapOf<String, Any>()
        datosCuidador["idCuidador"] = cuidadorId
        datosCuidador["Descripcion"] = descripcionLarga
        datosCuidador["servicios"] = selectedServicios
        datosCuidador["animales"] = selectedAnimales

        RetrofitClient.create().updateCuidador(datosCuidador).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Perfil de cuidador actualizado", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
