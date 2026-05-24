package com.example.petkarnet.data.model

data class Ubicacion(
    val latitud: Double,
    val longitud: Double,
    val nombre: String,
    val direccion: String,
    val veterinario: String?,
    val verificado: Boolean?
)