package com.example.petkarnet

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.util.Calendar
import androidx.activity.enableEdgeToEdge

class EditarCarnet : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_carnet)

        // Buscamos los botones (Nota que ya no usamos "view.findViewById")
        val cbRabia = findViewById<CheckBox>(R.id.cb_vacuna_rabia)
        val cbParvo = findViewById<CheckBox>(R.id.cb_vacuna_parvo)
        val cbDespara = findViewById<CheckBox>(R.id.cb_desparasitacion)

        val etFechaRabia = findViewById<EditText>(R.id.et_fecha_rabia)
        val etFechaParvo = findViewById<EditText>(R.id.et_fecha_parvo)
        val etFechaDespara = findViewById<EditText>(R.id.et_fecha_despara)

        val btnGuardar = findViewById<MaterialButton>(R.id.btn_guardar_carnet)

        // --- LÓGICA DE VISIBILIDAD ---
        cbRabia.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) etFechaRabia.visibility = View.VISIBLE
            else { etFechaRabia.visibility = View.GONE; etFechaRabia.text.clear() }
        }

        cbParvo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) etFechaParvo.visibility = View.VISIBLE
            else { etFechaParvo.visibility = View.GONE; etFechaParvo.text.clear() }
        }

        cbDespara.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) etFechaDespara.visibility = View.VISIBLE
            else { etFechaDespara.visibility = View.GONE; etFechaDespara.text.clear() }
        }

        // --- LÓGICA DEL CALENDARIO ---
        etFechaRabia.setOnClickListener { mostrarCalendario(etFechaRabia) }
        etFechaParvo.setOnClickListener { mostrarCalendario(etFechaParvo) }
        etFechaDespara.setOnClickListener { mostrarCalendario(etFechaDespara) }

        // --- LÓGICA DEL BOTÓN GUARDAR ---
        btnGuardar.setOnClickListener {
            Toast.makeText(this, "¡Carnet actualizado!", Toast.LENGTH_SHORT).show()
            // Cerramos la pantalla de edición y regresamos al carnet bonito
            finish()
        }
    }

    // --- FUNCIÓN DEL CALENDARIO ---
    private fun mostrarCalendario(editText: EditText) {
        val calendario = Calendar.getInstance()
        val anio = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        // Nota que aquí usamos "this" en lugar de "requireContext()"
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
}