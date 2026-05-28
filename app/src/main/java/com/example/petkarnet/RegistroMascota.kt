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
import androidx.lifecycle.lifecycleScope
import com.example.petkarnet.data.model.MascotaRequest
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import com.example.petkarnet.util.CloudinaryManager
import java.io.File
import com.cloudinary.android.callback.ErrorInfo
import java.util.UUID




data class RazaMascota(val nombre: String, val imagenAId: Int) {
    override fun toString(): String {
        return nombre
    }
}

class RegistroMascota : AppCompatActivity() {

    private var uriFotoSeleccionada: Uri? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var btnGuardar: com.google.android.material.button.MaterialButton

    private val abrirGaleria =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                uriFotoSeleccionada = uri
                val ivFotoMascota =
                    findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.iv_foto_mascota)
                ivFotoMascota.setImageURI(uri)
                ivFotoMascota.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_mascota)

        val ivFotoMascota = findViewById<ImageView>(R.id.iv_foto_mascota)
        ivFotoMascota.setOnClickListener {
            abrirGaleria.launch("image/*")
        }

        progressBar = findViewById(R.id.progress_bar)
        btnGuardar = findViewById(R.id.btn_guardar_mascota)

        val tilNombre = findViewById<TextInputLayout>(R.id.til_nombre_mascota)
        val etNombre =
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_nombre_mascota)

        val tilRaza = findViewById<TextInputLayout>(R.id.til_raza)
        val etRaza = findViewById<AutoCompleteTextView>(R.id.et_raza)

        val tilSexo = findViewById<TextInputLayout>(R.id.til_sexo)
        val etSexo = findViewById<AutoCompleteTextView>(R.id.et_sexo)

        val tilColor = findViewById<TextInputLayout>(R.id.til_color)
        val etColor =
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_color)


        val tilPeso = findViewById<TextInputLayout>(R.id.til_peso)
        val etPeso =
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_peso)

        val tilNombreDueno = findViewById<TextInputLayout>(R.id.til_nombre_dueno)
        val etNombreDueno =
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_nombre_dueno)

        val tilTelefono = findViewById<TextInputLayout>(R.id.til_telefono_dueno)
        val etTelefono =
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_telefono_dueno)

        val tilDireccion = findViewById<TextInputLayout>(R.id.til_direccion_dueno)
        val etDireccion =
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_direccion_dueno)

        val tilEdad = findViewById<TextInputLayout>(R.id.til_edad)
        val etEdad = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_edad)


        etEdad.isFocusable = false // Evita que se abra el teclado numérico
        etEdad.isClickable = true
        etEdad.setOnClickListener {
            val calendario = java.util.Calendar.getInstance()
            val anio = calendario.get(java.util.Calendar.YEAR)
            val mes = calendario.get(java.util.Calendar.MONTH)
            val dia = calendario.get(java.util.Calendar.DAY_OF_MONTH)

            android.app.DatePickerDialog(this,
                R.style.TemaCalendarioPet,
                { _, yearSeleccionado, monthSeleccionado, daySeleccionado ->
                // String.format("%02d", numero) asegura que si eligen el mes 5, se escriba "05"
                val mesFormateado = String.format("%02d", monthSeleccionado + 1)
                val diaFormateado = String.format("%02d", daySeleccionado)

                // Imprime exactamente: 2024-05-09
                etEdad.setText("$yearSeleccionado-$mesFormateado-$diaFormateado")
            }, anio, mes, dia).show()
        }


        val etLadaTelefono = findViewById<AutoCompleteTextView>(R.id.et_lada_telefono)

        val opcionesLada = arrayOf(
            "🇲🇽 +52", "🇺🇸 +1", "🇨🇴 +57", "🇦🇷 +54", "🇪🇸 +34", "🇨🇱 +56", "🇵🇪 +51"
        )
        val adapterLada = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opcionesLada)
        etLadaTelefono.setAdapter(adapterLada)
        etLadaTelefono.setText(opcionesLada[0], false)

        btnGuardar.setOnClickListener {
            // Limpiar errores previos
            tilNombre.error = null
            tilRaza.error = null
            tilSexo.error = null
            tilColor.error = null
            tilEdad.error = null
            tilPeso.error = null
            tilNombreDueno.error = null
            tilTelefono.error = null
            tilDireccion.error = null

            val nombre = etNombre.text.toString().trim()
            val raza = etRaza.text.toString().trim()
            val sexo = etSexo.text.toString().trim()
            val color = etColor.text.toString().trim()
            val edad = etEdad.text.toString().trim()
            val peso = etPeso.text.toString().trim()
            val nombreDueno = etNombreDueno.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()

            var formularioValido = true

            if (nombre.isEmpty()) {
                tilNombre.error = "Ingresa el nombre de la mascota"
                formularioValido = false
            }

            if (raza.isEmpty()) {
                tilRaza.error = "Selecciona una raza de la lista"
                formularioValido = false
            }

            if (sexo.isEmpty()) {
                tilSexo.error = "Selecciona el sexo"
                formularioValido = false
            }

            if (color.isEmpty()) {
                tilColor.error = "Ingresa el color o señas particulares"
                formularioValido = false
            }


            if (peso.isEmpty()) {
                tilPeso.error = "El peso es obligatorio para dosis médicas"
                formularioValido = false
            }

            if (nombreDueno.isEmpty()) {
                tilNombreDueno.error = "Ingresa el nombre del responsable"
                formularioValido = false
            }

            if (telefono.isEmpty() || telefono.length < 10) {
                tilTelefono.error = "Ingresa un número válido de 10 dígitos"
                formularioValido = false
            }

            if (direccion.isEmpty()) {
                tilDireccion.error = "La dirección es necesaria para emergencias"
                formularioValido = false
            }


            val formatoFechaRegex = "^\\d{4}-\\d{2}-\\d{2}$".toRegex()

            if (edad.isEmpty()) {
                tilEdad.error = "Ingresa la fecha de nacimiento aproximada"
                formularioValido = false
            } else if (!edad.matches(formatoFechaRegex)) {
                // Si el usuario intentó escribir a mano y lo puso mal, lo bloqueamos
                tilEdad.error = "El formato debe ser AAAA-MM-DD (Ej. 2023-05-24)"
                formularioValido = false
            }

            if (formularioValido) {
                // Determinar especie
                val rgEspecie = findViewById<RadioGroup>(R.id.rg_especie)
                val especie = when (rgEspecie.checkedRadioButtonId) {
                    R.id.rb_perro -> "perro"
                    R.id.rb_gato -> "gato"
                    else -> "otro"
                }

                if (uriFotoSeleccionada != null) {
                    subirFotoYGuardar(uriFotoSeleccionada!!, nombre, especie, raza, edad)
                } else {
                    // Si no hay foto, guardar directamente con foto = null (o vacío)
                    guardarMascotaConFoto(nombre, especie, raza, edad, null)
                }

                // Llamar al backend
            }
        }

        // Configurar razas
        val razasPerro = listOf(
            RazaMascota("Mestizo (Sin raza específica)", R.drawable.mestizo),
            RazaMascota("Pug", R.drawable.pug),
            RazaMascota("Chihuahua", R.drawable.chihuahua),
            RazaMascota("Golden Retriever", R.drawable.golden),
            RazaMascota("Labrador", R.drawable.labrador),
            RazaMascota("Pastor Aleman", R.drawable.pastor_aleman),
            RazaMascota("Husky Siberiano", R.drawable.husky),
            RazaMascota("BullDog Francés", R.drawable.bulldog),
            RazaMascota("Poodle", R.drawable.poodle),
            RazaMascota("Schnauzer", R.drawable.schnauzer),
            RazaMascota("Pitbull", R.drawable.pitbull),
            RazaMascota("Yorkshire Terrier", R.drawable.terrier),
            RazaMascota("Otro...", R.drawable.otro)
        )

        val razasGato = listOf(
            RazaMascota("Mestizo (Sin raza específica)", R.drawable.mestizon),
            RazaMascota("Siamés", R.drawable.siames),
            RazaMascota("Persa", R.drawable.persa),
            RazaMascota("Maine Coon", R.drawable.maine_coon),
            RazaMascota("Bengala", R.drawable.bengala),
            RazaMascota("Sphynx (Esfinge)", R.drawable.esfinge),
            RazaMascota("Azul Ruso", R.drawable.ruso),
            RazaMascota("Ragdoll", R.drawable.ragdoll),
            RazaMascota("Angora", R.drawable.angora),
            RazaMascota("British Shorthair", R.drawable.british_shorthair),
            RazaMascota("Gato Europeo", R.drawable.gato_europeo),
            RazaMascota("Munchkin", R.drawable.munchkin),
            RazaMascota("Otro...", R.drawable.otron)
        )

        val autoCompleteRaza = findViewById<AutoCompleteTextView>(R.id.et_raza)
        val rgEspecie = findViewById<RadioGroup>(R.id.rg_especie)

        fun actualizarMenuRazas(razas: List<RazaMascota>) {
            val adapter = RazaAdapter(this, razas)
            autoCompleteRaza.setAdapter(adapter)
            autoCompleteRaza.setText(razas[0].nombre, false)
        }

        actualizarMenuRazas(razasPerro)

        rgEspecie.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_perro) {
                actualizarMenuRazas(razasPerro)
            } else if (checkedId == R.id.rb_gato) {
                actualizarMenuRazas(razasGato)
            }
        }

        // Configurar sexo
        val autoCompleteSexo = findViewById<AutoCompleteTextView>(R.id.et_sexo)
        val opcionesSexo = arrayOf("Macho", "Hembra")
        val adapterSexo =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opcionesSexo)
        autoCompleteSexo.setAdapter(adapterSexo)
    }

    private fun guardarMascotaConFoto(
        nombre: String,
        especie: String,
        raza: String,
        edad: String,
        urlFoto: String?
    ) {
        val request = MascotaRequest(
            nombre = nombre,
            especie = especie,
            raza = raza,
            fecha_nacimiento = edad, // Estás usando el campo edad como fecha de nacimiento
            foto = urlFoto
        )

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@RegistroMascota)
                val respuesta = api.crearMascota(request)

                mostrarCarga(false)

                if (respuesta.isSuccessful) {
                    Toast.makeText(
                        this@RegistroMascota,
                        "¡Mascota registrada exitosamente!",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(this@RegistroMascota, MenuDueno::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = respuesta.errorBody()?.string()
                    mostrarError("Error al guardar: $errorBody")
                }
            } catch (e: Exception) {
                mostrarCarga(false)
                mostrarError("Error de conexión: ${e.message}")
            }
        }
    }

    private fun mostrarCarga(mostrar: Boolean) {
        if (mostrar) {
            progressBar.visibility = android.view.View.VISIBLE
            btnGuardar.isEnabled = false
            btnGuardar.text = "Guardando..."
        } else {
            progressBar.visibility = android.view.View.GONE
            btnGuardar.isEnabled = true
            btnGuardar.text = "Crear Perfil"
        }
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun subirFotoYGuardar(
        uri: Uri,
        nombre: String,
        especie: String,
        raza: String,
        edad: String
    ) {

        val imageFile = CloudinaryManager.getFileFromUri(this, uri)
        if (imageFile == null) {
            mostrarError("No se pudo acceder a la imagen")
            return
        }


        mostrarCarga(true)

        // subir la imagen con cloudinary
        CloudinaryManager.uploadImage(imageFile) { url ->
            mostrarCarga(false) // ocultar la carga cuando termine (éxito o error)
            if (url != null) {
                // Se obtuvo la URL de la foto, ahora guardar la mascota con esa URL
                guardarMascotaConFoto(nombre, especie, raza, edad, url)
            } else {
                mostrarError("Error al subir la foto")
            }
        }
    }

    class RazaAdapter(context: Context, private val razas: List<RazaMascota>) :
        ArrayAdapter<RazaMascota>(context, 0, razas) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return crearFila(position, convertView, parent)
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return crearFila(position, convertView, parent)
        }

        private fun crearFila(position: Int, convertView: View?, parent: ViewGroup): View {
            val fila =
                convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.item_raza, parent, false)

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
}