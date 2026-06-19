package com.example.petkarnet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
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

    private lateinit var progressBar: ProgressBar
    private lateinit var btnIngresar: Button
    private lateinit var cbMantenerSesion: CheckBox // NUEVA VARIABLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio_sesion)

        val tilCorreo = findViewById<TextInputLayout>(R.id.til_login_correo)
        val etCorreo = findViewById<TextInputEditText>(R.id.et_login_correo)

        val tilPassword = findViewById<TextInputLayout>(R.id.til_login_password)
        val etPassword = findViewById<TextInputEditText>(R.id.et_login_password)

        cbMantenerSesion = findViewById(R.id.cb_mantener_sesion) // ENLAZAMOS EL CHECKBOX
        btnIngresar = findViewById<Button>(R.id.btn_ingresar)
        progressBar = findViewById<ProgressBar>(R.id.progress_bar)

        val tvOlvidaste = findViewById<TextView>(R.id.tv_olvidaste_password)

        tvOlvidaste.setOnClickListener {
            val intent = Intent(this, RecuperarPassword::class.java)
            startActivity(intent)
        }

        btnIngresar.setOnClickListener {
            tilCorreo.error = null
            tilPassword.error = null

            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString().trim()
            var esValido = true

            if (correo.isEmpty()) {
                tilCorreo.error = "Ingresa tu correo"
                esValido = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                tilCorreo.error = "Formato de correo inválido"
                esValido = false
            }

            if (password.isEmpty()) {
                tilPassword.error = "Ingresa tu contraseña"
                esValido = false
            }

            if (esValido) {
                // Pasamos también el estado del Checkbox
                iniciarSesion(correo, password, cbMantenerSesion.isChecked)
            }
        }
    }

    // Actualizamos la función para recibir la decisión del Checkbox
    private fun iniciarSesion(email: String, password: String, mantenerSesion: Boolean) {
        mostrarCarga(true)

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@Inicio_Sesion)
                val respuesta = api.login(LoginRequest(email, password))

                mostrarCarga(false)

                if (respuesta.isSuccessful) {
                    val body = respuesta.body()
                    if (body != null) {
                        val token = body.token
                        val usuario = body.usuario

                        val prefs = getSharedPreferences("petkarnet_prefs", Context.MODE_PRIVATE)

                        // Guardamos TODO en SharedPreferences, incluyendo la decisión del usuario
                        with(prefs.edit()) {
                            putString("jwt_token", token)
                            putInt("usuario_id", usuario.id)
                            putString("usuario_rol", usuario.rol)
                            putString("usuario_nombre", usuario.nombre)
                            putBoolean("mantener_sesion", mantenerSesion) // <-- ¡AQUÍ ESTÁ LA MAGIA!
                            apply()
                        }

                        Toast.makeText(
                            this@Inicio_Sesion,
                            "¡Bienvenido, ${usuario.nombre}!",
                            Toast.LENGTH_LONG
                        ).show()

                        navegarSegunRol(usuario.rol)
                    }
                } else {
                    when (respuesta.code()) {
                        401 -> mostrarError("Correo o contraseña incorrectos")
                        403 -> mostrarError("Cuenta desactivada. Contacta al administrador")
                        500 -> mostrarError("Error del servidor. Intenta más tarde")
                        else -> mostrarError("Error al iniciar sesión (${respuesta.code()})")
                    }
                }
            } catch (e: Exception) {
                mostrarCarga(false)
                mostrarError("Error de conexión: ${e.message}")
            }
        }
    }

    private fun navegarSegunRol(rol: String) {
        val intent = when (rol) {
            "dueño" -> Intent(this, MenuDueno::class.java) // Quizás ahora quieras mandarlo a Mis_mascotas directamente
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

    private fun mostrarCarga(mostrar: Boolean) {
        if (mostrar) {
            progressBar.visibility = android.view.View.VISIBLE
            btnIngresar.isEnabled = false
            btnIngresar.text = "Ingresando..."
        } else {
            progressBar.visibility = android.view.View.GONE
            btnIngresar.isEnabled = true
            btnIngresar.text = "Ingresar"
        }
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}