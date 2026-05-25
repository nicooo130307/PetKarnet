package com.example.petkarnet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.petkarnet.data.model.LoginRequest
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class Inicio_Sesion : AppCompatActivity() {

    // Declarar el ProgressBar y el botón como variables de clase
    private lateinit var progressBar: ProgressBar
    private lateinit var btnIngresar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio_sesion)

        val tilCorreo = findViewById<TextInputLayout>(R.id.til_login_correo)
        val etCorreo = findViewById<TextInputEditText>(R.id.et_login_correo)

        val tilPassword = findViewById<TextInputLayout>(R.id.til_login_password)
        val etPassword = findViewById<TextInputEditText>(R.id.et_login_password)

        btnIngresar = findViewById<Button>(R.id.btn_ingresar)
        progressBar = findViewById<ProgressBar>(R.id.progress_bar)

        btnIngresar.setOnClickListener {
            // Limpiar errores previos
            tilCorreo.error = null
            tilPassword.error = null

            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString().trim()

            var esValido = true

            // Validar Correo
            if (correo.isEmpty()) {
                tilCorreo.error = "Ingresa tu correo"
                esValido = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                tilCorreo.error = "Formato de correo inválido"
                esValido = false
            }

            // Validar Contraseña
            if (password.isEmpty()) {
                tilPassword.error = "Ingresa tu contraseña"
                esValido = false
            }

            // Si todo está bien, llamamos al backend
            if (esValido) {
                iniciarSesion(correo, password)
            }
        }
    }

    private fun iniciarSesion(email: String, password: String) {
        // Mostrar carga
        mostrarCarga(true)

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@Inicio_Sesion)
                val respuesta = api.login(LoginRequest(email, password))

                // Ocultar carga
                mostrarCarga(false)

                if (respuesta.isSuccessful) {
                    val body = respuesta.body()
                    if (body != null) {
                        // Login exitoso
                        val token = body.token
                        val usuario = body.usuario

                        // Guardar el token y datos del usuario en SharedPreferences
                        val prefs = getSharedPreferences("petkarnet_prefs", Context.MODE_PRIVATE)
                        prefs.edit().putString("jwt_token", token).apply()
                        prefs.edit().putInt("usuario_id", usuario.id).apply()
                        prefs.edit().putString("usuario_rol", usuario.rol).apply()
                        prefs.edit().putString("usuario_nombre", usuario.nombre).apply()

                        Toast.makeText(
                            this@Inicio_Sesion,
                            "¡Bienvenido, ${usuario.nombre}!",
                            Toast.LENGTH_LONG
                        ).show()

                        // Navegar según el rol
                        navegarSegunRol(usuario.rol)
                    }
                } else {
                    // Error del servidor
                    when (respuesta.code()) {
                        401 -> mostrarError("Correo o contraseña incorrectos")
                        403 -> mostrarError("Cuenta desactivada. Contacta al administrador")
                        500 -> mostrarError("Error del servidor. Intenta más tarde")
                        else -> mostrarError("Error al iniciar sesión (${respuesta.code()})")
                    }
                }
            } catch (e: Exception) {
                // Ocultar carga incluso si hay error
                mostrarCarga(false)
                mostrarError("Error de conexión: ${e.message}")
            }
        }
    }

    private fun navegarSegunRol(rol: String) {
        val intent = when (rol) {
            "dueño" -> Intent(this, MenuDueno::class.java)
            "veterinario" -> Intent(this, MenuVeterinario::class.java)
            "admin" -> Intent(this, MenuAdmin::class.java)
            else -> {
                mostrarError("Rol desconocido")
                return
            }
        }
        startActivity(intent)
        finish()
    }

    // Función para mostrar u ocultar el ProgressBar y controlar el botón
    private fun mostrarCarga(mostrar: Boolean) {
        if (mostrar) {
            progressBar.visibility = android.view.View.VISIBLE   // Mostrar círculo
            btnIngresar.isEnabled = false                       // Deshabilitar botón
            btnIngresar.text = "Ingresando..."                  // Cambiar texto
        } else {
            progressBar.visibility = android.view.View.GONE     // Ocultar círculo
            btnIngresar.isEnabled = true                        // Habilitar botón
            btnIngresar.text = "Ingresar"                       // Restaurar texto
        }
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}