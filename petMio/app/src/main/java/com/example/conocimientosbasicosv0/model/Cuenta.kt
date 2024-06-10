package com.example.conocimientosbasicosv0.model

import java.io.Serializable

data class Cuenta(
    val idCuenta: Int?,
    val apellidoDos: String?,
    val apellidoPrimero: String?,
    val email: String?,
    val movil: Int?,
    val nombre: String?,
    val passwd: String?,
    val telefono: Int?,
    var tipoPerfil: Byte?,
    val urlImagenes: String? = null,
    val username: String?
) : Serializable



