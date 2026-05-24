package com.example.petkarnet

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText

// Nota: Si ya tienes RazaMascota y RazaAdapter en tu otro archivo,
// puedes borrarlas de aquí para no tenerlas duplicadas. Si no, déjalas sin problema.
data class RazaMascotaVet(val nombre: String, val imagenAId: Int) {
    override fun toString(): String {
        return nombre
    }
}

class registro_mascota_vet : AppCompatActivity() {

    private var uriFotoSeleccionada: Uri? = null

    private val abrirGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uriFotoSeleccionada = uri
            val ivFotoPaciente = findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.iv_foto_paciente)
            ivFotoPaciente.setImageURI(uri)
            ivFotoPaciente.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_mascota_vet)
        enableEdgeToEdge()

        // 1. Enlazamos la foto
        val ivFotoPaciente = findViewById<ImageView>(R.id.iv_foto_paciente)
        ivFotoPaciente.setOnClickListener {
            abrirGaleria.launch("image/*")
        }

        // 2. Enlazamos las vistas del Paciente
        val autoCompleteRaza = findViewById<AutoCompleteTextView>(R.id.et_raza_paciente)

        val rgEspecie = findViewById<RadioGroup>(R.id.rg_especie_paciente)

        val tilNombre = findViewById<TextInputLayout>(R.id.til_nombre_paciente)
        val etNombre = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_nombre_paciente)

        val tilRaza = findViewById<TextInputLayout>(R.id.til_raza_paciente)
        val etRaza = findViewById<AutoCompleteTextView>(R.id.et_raza_paciente)

        val tilSexo = findViewById<TextInputLayout>(R.id.til_sexo_paciente)
        val etSexo = findViewById<AutoCompleteTextView>(R.id.et_sexo_paciente)

        val tilColor = findViewById<TextInputLayout>(R.id.til_color_paciente)
        val etColor = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_color_paciente)

        val tilEdad = findViewById<TextInputLayout>(R.id.til_edad_paciente)
        val etEdad = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_edad_paciente)

        val tilPeso = findViewById<TextInputLayout>(R.id.til_peso_paciente)
        val etPeso = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_peso_paciente)

        // 3. Enlazamos las vistas del Propietario
        val tilNombrePropietario = findViewById<TextInputLayout>(R.id.til_nombre_propietario)
        val tilTelefonoPropietario = findViewById<TextInputLayout>(R.id.til_telefono_propietario)
        val etNombrePropietario = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_nombre_propietario)
        val etTelefonoPropietario = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_telefono_propietario)
        val etDireccionPropietario = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_direccion_propietario)

        val btnCrearExpediente = findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_crear_expediente)

        // ==============================================================
        // CONFIGURACIÓN DE LISTAS Y ADAPTADORES (Igual que en Dueño)
        // ==============================================================

        val razasPerro = listOf(
            RazaMascotaVet("Mestizo (Sin raza específica)", R.drawable.mestizo),
            RazaMascotaVet("Pug", R.drawable.pug),
            RazaMascotaVet("Chihuahua", R.drawable.chihuahua),
            RazaMascotaVet("Golden Retriever", R.drawable.golden),
            RazaMascotaVet("Labrador", R.drawable.labrador),
            RazaMascotaVet("Pastor Aleman", R.drawable.pastor_aleman),
            RazaMascotaVet("Husky Siberiano", R.drawable.husky),
            RazaMascotaVet("BullDog Francés", R.drawable.bulldog),
            RazaMascotaVet("Poodle", R.drawable.poodle),
            RazaMascotaVet("Schnauzer", R.drawable.schnauzer),
            RazaMascotaVet("Pitbull", R.drawable.pitbull),
            RazaMascotaVet("Yorkshire Terrier", R.drawable.terrier),
            RazaMascotaVet("Otro...", R.drawable.otro)
        )

        val razasGato = listOf(
            RazaMascotaVet("Mestizo (Sin raza específica)", R.drawable.mestizon),
            RazaMascotaVet("Siamés", R.drawable.siames),
            RazaMascotaVet("Persa", R.drawable.persa),
            RazaMascotaVet("Maine Coon", R.drawable.maine_coon),
            RazaMascotaVet("Bengala", R.drawable.bengala),
            RazaMascotaVet("Sphynx (Esfinge)", R.drawable.esfinge),
            RazaMascotaVet("Azul Ruso", R.drawable.ruso),
            RazaMascotaVet("Ragdoll", R.drawable.ragdoll),
            RazaMascotaVet("Angora", R.drawable.angora),
            RazaMascotaVet("British Shorthair", R.drawable.british_shorthair),
            RazaMascotaVet("Gato Europeo", R.drawable.gato_europeo),
            RazaMascotaVet("Munchkin", R.drawable.munchkin),
            RazaMascotaVet("Otro...", R.drawable.otron)
        )


        val autoCompleteSexo = findViewById<AutoCompleteTextView>(R.id.et_sexo_paciente)

        val opcionesSexo = arrayOf("Macho", "Hembra")
        val adapterSexo = ArrayAdapter(this,
            android.R.layout.simple_dropdown_item_1line,
            opcionesSexo
        )
        autoCompleteSexo.setAdapter(adapterSexo)

        fun actualizarMenuRazas(razas: List<RazaMascotaVet>) {
            val adapter = RazaVetAdapter(this, razas)
            autoCompleteRaza.setAdapter(adapter)
            autoCompleteRaza.setText(razas[0].nombre, false)
        }

        // Inicializamos con perros
        actualizarMenuRazas(razasPerro)

        // Cambio entre perro y gato
        rgEspecie.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_perro) {
                actualizarMenuRazas(razasPerro)
            } else if (checkedId == R.id.rb_gato) {
                actualizarMenuRazas(razasGato)
            }
        }

        // ==============================================================
        // LÓGICA DEL BOTÓN GUARDAR Y VALIDACIONES
        // ==============================================================

        btnCrearExpediente.setOnClickListener {
            // Limpiamos errores
            tilNombre.error = null
            tilRaza.error = null
            tilSexo.error = null
            tilColor.error = null
            tilEdad.error = null
            tilPeso.error = null
            tilNombrePropietario.error = null
            tilTelefonoPropietario.error = null

            // Extraemos texto
            val nombre = etNombre.text.toString().trim()
            val raza = etRaza.text.toString().trim()
            val sexo = etSexo.text.toString().trim()
            val color = etColor.text.toString().trim()
            val edad = etEdad.text.toString().trim()
            val peso = etPeso.text.toString().trim()
            val nombreDueno = etNombrePropietario.text.toString().trim()
            val telefono = etTelefonoPropietario.text.toString().trim()

            var formularioValido = true

            // Validaciones
            if (nombre.isEmpty()) {
                tilNombre.error = "Ingresa el nombre del paciente"
                formularioValido = false
            }
            if (raza.isEmpty()) {
                tilRaza.error = "Selecciona una raza"
                formularioValido = false
            }
            if (sexo.isEmpty()) {
                tilSexo.error = "Selecciona el sexo"
                formularioValido = false
            }
            if (color.isEmpty()) {
                tilColor.error = "Ingresa el color o señas"
                formularioValido = false
            }
            if (edad.isEmpty()) {
                tilEdad.error = "Ingresa la edad"
                formularioValido = false
            }
            if (peso.isEmpty()) {
                tilPeso.error = "Ingresa el peso (obligatorio para el doctor)"
                formularioValido = false
            }
            if (nombreDueno.isEmpty()) {
                tilNombrePropietario.error = "Ingresa el nombre del dueño"
                formularioValido = false
            }
            if (telefono.isEmpty() || telefono.length < 10) {
                tilTelefonoPropietario.error = "Ingresa un teléfono válido de 10 dígitos"
                formularioValido = false
            }

            if (formularioValido) {
                Toast.makeText(this, "¡Expediente médico de $nombre creado con éxito!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, Tipo_consulta::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}

// Adaptador para el menú desplegable de razas
class RazaVetAdapter(context: Context, private val razas: List<RazaMascotaVet>) :
    ArrayAdapter<RazaMascotaVet>(context, 0, razas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return crearFila(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return crearFila(position, convertView, parent)
    }

    private fun crearFila(position: Int, convertView: View?, parent: ViewGroup): View {
        val fila = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_raza, parent, false)

        val razaActual = getItem(position)
        val ivFoto = fila.findViewById<ImageView>(R.id.iv_raza_foto)
        val tvNombre = fila.findViewById<TextView>(R.id.tv_raza_nombre)

        razaActual?.let {
            ivFoto.setImageResource(it.imagenAId)
            tvNombre.text = it.nombre
        }

        return fila
    }
}