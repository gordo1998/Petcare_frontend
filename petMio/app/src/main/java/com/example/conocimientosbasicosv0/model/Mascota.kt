package com.example.conocimientosbasicosv0.model

import java.io.Serializable

data class Mascota(
    val descEnfermedades: String,
    val descSobreMascota: String,
    val edad: Int,
    val nombre: String,
    val peso: Float,
    val alimento: Int?,
    val duenyo: Int?,
    val raza: Int?
)

data class MascotaInfo(
    val idMascota: Int?,
    val nombre: String,
    val raza: String?,
    val animal: String?,
    val edad: Int?,
    val peso: Float?,
    val enfermedades: String?,
    val descripcion: String?
) : Serializable

data class MascotaReserva(
    val idMascota: Int?,
    val nombre: String,
    val raza: String?,
    val animal: String?,
) : Serializable

