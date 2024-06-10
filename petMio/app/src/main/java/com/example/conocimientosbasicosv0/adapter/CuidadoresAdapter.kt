package com.example.conocimientosbasicosv0.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.activity.CuidadorProfileActivity
import com.example.conocimientosbasicosv0.model.Cuidador

class CuidadoresAdapter(private var cuidadoresList: List<Cuidador>, private val servicioId: Int) : RecyclerView.Adapter<CuidadoresAdapter.CuidadorViewHolder>() {

    class CuidadorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val nombreApellidoTextView: TextView = itemView.findViewById(R.id.nombreApellidoTextView)
        val descripcionCortaTextView: TextView = itemView.findViewById(R.id.descripcionCortaTextView)
        val mascotasContainer: LinearLayout = itemView.findViewById(R.id.mascotasContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuidadorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cuidador, parent, false)
        return CuidadorViewHolder(view)
    }

    override fun onBindViewHolder(holder: CuidadorViewHolder, position: Int) {
        val cuidador = cuidadoresList[position]
        holder.usernameTextView.text = cuidador.username
        holder.nombreApellidoTextView.text = "${cuidador.nombre} ${cuidador.apellido1} ${cuidador.apellido2}"
        holder.descripcionCortaTextView.text = cuidador.descripcionCorta
        Log.e("CuidadorAdapter", "SERVICIO ${servicioId}")

        // Limpiar cualquier imagen previa en el contenedor de mascotas
        holder.mascotasContainer.removeAllViews()

        // Agregar imágenes de mascotas
        cuidador.mascotas.forEach { animal ->
            val imageView = ImageView(holder.itemView.context)
            imageView.layoutParams = LinearLayout.LayoutParams(
                40.dpToPx(holder.itemView.context),
                40.dpToPx(holder.itemView.context)
            ).apply {
                setMargins(8, 0, 8, 0)
            }
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
            holder.mascotasContainer.addView(imageView)
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
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

    }

    override fun getItemCount(): Int {
        return cuidadoresList.size
    }

    fun updateData(newCuidadoresList: List<Cuidador>) {
        cuidadoresList = newCuidadoresList
        notifyDataSetChanged()
    }


}



// Extension function to convert dp to px
fun Int.dpToPx(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}
