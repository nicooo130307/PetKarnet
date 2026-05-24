package com.example.petkarnet.data.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: String,
    val telefono: String?,
    val direccion: String?,
    val foto_perfil: String?,
    val verificado: Boolean,
    val activo: Boolean,
    val fecha_registro: String?
)