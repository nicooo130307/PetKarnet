package com.example.petkarnet.data.model

data class ResenaRequest(
    val id_veterinario: Int,
    val puntuacion: Int,      // 1-5 estrellas al veterinario
    val comentario: String?
)