package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.petkarnet.data.model.RestablecerRequest
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class RestablecerPassword : AppCompatActivity() {

    private lateinit var tilToken: TextInputLayout
    private lateinit var etToken: TextInputEditText
    private lateinit var tilNuevaPassword: TextInputLayout
    private lateinit var etNuevaPassword: TextInputEditText
    private lateinit var btnRestablecer: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_restablecer_password)

        tilToken = findViewById(R.id.til_token)
        etToken = findViewById(R.id.et_token)
        tilNuevaPassword = findViewById(R.id.til_nueva_password)
        etNuevaPassword = findViewById(R.id.et_nueva_password)
        btnRestablecer = findViewById(R.id.btn_restablecer)
        progressBar = findViewById(R.id.progress_bar)

        btnRestablecer.setOnClickListener {
            val token = etToken.text.toString().trim()
            val nuevaPassword = etNuevaPassword.text.toString().trim()

            // Validaciones
            if (token.isEmpty()) {
                tilToken.error = "Ingresa el token de recuperación"
                return@setOnClickListener
            }
            if (nuevaPassword.isEmpty()) {
                tilNuevaPassword.error = "Ingresa la nueva contraseña"
                return@setOnClickListener
            }
            if (nuevaPassword.length < 6) {
                tilNuevaPassword.error = "Mínimo 6 caracteres"
                return@setOnClickListener
            }

            tilToken.error = null
            tilNuevaPassword.error = null

            restablecerPassword(token, nuevaPassword)
        }
    }

    private fun restablecerPassword(token: String, nuevaPassword: String) {
        progressBar.visibility = View.VISIBLE
        btnRestablecer.isEnabled = false

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@RestablecerPassword)
                val respuesta = api.restablecerPassword(token, RestablecerRequest(nuevaPassword))

                progressBar.visibility = View.GONE
                btnRestablecer.isEnabled = true

                if (respuesta.isSuccessful) {
                    Toast.makeText(this@RestablecerPassword, "Contraseña restablecida exitosamente", Toast.LENGTH_LONG).show()
                    // Redirigir al login
                    val intent = Intent(this@RestablecerPassword, Inicio_Sesion::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = respuesta.errorBody()?.string()
                    Toast.makeText(this@RestablecerPassword, "Error: $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                btnRestablecer.isEnabled = true
                Toast.makeText(this@RestablecerPassword, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}