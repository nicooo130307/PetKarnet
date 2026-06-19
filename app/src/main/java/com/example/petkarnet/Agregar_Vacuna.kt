package com.example.petkarnet

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.petkarnet.data.model.VacunaRequest
import com.example.petkarnet.data.network.RetrofitClient
import com.example.petkarnet.util.CloudinaryManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class Agregar_Vacuna : AppCompatActivity() {

    private lateinit var actvTipoVacuna: AutoCompleteTextView
    private lateinit var etFechaAplicacion: EditText
    private lateinit var etProximaDosis: EditText
    private lateinit var etNotas: EditText
    private lateinit var btnGuardar: MaterialButton
    private lateinit var btnAdjuntarFoto: MaterialButton
    private lateinit var ivComprobante: ImageView
    private lateinit var progressBar: ProgressBar

    private var idMascota: Int = -1
    private var tipoVacunaSeleccionada: String = ""
    private var especieMascota: String = ""

    // Variables para la foto
    private var uriFotoSeleccionada: Uri? = null

    private lateinit var listaVacunas: List<String>

    // Launcher para abrir la galería
    private val abrirGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uriFotoSeleccionada = uri
            ivComprobante.setImageURI(uri)
            ivComprobante.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_vacuna)

        // 1. Recibimos los datos desde el Fragment anterior
        idMascota = intent.getIntExtra("ID_MASCOTA", -1)
        especieMascota = intent.getStringExtra("ESPECIE_MASCOTA") ?: "Perro"

        if (idMascota == -1) {
            Toast.makeText(this, "Error al identificar la mascota", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 2. Elegimos la lista correcta según la especie
        listaVacunas = if (especieMascota.equals("gato", ignoreCase = true)) {
            listOf("Rabia", "Triple Felina", "Leucemia Felina", "Desparasitación")
        } else {
            listOf("Rabia", "Parvovirus", "Moquillo", "Leptospirosis", "Adenovirus", "Desparasitación")
        }

        // 3. Enlazamos las vistas
        actvTipoVacuna = findViewById(R.id.actv_tipo_vacuna)
        etFechaAplicacion = findViewById(R.id.et_fecha_aplicacion)
        etProximaDosis = findViewById(R.id.et_proxima_dosis)
        etNotas = findViewById(R.id.et_notas_vacuna)
        btnGuardar = findViewById(R.id.btn_guardar_vacuna)
        btnAdjuntarFoto = findViewById(R.id.btn_adjuntar_foto)
        ivComprobante = findViewById(R.id.iv_comprobante_vacuna)
        progressBar = findViewById(R.id.progress_bar)

        // 4. Configurar dropdown de vacunas
        configurarDropdown()

        // 5. Calendarios para fechas
        etFechaAplicacion.setOnClickListener { mostrarCalendario(etFechaAplicacion) }
        etProximaDosis.setOnClickListener { mostrarCalendario(etProximaDosis) }

        // 6. Botón para adjuntar foto
        btnAdjuntarFoto.setOnClickListener {
            abrirGaleria.launch("image/*")
        }

        // 7. Botón de guardar
        btnGuardar.setOnClickListener {
            guardarVacuna()
        }
    }

    private fun configurarDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listaVacunas)
        actvTipoVacuna.setAdapter(adapter)

        actvTipoVacuna.setOnItemClickListener { _, _, position, _ ->
            tipoVacunaSeleccionada = listaVacunas[position]
        }
    }

    private fun guardarVacuna() {
        // Validaciones
        if (tipoVacunaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Selecciona el tipo de vacuna", Toast.LENGTH_SHORT).show()
            return
        }

        val fechaAplicacion = etFechaAplicacion.text.toString().trim()
        if (fechaAplicacion.isEmpty()) {
            Toast.makeText(this, "Ingresa la fecha de aplicación", Toast.LENGTH_SHORT).show()
            return
        }

        val proximaDosis = etProximaDosis.text.toString().trim()
        val notas = etNotas.text.toString().trim()

        // Si hay foto seleccionada, subirla primero a Cloudinary
        if (uriFotoSeleccionada != null) {
            subirFotoYGuardar(fechaAplicacion, proximaDosis, notas)
        } else {
            // Guardar sin foto
            guardarVacunaEnBD(fechaAplicacion, proximaDosis, notas, null)
        }
    }

    private fun subirFotoYGuardar(fechaAplicacion: String, proximaDosis: String, notas: String) {
        val imageFile = CloudinaryManager.getFileFromUri(this, uriFotoSeleccionada!!)
        if (imageFile == null) {
            Toast.makeText(this, "No se pudo acceder a la imagen", Toast.LENGTH_SHORT).show()
            return
        }

        mostrarCarga(true)

        CloudinaryManager.uploadImage(imageFile) { url ->
            if (url != null) {
                guardarVacunaEnBD(fechaAplicacion, proximaDosis, notas, url)
            } else {
                mostrarCarga(false)
                Toast.makeText(this, "Error al subir la foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarVacunaEnBD(
        fechaAplicacion: String,
        proximaDosis: String,
        notas: String,
        fotoUrl: String?
    ) {
        mostrarCarga(true)

        val fechaApiSQL = formatearFechaSQL(fechaAplicacion)
        val fechaDosisSQL = if (proximaDosis.isNotEmpty()) formatearFechaSQL(proximaDosis) else null

        val request = VacunaRequest(
            id_mascota = idMascota,
            tipo_vacuna = tipoVacunaSeleccionada,
            fecha_aplicacion = fechaApiSQL,
            proxima_dosis = fechaDosisSQL,
            foto_comprobante = fotoUrl,
            notas = if (notas.isNotEmpty()) notas else null
        )

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@Agregar_Vacuna)
                val respuesta = api.registrarVacuna(request)

                mostrarCarga(false)

                if (respuesta.isSuccessful) {
                    val mensaje = respuesta.body()?.mensaje ?: "Registrada con éxito"
                    Toast.makeText(this@Agregar_Vacuna, mensaje, Toast.LENGTH_SHORT).show()
                    finish() // Regresamos a la pantalla anterior
                } else {
                    Toast.makeText(
                        this@Agregar_Vacuna,
                        "Error ${respuesta.code()}: No se pudo registrar",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                mostrarCarga(false)
                Toast.makeText(this@Agregar_Vacuna, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatearFechaSQL(fechaAndroid: String): String {
        return try {
            val sdfEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val sdfSalida = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = sdfEntrada.parse(fechaAndroid)
            sdfSalida.format(date!!)
        } catch (e: Exception) {
            fechaAndroid
        }
    }

    private fun mostrarCalendario(editText: EditText) {
        val c = Calendar.getInstance()
        DatePickerDialog(this, R.style.TemaCalendarioPet, { _, y, m, d ->
            editText.setText(String.format("%02d/%02d/%d", d, m + 1, y))
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun mostrarCarga(mostrar: Boolean) {
        progressBar.visibility = if (mostrar) View.VISIBLE else View.GONE
        btnGuardar.isEnabled = !mostrar
        btnGuardar.text = if (mostrar) "Guardando..." else "Guardar Registro"
    }
}