package com.example.conocimientosbasicosv0.model

data class ReservaRequest(
    val idCuidador: Int,
    val idDueño: Int,
    val idServicio: Int,
    val mascotas: List<MascotaId>
)

data class MascotaId(
    val idMascota: Int
)

data class ReservaMascotaRequest(
    val idReserva: Int,
    val listaMascotas: List<Int>
)