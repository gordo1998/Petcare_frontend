package com.example.conocimientosbasicosv0.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.activity.CuidadoresActivity
import com.example.conocimientosbasicosv0.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServiciosFragment : Fragment() {
    private var mParam1: String? = null
    private var mParam2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mParam1 = it.getString(ARG_PARAM1)
            mParam2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_servicios, container, false)
        cargarServicios(view)
        return view
    }

    private fun cargarServicios(view: View) {
        RetrofitClient.create().getServicios().enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(
                call: Call<Map<String, String>>,
                response: Response<Map<String, String>>
            ) {
                if (response.isSuccessful) {
                    val servicios = response.body()
                    servicios?.let { mostrarServicios(view, it) }
                } else {
                    // Manejar error en un futuro...
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                // Manejar error en un futuro...
            }
        })
    }

    private fun mostrarServicios(view: View, servicios: Map<String, String>) {
        val layout = view.findViewById<LinearLayout>(R.id.layoutServicios)
        layout.removeAllViews() // Limpiar antes de agregar nuevos servicios
        var index = 0
        for ((id, nombre) in servicios) {
            val serviceView = LayoutInflater.from(context).inflate(R.layout.service_item, layout, false)
            val serviceIcon = serviceView.findViewById<ImageView>(R.id.service_icon)
            val serviceTitle = serviceView.findViewById<TextView>(R.id.service_title)

            // Asignar íconos dependiendo del servicio
            when (index) {
                0 -> serviceIcon.setImageResource(R.drawable.ic_paw)
                1 -> serviceIcon.setImageResource(R.drawable.ic_home_care)
                2 -> serviceIcon.setImageResource(R.drawable.ic_pet_hotel)
                3 -> serviceIcon.setImageResource(R.drawable.ic_special_services)
            }

            serviceTitle.text = nombre
            serviceView.setOnClickListener {
                abrirCuidadoresActivity(id.toInt())
            }

            layout.addView(serviceView)
            index++
            if (index >= 4) break
        }
    }

    private fun abrirCuidadoresActivity(servicioId: Int) {
        val intent = Intent(requireContext(), CuidadoresActivity::class.java).apply {
            putExtra("EXTRA_SERVICIO_ID", servicioId)
        }
        startActivity(intent)
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        fun newInstance(param1: String?, param2: String?): ServiciosFragment {
            return ServiciosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        }
    }
}
