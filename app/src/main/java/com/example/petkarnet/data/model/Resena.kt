package com.example.petkarnet.data.model

data class Resena(
    val id: Int,
    val id_usuario: Int,
    val id_veterinario: Int,
    val puntuacion: Int, // mivonvo
    val comentario: String?,
    val fecha: String?,  //en el mismo formato que puse antes nico
    val dueno_nombre: String?
)
