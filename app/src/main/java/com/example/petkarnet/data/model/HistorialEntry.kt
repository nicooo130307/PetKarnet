package com.example.petkarnet.data.model

data class HistorialEntry(
    val id: Int,
    val id_mascota: Int,
    val id_veterinario: Int,
    val tipo_vacuna: String,
    val fecha_aplicacion: String,
    val proxima_dosis: String?,
    val foto_comprobante: String?,
    val notas: String?,
    val nombre_veterinario: String?
)