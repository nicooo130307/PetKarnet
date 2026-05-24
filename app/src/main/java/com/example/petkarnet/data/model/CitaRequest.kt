package com.example.petkarnet.data.model

data class CitaRequest(
    val id_mascota: Int,
    val id_veterinario: Int,
    val tipo_cita: String,
    val fecha_hora: String,      // como el anterior "YYYY-MM-DD HH:MM:SS"
    val notas: String?           // por si las dudas nico
)