package com.example.petkarnet.data.model

data class LoginResponse(
    val mensaje: String,
    val token: String,
    val usuario: Usuario
)