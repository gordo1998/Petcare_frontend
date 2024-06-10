package com.example.conocimientosbasicosv0.model

data class Reserva(
    val idReserva: String,
    val nombreCuidador: String,
    val apellidoUno: String,
    val apellidoDos: String,
    val mascotas: List<MascotaReserva>,
    val servicio: String,

    )
