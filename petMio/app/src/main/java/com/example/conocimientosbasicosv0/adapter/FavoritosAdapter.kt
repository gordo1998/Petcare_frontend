package com.example.conocimientosbasicosv0.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.activity.CuidadorProfileActivity
import com.example.conocimientosbasicosv0.api.RetrofitClient
import com.example.conocimientosbasicosv0.model.Cuidador
import com.example.conocimientosbasicosv0.model.Servicio
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritosAdapter(
    private var cuidadoresList: List<Cuidador>,
    private val eliminarCuidador: (Cuidador) -> Unit
) : RecyclerView.Adapter<FavoritosAdapter.CuidadorViewHolder>() {

    class CuidadorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val descripcionCortaTextView: TextView = itemView.findViewById(R.id.descripcionCortaTextView)
        val verPerfilButton: Button = itemView.findViewById(R.id.verPerfilButton)
        val eliminarButton: Button = itemView.findViewById(R.id.eliminarButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuidadorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cuidador_favorito, parent, false)
        return CuidadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: CuidadorViewHolder, position: Int) {
        val cuidador = cuidadoresList[position]
        holder.nombreTextView.text = cuidador.nombre
        holder.usernameTextView.text = cuidador.username
        holder.descripcionCortaTextView.text = cuidador.descripcionCorta

        holder.verPerfilButton.setOnClickListener {
            mostrarServiciosDialog(holder.itemView, cuidador)
        }

        holder.eliminarButton.setOnClickListener {
            eliminarCuidador(cuidador)
        }
    }

    override fun getItemCount(): Int {
        return cuidadoresList.size
    }

    private fun mostrarServiciosDialog(view: View, cuidador: Cuidador) {
        val context = view.context

        RetrofitClient.create().getServicios(cuidador.idCuidador).enqueue(object :
            Callback<Map<String, Map<String, Any>>> {
            override fun onResponse(call: Call<Map<String, Map<String, Any>>>, response: Response<Map<String, Map<String, Any>>>) {
                if (response.isSuccessful) {
                    response.body()?.let { servicios ->
                        AlertDialog.Builder(context).apply {
                            val layout = LayoutInflater.from(context).inflate(R.layout.dialog_servicios, null)
                            setView(layout)


                            val serviciosLayout = layout.findViewById<LinearLayout>(R.id.layoutServicios)
                            servicios.forEach { (_, detalle) ->
                                val idServicio = (detalle["idServicio"] as? Number)?.toInt() ?: -1
                                val nombreServicio = detalle["nombre"] as? String ?: "Desconocido"
                                val serviceView = LayoutInflater.from(context).inflate(R.layout.service_item, serviciosLayout, false)
                                val serviceIcon = serviceView.findViewById<ImageView>(R.id.service_icon)
                                val serviceTitle = serviceView.findViewById<TextView>(R.id.service_title)
                                serviceTitle.text = nombreServicio

                                val iconResId = when (idServicio) {
                                    1 -> R.drawable.ic_paw
                                    2 -> R.drawable.ic_home_care
                                    3 -> R.drawable.ic_pet_hotel
                                    4 -> R.drawable.ic_special_services
                                    else -> R.drawable.ic_paw // default icon
                                }
                                serviceIcon.setImageResource(iconResId)

                                serviceView.setOnClickListener {
                                    abrirCuidadoresActivity(context, idServicio, cuidador)
                                }

                                serviciosLayout.addView(serviceView)
                            }
                            setNegativeButton("Cancelar", null)
                            show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Error en la respuesta del servidor: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Map<String, Any>>>, t: Throwable) {
                Toast.makeText(context, "Error al cargar los servicios: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }


    private fun abrirCuidadoresActivity(context: Context, servicioId: Int, cuidador: Cuidador) {
        val intent = Intent(context, CuidadorProfileActivity::class.java).apply {
            putExtra("username", cuidador.username)
            putExtra("nombre", cuidador.nombre)
            putExtra("apellido1", cuidador.apellido1)
            putExtra("apellido2", cuidador.apellido2)
            putExtra("descripcionCorta", cuidador.descripcionCorta)
            putExtra("descripcionLarga", cuidador.descripcionLarga)
            putExtra("id", cuidador.idCuidador)
            putExtra("servicioId", servicioId)
            putStringArrayListExtra("mascotas", ArrayList(cuidador.mascotas))
        }
        context.startActivity(intent)
    }


    fun updateCuidadoresList(newList: List<Cuidador>) {
        cuidadoresList = newList
        notifyDataSetChanged()
    }
}
