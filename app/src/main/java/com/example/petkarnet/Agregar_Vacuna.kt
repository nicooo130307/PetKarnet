package com.example.petkarnet

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.petkarnet.data.model.VacunaRequest
import com.example.petkarnet.data.network.RetrofitClient
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
    private lateinit var progressBar: ProgressBar

    private var idMascota: Int = -1
    private var tipoVacunaSeleccionada: String = ""

    // La misma lista ideal del álbum
    private val listaVacunas = listOf(
        "Rabia", "Parvovirus", "Moquillo", "Leptospirosis", "Adenovirus", "Desparasitación"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_vacuna)

        // Recibimos el ID de la mascota desde el Fragment anterior
        idMascota = intent.getIntExtra("ID_MASCOTA", -1)

        if (idMascota == -1) {
            Toast.makeText(this, "Error al identificar la mascota", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        actvTipoVacuna = findViewById(R.id.actv_tipo_vacuna)
        etFechaAplicacion = findViewById(R.id.et_fecha_aplicacion)
        etProximaDosis = findViewById(R.id.et_proxima_dosis)
        etNotas = findViewById(R.id.et_notas_vacuna)
        btnGuardar = findViewById(R.id.btn_guardar_vacuna)
        progressBar = findViewById(R.id.progress_bar)

        configurarDropdown()

        etFechaAplicacion.setOnClickListener { mostrarCalendario(etFechaAplicacion) }
        etProximaDosis.setOnClickListener { mostrarCalendario(etProximaDosis) }

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

        mostrarCarga(true)

        // Convertimos las fechas de "DD/MM/YYYY" a "YYYY-MM-DD" para MySQL
        val fechaApiSQL = formatearFechaSQL(fechaAplicacion)
        val fechaDosisSQL = if (proximaDosis.isNotEmpty()) formatearFechaSQL(proximaDosis) else null

        val request = VacunaRequest(
            id_mascota = idMascota,
            tipo_vacuna = tipoVacunaSeleccionada,
            fecha_aplicacion = fechaApiSQL,
            proxima_dosis = fechaDosisSQL,
            foto_comprobante = null, // Por ahora sin foto
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
                    finish() // Regresamos al álbum
                } else {
                    // Si el usuario no es veterinario, caerá aquí (Error 403)
                    Toast.makeText(this@Agregar_Vacuna, "Error ${respuesta.code()}: No se pudo registrar", Toast.LENGTH_LONG).show()
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
            // Formato DD/MM/YYYY para que el usuario lo lea fácil
            editText.setText(String.format("%02d/%02d/%d", d, m + 1, y))
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun mostrarCarga(mostrar: Boolean) {
        progressBar.visibility = if (mostrar) View.VISIBLE else View.GONE
        btnGuardar.isEnabled = !mostrar
        btnGuardar.text = if (mostrar) "Guardando..." else "Guardar Registro"
    }
}