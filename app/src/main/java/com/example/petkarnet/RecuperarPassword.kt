package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.petkarnet.data.model.RecuperarRequest
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class RecuperarPassword : AppCompatActivity() {

    private lateinit var tilCorreo: TextInputLayout
    private lateinit var etCorreo: TextInputEditText
    private lateinit var btnEnviar: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tvMensaje: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperar_password)

        tilCorreo = findViewById(R.id.til_correo_recuperar)
        etCorreo = findViewById(R.id.et_correo_recuperar)
        btnEnviar = findViewById(R.id.btn_enviar_correo)
        progressBar = findViewById(R.id.progress_bar)
        tvMensaje = findViewById(R.id.tv_mensaje_recuperar)

        btnEnviar.setOnClickListener {
            val correo = etCorreo.text.toString().trim()

            // Validar correo
            if (correo.isEmpty()) {
                tilCorreo.error = "Ingresa tu correo electrónico"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                tilCorreo.error = "Ingresa un correo válido"
                return@setOnClickListener
            }
            tilCorreo.error = null

            // Llamar al backend
            solicitarRecuperacion(correo)
        }
    }

    private fun solicitarRecuperacion(email: String) {
        progressBar.visibility = View.VISIBLE
        btnEnviar.isEnabled = false
        tvMensaje.text = ""

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@RecuperarPassword)
                val respuesta = api.solicitarRecuperacion(RecuperarRequest(email))

                progressBar.visibility = View.GONE
                btnEnviar.isEnabled = true

                if (respuesta.isSuccessful) {
                    // Éxito (el backend siempre responde 200 aunque el email no exista, por seguridad)
                    tvMensaje.text = "Si el correo está registrado, recibirás un enlace de recuperación. Revisa tu bandeja de entrada."
                    tvMensaje.setTextColor(resources.getColor(android.R.color.holo_green_dark))

                    // Opcional: Mostrar un botón para ir al login
                    Toast.makeText(this@RecuperarPassword, "Solicitud enviada", Toast.LENGTH_SHORT).show()
                } else {
                    tvMensaje.text = "Error al procesar la solicitud. Intenta de nuevo."
                    tvMensaje.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                btnEnviar.isEnabled = true
                tvMensaje.text = "Error de conexión: ${e.message}"
                tvMensaje.setTextColor(resources.getColor(android.R.color.holo_red_dark))
            }
        }
    }
}