package com.example.petkarnet.data.model

data class VacunaRequest(
    val id_mascota: Int,
    val tipo_vacuna: String,
    val fecha_aplicacion: String,    // "YYYY-MM-DD"
    val proxima_dosis: String?,
    val foto_comprobante: String?,
    val notas: String?
)