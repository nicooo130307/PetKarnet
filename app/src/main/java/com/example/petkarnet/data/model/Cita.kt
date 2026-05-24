package com.example.petkarnet.data.model

data class Cita(
    val id: Int,
    val id_mascota: Int,
    val id_dueno: Int,
    val id_veterinario: Int,
    val tipo_cita: String,
    val fecha_hora: String,
    val estado: String,
    val notas: String?,
    val recordatorio_enviado: Boolean?,
    val mascota_nombre: String?,
    val veterinario_nombre: String?,
    val dueno_nombre: String?
)
