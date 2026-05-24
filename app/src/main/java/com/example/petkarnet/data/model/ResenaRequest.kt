package com.example.petkarnet.data.model

data class ResenaRequest(
    val id_veterinario: Int,
    val puntuacion: Int,
    val comentario: String?
)