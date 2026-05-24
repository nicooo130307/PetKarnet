package com.example.petkarnet.data.model

data class Mascota(
    val id: Int,
    val id_usuario: Int,
    val nombre: String,
    val especie: String,
    val raza: String?,
    val fecha_nacimiento: String?,
    val foto: String?
)