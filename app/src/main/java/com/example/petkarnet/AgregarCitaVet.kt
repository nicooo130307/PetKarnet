package com.example.petkarnet

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import java.util.Calendar

class AgregarCitaVet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_cita_vet)

        val etFecha = findViewById<EditText>(R.id.et_fecha_cita)
        val etHora = findViewById<EditText>(R.id.et_hora_cita)
        val btnGuardar = findViewById<MaterialButton>(R.id.btn_guardar_cita)

        // Al tocar los campos, abrimos los selectores
        etFecha.setOnClickListener { mostrarCalendario(etFecha) }
        etHora.setOnClickListener { mostrarReloj(etHora) }

        // Botón guardar
        btnGuardar.setOnClickListener {
            Toast.makeText(this, "¡Cita programada con éxito!", Toast.LENGTH_SHORT).show()
            finish() // Cierra la pantalla y regresa a la lista
        }
    }

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

        // Creamos la ventana emergente del reloj
        val timePickerDialog = TimePickerDialog(
            this,
            R.style.TemaCalendarioPet,
            { _, horaSeleccionada, minutoSeleccionado ->
                // Formateamos para que siempre tenga 2 dígitos (ej. 09:05 en vez de 9:5)
                val horaFormateada = String.format("%02d:%02d", horaSeleccionada, minutoSeleccionado)
                editText.setText(horaFormateada)
            },
            hora, minuto, true // "true" es para usar formato de 24 horas
        )
        timePickerDialog.show()
    }


    }
