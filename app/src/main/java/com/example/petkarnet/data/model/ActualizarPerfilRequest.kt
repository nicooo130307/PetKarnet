package com.example.petkarnet.data.model

data class ActualizarPerfilRequest(
    val nombre: String? = null,
    val email: String? = null,
    val telefono: String? = null,
    val direccion: String? = null
)