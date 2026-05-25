package com.example.petkarnet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.petkarnet.data.model.RegistroRequest
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import com.example.petkarnet.data.model.LoginRequest


class Registro : AppCompatActivity() {

    // Declarar el ProgressBar como variable de clase
    private lateinit var progressBar: ProgressBar
    private lateinit var btnRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        val tilNombre = findViewById<TextInputLayout>(R.id.til_nombre)
        val etNombre = findViewById<TextInputEditText>(R.id.et_nombre)

        val tilCorreo = findViewById<TextInputLayout>(R.id.til_correo)
        val etCorreo = findViewById<TextInputEditText>(R.id.et_correo)

        val tilPassword = findViewById<TextInputLayout>(R.id.til_password)
        val etPassword = findViewById<TextInputEditText>(R.id.et_password)

        val tilConfirmar = findViewById<TextInputLayout>(R.id.til_confirmar_password)
        val etConfirmar = findViewById<TextInputEditText>(R.id.et_confirmar_password)

        val rgTipoUsuario = findViewById<RadioGroup>(R.id.rg_tipo_usuario)

        btnRegistrar = findViewById<Button>(R.id.btn_registrar_usuario)
        progressBar = findViewById<ProgressBar>(R.id.progress_bar)

        btnRegistrar.setOnClickListener {
            // Limpiar errores anteriores
            tilNombre.error = null
            tilCorreo.error = null
            tilPassword.error = null
            tilConfirmar.error = null

            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmarPassword = etConfirmar.text.toString().trim()

            var formularioValido = true

            // Validaciones (tus mismas validaciones de antes)
            if (nombre.isEmpty()) {
                tilNombre.error = "Por favor, ingresa tu nombre completo"
                formularioValido = false
            }

            if (correo.isEmpty()) {
                tilCorreo.error = "El correo es obligatorio"
                formularioValido = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                tilCorreo.error = "Ingresa un correo válido (ej. usuario@mail.com)"
                formularioValido = false
            }

            if (password.isEmpty()) {
                tilPassword.error = "La contraseña es obligatoria"
                formularioValido = false
            } else if (password.length < 6) {
                tilPassword.error = "La contraseña debe tener al menos 6 caracteres"
                formularioValido = false
            }

            if (confirmarPassword.isEmpty()) {
                tilConfirmar.error = "Debes confirmar tu contraseña"
                formularioValido = false
            } else if (password != confirmarPassword) {
                tilConfirmar.error = "Las contraseñas no coinciden"
                tilPassword.error = "Las contraseñas no coinciden"
                formularioValido = false
            }

            if (rgTipoUsuario.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Selecciona si eres dueño o veterinario", Toast.LENGTH_SHORT).show()
                formularioValido = false
            }

            if (formularioValido) {
                val rol = when (rgTipoUsuario.checkedRadioButtonId) {
                    R.id.rb_dueno -> "dueño"
                    R.id.rb_veterinario -> "veterinario"
                    else -> "dueño"
                }

                // Llamar al backend con indicador de carga
                registrarUsuario(nombre, correo, password, rol)
            }
        }
    }

    private fun registrarUsuario(nombre: String, email: String, password: String, rol: String) {
        mostrarCarga(true)

        val request = RegistroRequest(nombre, email, password, rol)

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@Registro)
                val respuestaRegistro = api.registro(request)

                if (respuestaRegistro.isSuccessful) {
                    // Registro exitoso, ahora hacemos login automático
                    val respuestaLogin = api.login(LoginRequest(email, password))

                    if (respuestaLogin.isSuccessful) {
                        val body = respuestaLogin.body()
                        if (body != null) {
                            // Guardar token y datos del usuario
                            val prefs = getSharedPreferences("petkarnet_prefs", Context.MODE_PRIVATE)
                            prefs.edit().putString("jwt_token", body.token).apply()
                            prefs.edit().putInt("usuario_id", body.usuario.id).apply()
                            prefs.edit().putString("usuario_rol", body.usuario.rol).apply()
                            prefs.edit().putString("usuario_nombre", body.usuario.nombre).apply()

                            mostrarCarga(false)
                            Toast.makeText(this@Registro, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()

                            // Navegar según el rol
                            val intent = when (rol) {
                                "dueño" -> Intent(this@Registro, RegistroMascota::class.java)
                                "veterinario" -> Intent(this@Registro, PerfilVeterinario::class.java)
                                else -> Intent(this@Registro, Inicio_Sesion::class.java)
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            mostrarCarga(false)
                            mostrarError("Error al obtener los datos del usuario")
                        }
                    } else {
                        mostrarCarga(false)
                        mostrarError("Error al iniciar sesión automáticamente: ${respuestaLogin.code()}")
                    }
                } else {
                    mostrarCarga(false)
                    val errorBody = respuestaRegistro.errorBody()?.string()
                    mostrarError("Error al registrarse: $errorBody")
                }
            } catch (e: Exception) {
                mostrarCarga(false)
                mostrarError("Error de conexión: ${e.message}")
            }
        }
    }

    // Función para mostrar u ocultar el ProgressBar y controlar el botón
    private fun mostrarCarga(mostrar: Boolean) {
        if (mostrar) {
            progressBar.visibility = android.view.View.VISIBLE   // Mostrar círculo
            btnRegistrar.isEnabled = false                      // Deshabilitar botón
            btnRegistrar.text = "Registrando..."                // Cambiar texto
        } else {
            progressBar.visibility = android.view.View.GONE     // Ocultar círculo
            btnRegistrar.isEnabled = true                       // Habilitar botón
            btnRegistrar.text = "Registrar"                     // Restaurar texto
        }
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}