package com.example.petkarnet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class PerfilVeterinario : AppCompatActivity() {

    private var uriFotoSeleccionada: Uri? = null
    private var horarioMap: Map<String, Pair<String, String>?> = emptyMap()

    // Configuración para abrir la galería y seleccionar foto
    private val abrirGaleria =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                uriFotoSeleccionada = uri
                val ivFotoVet = findViewById<ShapeableImageView>(R.id.iv_foto_vet)
                ivFotoVet.setImageURI(uri)
                ivFotoVet.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_veterinario)

        // 1. Vincular vistas
        val ivFotoVet = findViewById<ShapeableImageView>(R.id.iv_foto_vet)
        val tilNombreClinica = findViewById<TextInputLayout>(R.id.til_nombre_clinica)
        val etNombreClinica = findViewById<TextInputEditText>(R.id.et_nombre_clinica)
        val tilCedula = findViewById<TextInputLayout>(R.id.til_cedula)
        val etCedula = findViewById<TextInputEditText>(R.id.et_cedula)
        val tilTelefono = findViewById<TextInputLayout>(R.id.til_telefono_clinica)
        val etTelefono = findViewById<TextInputEditText>(R.id.et_telefono_clinica)
        val tilDireccion = findViewById<TextInputLayout>(R.id.til_direccion_clinica)
        val etDireccion = findViewById<TextInputEditText>(R.id.et_direccion_clinica)
        val btnGuardar = findViewById<MaterialButton>(R.id.btn_guardar_clinica)
        val rvHorario = findViewById<RecyclerView>(R.id.rv_horario)

        // 2. Configurar clic en la foto
        ivFotoVet.setOnClickListener {
            abrirGaleria.launch("image/*")
        }

        // 3. Configurar el listado del Horario
        val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        rvHorario.layoutManager = LinearLayoutManager(this)
        val adapter = HorarioAdapter(diasSemana) { mapa ->
            horarioMap = mapa // Guarda el horario seleccionado
        }
        rvHorario.adapter = adapter

        // 4. Lógica del botón Guardar
        btnGuardar.setOnClickListener {
            // Limpiar errores visuales previos
            tilNombreClinica.error = null
            tilCedula.error = null
            tilTelefono.error = null
            tilDireccion.error = null

            val nombre = etNombreClinica.text.toString().trim()
            val cedula = etCedula.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()

            var formularioValido = true

            // Validaciones básicas
            if (nombre.isEmpty()) {
                tilNombreClinica.error = "Ingresa el nombre de la clínica"
                formularioValido = false
            }

            if (cedula.isEmpty() || cedula.length < 7) {
                tilCedula.error = "Ingresa una Cédula Profesional válida"
                formularioValido = false
            }

            if (telefono.isEmpty()) {
                tilTelefono.error = "El teléfono es obligatorio"
                formularioValido = false
            }

            if (direccion.isEmpty()) {
                tilDireccion.error = "La dirección es obligatoria"
                formularioValido = false
            }

            if (formularioValido) {
                // Si todo está bien, pasamos al menú principal
                val intent = Intent(this, MenuVeterinario::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}