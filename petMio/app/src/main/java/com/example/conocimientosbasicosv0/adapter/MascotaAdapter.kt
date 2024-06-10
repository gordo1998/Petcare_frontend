package com.example.conocimientosbasicosv0.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.conocimientosbasicosv0.R
import com.example.conocimientosbasicosv0.model.MascotaInfo

class MascotasAdapter(
    private val mascotasList: List<MascotaInfo>,
    private val onMascotaClick: (MascotaInfo) -> Unit
) : RecyclerView.Adapter<MascotasAdapter.MascotaViewHolder>() {

    private val selectedMascotas = mutableSetOf<MascotaInfo>()

    class MascotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val animalImageView: ImageView = itemView.findViewById(R.id.animalImageView)
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mascota_cuadrado, parent, false)
        return MascotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = mascotasList[position]
        holder.nombreTextView.text = mascota.nombre

        // Configuración de imagen basada en el tipo de animal
        when (mascota.animal) {
            "Perro" -> holder.animalImageView.setImageResource(R.drawable.perro)
            "Gato" -> holder.animalImageView.setImageResource(R.drawable.gato)
            "Conejo" -> holder.animalImageView.setImageResource(R.drawable.conejo)
            "Hámster" -> holder.animalImageView.setImageResource(R.drawable.hamster)
            "Cobaya" -> holder.animalImageView.setImageResource(R.drawable.cobaya)
            "Pez" -> holder.animalImageView.setImageResource(R.drawable.pez)
            "Pájaro" -> holder.animalImageView.setImageResource(R.drawable.pajaro)
            "Hurón" -> holder.animalImageView.setImageResource(R.drawable.huron)
            "Tortuga" -> holder.animalImageView.setImageResource(R.drawable.tortuga)
            "Serpiente" -> holder.animalImageView.setImageResource(R.drawable.serpiente)
            else -> holder.animalImageView.setImageResource(android.R.color.transparent)
        }

        // Aplicar el drawable de fondo basado en si la mascota está seleccionada o no
        if (selectedMascotas.contains(mascota)) {
            holder.itemView.setBackgroundResource(R.drawable.card_selected)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.card_unselected)
        }

        // Gestión del evento de clic en el elemento
        holder.itemView.setOnClickListener {
            if (selectedMascotas.contains(mascota)) {
                selectedMascotas.remove(mascota)
            } else {
                selectedMascotas.add(mascota)
            }
            notifyItemChanged(position)
            onMascotaClick(mascota)
        }

    }


    fun clearSelectedMascotas() {
        selectedMascotas.clear()
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return mascotasList.size
    }

    fun getSelectedMascotas(): List<Int> {
        return selectedMascotas.map { it.idMascota!! }
    }
}
