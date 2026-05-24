package com.example.petkarnet.data.model

data class RegistroRequest(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String
)