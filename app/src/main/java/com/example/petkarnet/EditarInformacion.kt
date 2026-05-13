package com.example.petkarnet

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class EditarInformacion : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_informacion)
        enableEdgeToEdge()

        // 1. Referenciamos los campos
        val etNombre = findViewById<EditText>(R.id.et_nombre_edit)
        val etCorreo = findViewById<EditText>(R.id.et_correo_edit)
        val etContrasena = findViewById<EditText>(R.id.et_contrasena_edit)
        val btnGuardar = findViewById<MaterialButton>(R.id.btn_guardar_cambios)

        // 2. Simulamos la carga de tus datos actuales para que no aparezca vacío
        etNombre.setText("Nicolas")
        etCorreo.setText("nicolas@cecyt5.com")
        etContrasena.setText("*********")

        // 3. Al darle clic a guardar
        btnGuardar.setOnClickListener {
            val nombreNuevo = etNombre.text.toString()

            if (nombreNuevo.isNotEmpty()) {
                // Confirmamos el éxito
                Toast.makeText(this, "¡Información actualizada!", Toast.LENGTH_SHORT).show()

                // Cerramos esta pantalla y volvemos a la de Lectura automáticamente
                finish()
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }
}