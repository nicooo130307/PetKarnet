package com.example.petkarnet

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.petkarnet.data.model.ActualizarPerfilRequest
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class EditarInformacion : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnGuardar: MaterialButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_informacion)

        etNombre = findViewById(R.id.et_nombre_edit)
        etCorreo = findViewById(R.id.et_correo_edit)
        etContrasena = findViewById(R.id.et_contrasena_edit)
        btnGuardar = findViewById(R.id.btn_guardar_cambios)
        progressBar = findViewById(R.id.progress_bar)

        // Cargar datos reales del usuario
        cargarDatosUsuario()

        btnGuardar.setOnClickListener {
            val nombreNuevo = etNombre.text.toString().trim()
            val correoNuevo = etCorreo.text.toString().trim()

            if (nombreNuevo.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (correoNuevo.isEmpty()) {
                Toast.makeText(this, "El correo no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            guardarCambios(nombreNuevo, correoNuevo)
        }
    }

    private fun cargarDatosUsuario() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@EditarInformacion)
                val respuesta = api.perfil()

                progressBar.visibility = View.GONE

                if (respuesta.isSuccessful) {
                    val usuario = respuesta.body()
                    usuario?.let {
                        etNombre.setText(it.nombre)
                        etCorreo.setText(it.email)
                        etContrasena.setText("••••••••••••") // Nunca mostramos la contraseña real
                    }
                } else {
                    Toast.makeText(this@EditarInformacion, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@EditarInformacion, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarCambios(nombre: String, email: String) {
        progressBar.visibility = View.VISIBLE
        btnGuardar.isEnabled = false

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@EditarInformacion)
                val request = ActualizarPerfilRequest(
                    nombre = nombre,
                    email = email
                )
                val respuesta = api.actualizarPerfil(request)

                progressBar.visibility = View.GONE
                btnGuardar.isEnabled = true

                if (respuesta.isSuccessful) {
                    val body = respuesta.body()
                    // Actualizar los datos guardados en SharedPreferences
                    val prefs = getSharedPreferences("petkarnet_prefs", MODE_PRIVATE)
                    prefs.edit().putString("usuario_nombre", nombre).apply()
                    // También guardar el email si es necesario
                    prefs.edit().putString("usuario_email", email).apply()

                    Toast.makeText(this@EditarInformacion, "¡Información actualizada!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = respuesta.errorBody()?.string()
                    Toast.makeText(this@EditarInformacion, "Error al guardar: $errorBody", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                btnGuardar.isEnabled = true
                Toast.makeText(this@EditarInformacion, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}