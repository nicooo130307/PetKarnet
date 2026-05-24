package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.activity.enableEdgeToEdge
class PerfilVeterinario : AppCompatActivity() {

    private var uriFotoSeleccionada: android.net.Uri? = null

    private val abrirGaleria = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            uriFotoSeleccionada = uri

            val ivFotoVet = findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.iv_foto_vet)
            ivFotoVet.setImageURI(uri)
            ivFotoVet.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_veterinario)


        val ivFotoVet = findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.iv_foto_vet)

        ivFotoVet.setOnClickListener {
            abrirGaleria.launch("image/*")
        }


        val tilNombreClinica = findViewById<TextInputLayout>(R.id.til_nombre_clinica)
        val etNombreClinica = findViewById<TextInputEditText>(R.id.et_nombre_clinica)

        val tilCedula = findViewById<TextInputLayout>(R.id.til_cedula)
        val etCedula = findViewById<TextInputEditText>(R.id.et_cedula)

        val tilTelefono = findViewById<TextInputLayout>(R.id.til_telefono_clinica)
        val etTelefono = findViewById<TextInputEditText>(R.id.et_telefono_clinica)

        val tilDireccion = findViewById<TextInputLayout>(R.id.til_direccion_clinica)
        val etDireccion = findViewById<TextInputEditText>(R.id.et_direccion_clinica)

        val tilHorario = findViewById<TextInputLayout>(R.id.til_horario)
        val etHorario = findViewById<TextInputEditText>(R.id.et_horario)

        val btnGuardar = findViewById<MaterialButton>(R.id.btn_guardar_clinica)

        // 2. Escuchamos el clic
        btnGuardar.setOnClickListener {

            // Limpiamos errores previos
            tilNombreClinica.error = null
            tilCedula.error = null
            tilTelefono.error = null
            tilDireccion.error = null
            tilHorario.error = null

            // Extraemos los textos
            val nombre = etNombreClinica.text.toString().trim()
            val cedula = etCedula.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()
            val horario = etHorario.text.toString().trim()

            var formularioValido = true

            // 3. Validaciones
            if (nombre.isEmpty()) {
                tilNombreClinica.error = "Ingresa el nombre de la clínica"
                formularioValido = false
            }

            if (cedula.isEmpty() || cedula.length < 7) {
                // Las cédulas suelen tener al menos 7 u 8 dígitos
                tilCedula.error = "Ingresa una Cédula Profesional válida"
                formularioValido = false
            }

            if (telefono.isEmpty()) {
                tilTelefono.error = "El teléfono es obligatorio"
                formularioValido = false
            }

            if (direccion.isEmpty()) {
                tilDireccion.error = "Ingresa la dirección para que los dueños te encuentren"
                formularioValido = false
            }

            if (horario.isEmpty()) {
                tilHorario.error = "Ingresa tus horas de atención"
                formularioValido = false
            }

            // 4. Si todo está correcto, avanzamos al Dashboard
            if (formularioValido) {
                println("¡Perfil de veterinario creado: $nombre!")
            }
        }
    }
}