package com.example.conocimientosbasicosv0.model

data class Cuidador(
    val idCuidador: Int,
    val nombre: String,
    val username: String,
    val descripcionCorta: String,
    val descripcionLarga: String,
    val apellido1: String = "",
    val apellido2: String = "",
    val mascotas: List<String> = emptyList()
)



