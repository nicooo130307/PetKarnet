package com.example.petkarnet

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.petkarnet.data.model.*
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AgregarCita : AppCompatActivity() {

    // Cambiamos Spinner por AutoCompleteTextView
    private lateinit var actvMascota: AutoCompleteTextView
    private lateinit var actvVeterinario: AutoCompleteTextView
    private lateinit var etFecha: EditText
    private lateinit var etHora: EditText
    private lateinit var etNotas: EditText
    private lateinit var rgMotivo: RadioGroup

    private lateinit var rbVacuna: RadioButton
    private lateinit var rbEstetica: RadioButton
    private lateinit var rbConsulta: RadioButton
    private lateinit var progressBar: ProgressBar

    private lateinit var btnGuardar: MaterialButton

    private var listaMascotas = listOf<Mascota>()
    private var listaVeterinarios = listOf<Usuario>()

    // Variables para guardar los IDs seleccionados
    private var idMascotaSeleccionada: Int? = null
    private var idVeterinarioSeleccionado: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_cita)

        // Enlace de vistas
        actvMascota = findViewById(R.id.actv_mascota)
        actvVeterinario = findViewById(R.id.actv_veterinario)
        etFecha = findViewById(R.id.et_fecha_cita)
        etHora = findViewById(R.id.et_hora_cita)
        etNotas = findViewById(R.id.et_notas_cita)
        rgMotivo = findViewById(R.id.rg_motivo_cita)
        rbVacuna = findViewById(R.id.rb_motivo_vacuna)
        rbEstetica = findViewById(R.id.rb_motivo_estetica)
        rbConsulta = findViewById(R.id.rb_motivo_consulta)
        btnGuardar = findViewById(R.id.btn_guardar_cita)
        progressBar = findViewById(R.id.progress_bar)

        etFecha.setOnClickListener { mostrarCalendario(etFecha) }
        etHora.setOnClickListener { mostrarReloj(etHora) }

        btnGuardar.setOnClickListener {
            guardarCita()
        }

        cargarMascotas()
        cargarVeterinarios()
    }

    private fun cargarMascotas() {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@AgregarCita)
                val respuesta = api.listarMascotas()
                if (respuesta.isSuccessful) {
                    listaMascotas = respuesta.body() ?: emptyList()
                    val nombres = listaMascotas.map { it.nombre }

                    // Asignar el adapter al AutoCompleteTextView
                    val adapter = ArrayAdapter(this@AgregarCita, android.R.layout.simple_dropdown_item_1line, nombres)
                    actvMascota.setAdapter(adapter)

                    // Escuchar la selección
                    actvMascota.setOnItemClickListener { _, _, position, _ ->
                        idMascotaSeleccionada = listaMascotas[position].id
                    }

                } else {
                    Toast.makeText(this@AgregarCita, "No se encontraron mascotas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AgregarCita, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarVeterinarios() {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@AgregarCita)
                val respuesta = api.listarVeterinarios()

                if (respuesta.isSuccessful) {
                    listaVeterinarios = respuesta.body() ?: emptyList()

                    val nombres = listaVeterinarios.map { v ->
                        if (v.isVerificado()) "${v.nombre} ✓" else v.nombre
                    }

                    // Asignar el adapter al AutoCompleteTextView
                    val adapter = ArrayAdapter(this@AgregarCita, android.R.layout.simple_dropdown_item_1line, nombres)
                    actvVeterinario.setAdapter(adapter)

                    // Escuchar la selección
                    actvVeterinario.setOnItemClickListener { _, _, position, _ ->
                        idVeterinarioSeleccionado = listaVeterinarios[position].id
                    }

                } else {
                    Toast.makeText(this@AgregarCita, "Error al cargar veterinarios", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AgregarCita, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarCita() {
        // Validar selección utilizando las variables de estado
        if (idMascotaSeleccionada == null) {
            Toast.makeText(this, "Selecciona una mascota", Toast.LENGTH_SHORT).show()
            return
        }
        if (idVeterinarioSeleccionado == null) {
            Toast.makeText(this, "Selecciona un veterinario", Toast.LENGTH_SHORT).show()
            return
        }
        if (rgMotivo.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Selecciona un motivo", Toast.LENGTH_SHORT).show()
            return
        }

        val fecha = etFecha.text.toString().trim()
        val hora = etHora.text.toString().trim()

        if (fecha.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, "Ingresa fecha y hora", Toast.LENGTH_SHORT).show()
            return
        }

        mostrarCarga(true)

        val sdfFecha = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val sdfHora = SimpleDateFormat("HH:mm", Locale.US)
        val sdfSQL = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

        val fechaHoraStr: String
        try {
            val fechaObj = sdfFecha.parse(fecha)
            val horaObj = sdfHora.parse(hora)
            val cal = Calendar.getInstance()
            cal.time = fechaObj!!
            val hCal = Calendar.getInstance()
            hCal.time = horaObj!!
            cal.set(Calendar.HOUR_OF_DAY, hCal.get(Calendar.HOUR_OF_DAY))
            cal.set(Calendar.MINUTE, hCal.get(Calendar.MINUTE))
            fechaHoraStr = sdfSQL.format(cal.time)
        } catch (e: Exception) {
            mostrarCarga(false)
            Toast.makeText(this, "Error en formato de fecha", Toast.LENGTH_SHORT).show()
            return
        }

        val tipoCita = when (rgMotivo.checkedRadioButtonId) {
            R.id.rb_motivo_vacuna -> "vacunacion"
            R.id.rb_motivo_estetica -> "estetica"
            R.id.rb_motivo_consulta -> "revision"
            else -> "otro"
        }

        val notas = etNotas.text.toString().trim()

        val request = CitaRequest(
            id_mascota = idMascotaSeleccionada!!,
            id_veterinario = idVeterinarioSeleccionado!!,
            tipo_cita = tipoCita,
            fecha_hora = fechaHoraStr,
            notas = if (notas.isNotEmpty()) notas else null
        )

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@AgregarCita)
                val respuesta = api.agendarCita(request)
                mostrarCarga(false)
                if (respuesta.isSuccessful) {
                    Toast.makeText(this@AgregarCita, "¡Cita agendada!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AgregarCita, "Error: ${respuesta.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                mostrarCarga(false)
                Toast.makeText(this@AgregarCita, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarCarga(mostrar: Boolean) {
        progressBar.visibility = if (mostrar) View.VISIBLE else View.GONE
        btnGuardar.isEnabled = !mostrar
        btnGuardar.text = if (mostrar) "Guardando..." else "Guardar Cita"
    }

    private fun mostrarCalendario(editText: EditText) {
        val c = Calendar.getInstance()
        DatePickerDialog(this, R.style.TemaCalendarioPet, { _, y, m, d ->
            editText.setText("$d/${m + 1}/$y")
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun mostrarReloj(editText: EditText) {
        val c = Calendar.getInstance()
        TimePickerDialog(this, R.style.TemaCalendarioPet, { _, h, m ->
            editText.setText(String.format("%02d:%02d", h, m))
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
    }
}