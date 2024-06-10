package com.example.conocimientosbasicosv0.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.model.Reserva

class ReservasAdapter(private var reservas: List<Reserva>) : RecyclerView.Adapter<ReservasAdapter.ReservaViewHolder>() {

    inner class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        private val apellidosTextView: TextView = itemView.findViewById(R.id.apellidosTextView)
        private val servicioTextView: TextView = itemView.findViewById(R.id.servicioTextView)
        private val mascotasTextView: TextView = itemView.findViewById(R.id.mascotasTextView)

        fun bind(reserva: Reserva) {
            nombreTextView.text = reserva.nombreCuidador
            apellidosTextView.text = "${reserva.apellidoUno} ${reserva.apellidoDos}"
            servicioTextView.text = reserva.servicio


            // Formateando la información de las mascotas
            val mascotasInfo = reserva.mascotas.joinToString("\n") { mascota ->
                "${mascota.nombre} (${mascota.raza})"
            }
            mascotasTextView.text = mascotasInfo
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        holder.bind(reservas[position])
    }

    override fun getItemCount(): Int = reservas.size

    fun updateReservas(nuevasReservas: List<Reserva>) {
        reservas = nuevasReservas
        notifyDataSetChanged()
    }
}
