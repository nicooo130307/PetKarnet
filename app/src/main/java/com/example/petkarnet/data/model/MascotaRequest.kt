package com.example.petkarnet.data.model

data class MascotaRequest(
    val nombre: String,
    val especie: String,
    val raza: String?,
    val fecha_nacimiento: String?,
    val foto: String?
)