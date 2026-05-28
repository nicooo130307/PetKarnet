package com.example.petkarnet

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
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

    private lateinit var spMascota: Spinner
    private lateinit var spVeterinario: Spinner
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_cita)

        spMascota = findViewById(R.id.sp_mascota)
        spVeterinario = findViewById(R.id.sp_veterinario)
        etFecha = findViewById(R.id.et_fecha_cita)
        etHora = findViewById(R.id.et_hora_cita)
        etNotas = findViewById(R.id.et_notas_cita)
        rgMotivo = findViewById(R.id.rg_motivo_cita)
        rbVacuna = findViewById(R.id.rb_motivo_vacuna)
        rbEstetica = findViewById(R.id.rb_motivo_estetica)
        rbConsulta = findViewById(R.id.rb_motivo_consulta)
        btnGuardar = findViewById(R.id.btn_guardar_cita)
        progressBar = findViewById<ProgressBar>(R.id.progress_bar)

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
                    val adapter = ArrayAdapter(
                        this@AgregarCita,
                        android.R.layout.simple_spinner_item,
                        listaMascotas.map { it.nombre }
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spMascota.adapter = adapter
                } else {
                    Toast.makeText(this@AgregarCita, "Error al cargar mascotas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AgregarCita, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
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
                    val adapter = ArrayAdapter(
                        this@AgregarCita,
                        android.R.layout.simple_spinner_item,
                        listaVeterinarios.map { it.nombre }
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spVeterinario.adapter = adapter
                } else {
                    val errorDelBackend = respuesta.errorBody()?.string()
                    Toast.makeText(this@AgregarCita, "Error backend: $errorDelBackend", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AgregarCita, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarCita() {
        mostrarCarga(true)
        // Validar selección
        if (spMascota.selectedItemPosition == -1 || listaMascotas.isEmpty()) {
            Toast.makeText(this, "Selecciona una mascota", Toast.LENGTH_SHORT).show()
            mostrarCarga(false)
            return
        }
        if (spVeterinario.selectedItemPosition == -1 || listaVeterinarios.isEmpty()) {
            Toast.makeText(this, "Selecciona un veterinario", Toast.LENGTH_SHORT).show()
            mostrarCarga(false)

            return
        }
        if (rgMotivo.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Selecciona un motivo", Toast.LENGTH_SHORT).show()
            mostrarCarga(false)

            return
        }
        val fecha = etFecha.text.toString().trim()
        val hora = etHora.text.toString().trim()
        if (fecha.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, "Ingresa fecha y hora", Toast.LENGTH_SHORT).show()
            mostrarCarga(false)
            return
        }




        // Convertir fecha y hora al formato SQL
        val sdfFecha = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val sdfHora = SimpleDateFormat("HH:mm", Locale.US)
        val sdfSQL = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val fechaHoraStr: String
        try {
            val fechaObj = sdfFecha.parse(fecha)
            val horaObj = sdfHora.parse(hora)
            val calendario = Calendar.getInstance()
            calendario.time = fechaObj
            val calHora = Calendar.getInstance()
            calHora.time = horaObj
            calendario.set(Calendar.HOUR_OF_DAY, calHora.get(Calendar.HOUR_OF_DAY))
            calendario.set(Calendar.MINUTE, calHora.get(Calendar.MINUTE))
            fechaHoraStr = sdfSQL.format(calendario.time)
        } catch (e: Exception) {
            Toast.makeText(this, "Formato de fecha/hora incorrecto", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener tipo de cita
        val tipoCita = when (rgMotivo.checkedRadioButtonId) {
            R.id.rb_motivo_vacuna -> "vacunacion"
            R.id.rb_motivo_estetica -> "estetica"
            R.id.rb_motivo_consulta -> "revision"
            else -> "otro"
        }

        val mascotaId = listaMascotas[spMascota.selectedItemPosition].id
        val veterinarioId = listaVeterinarios[spVeterinario.selectedItemPosition].id
        val notas = etNotas.text.toString().trim()

        val citaRequest = CitaRequest(
            id_mascota = mascotaId,
            id_veterinario = veterinarioId,
            tipo_cita = tipoCita,
            fecha_hora = fechaHoraStr,
            notas = if (notas.isNotEmpty()) notas else null
        )

        // Llamar a la API
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@AgregarCita)
                val respuesta = api.agendarCita(citaRequest)
                if (respuesta.isSuccessful) {
                    Toast.makeText(this@AgregarCita, "¡Cita programada con éxito!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorBody = respuesta.errorBody()?.string()
                    Toast.makeText(this@AgregarCita, "Error al guardar: $errorBody", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AgregarCita, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun mostrarCarga(mostrar: Boolean) {
        if (mostrar) {
            progressBar.visibility = android.view.View.VISIBLE   // Mostrar círculo
            btnGuardar.isEnabled = false                      // Deshabilitar botón
            btnGuardar.text = "Guardando..."                // Cambiar texto
        } else {
            progressBar.visibility = android.view.View.GONE     // Ocultar círculo
            btnGuardar.isEnabled = true                       // Habilitar botón
            btnGuardar.text = "Guardar Cita"                     // Restaurar texto
        }
    }

    // ... (mostrarCalendario y mostrarReloj sin cambios)
    private fun mostrarCalendario(editText: EditText) {
        val calendario = Calendar.getInstance()
        val anio = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            R.style.TemaCalendarioPet,
            { _, anioSeleccionado, mesSeleccionado, diaSeleccionado ->
                val fechaFormateada = "$diaSeleccionado/${mesSeleccionado + 1}/$anioSeleccionado"
                editText.setText(fechaFormateada)
            },
            anio, mes, dia
        )
        datePickerDialog.show()
    }

    private fun mostrarReloj(editText: EditText) {
        val calendario = Calendar.getInstance()
        val hora = calendario.get(Calendar.HOUR_OF_DAY)
        val minuto = calendario.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            R.style.TemaCalendarioPet,
            { _, horaSeleccionada, minutoSeleccionado ->
                val horaFormateada = String.format("%02d:%02d", horaSeleccionada, minutoSeleccionado)
                editText.setText(horaFormateada)
            },
            hora, minuto, true
        )
        timePickerDialog.show()
    }
}