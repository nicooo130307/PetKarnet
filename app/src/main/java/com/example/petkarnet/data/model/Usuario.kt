package com.example.petkarnet.data.model

data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: String,
    val telefono: String?,
    val direccion: String?,
    val foto_perfil: String?,
    val verificado: Any?,
    val activo: Boolean,
    val fecha_registro: String?
)
{


    fun isVerificado(): Boolean {
        return when (verificado) {
            is Boolean -> verificado
            is Number -> verificado.toInt() == 1
            is String -> verificado == "1" || verificado.lowercase() == "true"
            else -> false
        }
    }
}