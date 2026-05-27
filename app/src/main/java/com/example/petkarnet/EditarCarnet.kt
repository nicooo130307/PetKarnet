package com.example.petkarnet

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class EditarCarnet : AppCompatActivity() {

    // Variables globales para guardar temporalmente las fotos seleccionadas
    private var uriFotoVacuna: Uri? = null
    private var uriFotoDesparasitante: Uri? = null

    // Lanzador para la galería de la Vacuna
    private val abrirGaleriaVacuna = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uriFotoVacuna = uri
            val ivFotoVacuna = findViewById<ImageView>(R.id.iv_foto_vacuna_dueno)
            ivFotoVacuna.setImageURI(uri)
            ivFotoVacuna.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    // Lanzador para la galería del Desparasitante
    private val abrirGaleriaDesparasitante = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uriFotoDesparasitante = uri
            val ivFotoDesp = findViewById<ImageView>(R.id.iv_foto_desp_dueno)
            ivFotoDesp.setImageURI(uri)
            ivFotoDesp.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_carnet)

        // ==============================================================
        // 1. CONFIGURACIÓN DE FECHAS (Calendarios nativos)
        // ==============================================================
        val etFechaAppVacuna = findViewById<TextInputEditText>(R.id.et_fecha_app_vacuna)
        val etFechaProxVacuna = findViewById<TextInputEditText>(R.id.et_fecha_prox_vacuna)
        val etFechaAppDesp = findViewById<TextInputEditText>(R.id.et_fecha_app_desp)
        val etFechaProxDesp = findViewById<TextInputEditText>(R.id.et_fecha_prox_desp)

        fun configurarDatePicker(editText: TextInputEditText) {
            editText.setOnClickListener {
                val calendario = Calendar.getInstance()
                val anio = calendario.get(Calendar.YEAR)
                val mes = calendario.get(Calendar.MONTH)
                val dia = calendario.get(Calendar.DAY_OF_MONTH)

                DatePickerDialog(this, { _, yearSeleccionado, monthSeleccionado, daySeleccionado ->
                    val mesReal = monthSeleccionado + 1
                    editText.setText("$daySeleccionado/$mesReal/$yearSeleccionado")
                }, anio, mes, dia).show()
            }
        }

        // Activamos los calendarios en los 4 campos de texto
        configurarDatePicker(etFechaAppVacuna)
        configurarDatePicker(etFechaProxVacuna)
        configurarDatePicker(etFechaAppDesp)
        configurarDatePicker(etFechaProxDesp)


        // ==============================================================
        // 2. CONFIGURACIÓN DE GALERÍAS (Al tocar las fotos)
        // ==============================================================
        findViewById<ImageView>(R.id.iv_foto_vacuna_dueno).setOnClickListener {
            abrirGaleriaVacuna.launch("image/*")
        }

        findViewById<ImageView>(R.id.iv_foto_desp_dueno).setOnClickListener {
            abrirGaleriaDesparasitante.launch("image/*")
        }


        // ==============================================================
        // 3. CONFIGURACIÓN DE MENÚS DESPLEGABLES (Autocompletar)
        // ==============================================================
        val etNombreVacuna = findViewById<AutoCompleteTextView>(R.id.et_nombre_vacuna_dueno)
        val etNombreDesp = findViewById<AutoCompleteTextView>(R.id.et_nombre_desp_dueno)

        val listaVacunas = arrayOf("Múltiple (Sextuple)", "Rabia", "Bordetella", "Parvovirus/Moquillo")
        val listaDesp = arrayOf("NexGard Spectra", "Bravecto", "Simparica Trio", "Drontal Plus")

        etNombreVacuna.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listaVacunas))
        etNombreDesp.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listaDesp))


        // ==============================================================
        // 4. LÓGICA DEL ACORDEÓN: VACUNAS
        // ==============================================================
        val btnAbrirFormVacuna = findViewById<MaterialButton>(R.id.btn_abrir_form_vacuna)
        val llFormularioVacuna = findViewById<LinearLayout>(R.id.ll_formulario_vacuna_dueno)
        val btnGuardarVacunaIndividual = findViewById<MaterialButton>(R.id.btn_guardar_vacuna_individual)

        // Al presionar "+ Añadir", ocultamos el botón y mostramos el formulario
        btnAbrirFormVacuna.setOnClickListener {
            btnAbrirFormVacuna.visibility = View.GONE
            llFormularioVacuna.visibility = View.VISIBLE
        }

        // Al guardar, ocultamos el formulario, mostramos el botón y limpiamos datos
        btnGuardarVacunaIndividual.setOnClickListener {
            Toast.makeText(this, "Vacuna guardada en el historial", Toast.LENGTH_SHORT).show()
            llFormularioVacuna.visibility = View.GONE
            btnAbrirFormVacuna.visibility = View.VISIBLE

            // Limpieza de campos
            etNombreVacuna.text.clear()
            etFechaAppVacuna.text?.clear()
            etFechaProxVacuna.text?.clear()
            findViewById<ImageView>(R.id.iv_foto_vacuna_dueno).setImageResource(R.drawable.agrega_foto)
            uriFotoVacuna = null
        }


        // ==============================================================
        // 5. LÓGICA DEL ACORDEÓN: DESPARASITANTES
        // ==============================================================
        val btnAbrirFormDesp = findViewById<MaterialButton>(R.id.btn_abrir_form_desp)
        val llFormularioDesp = findViewById<LinearLayout>(R.id.ll_formulario_desp_dueno)
        val btnGuardarDespIndividual = findViewById<MaterialButton>(R.id.btn_guardar_desp_individual)

        btnAbrirFormDesp.setOnClickListener {
            btnAbrirFormDesp.visibility = View.GONE
            llFormularioDesp.visibility = View.VISIBLE
        }

        btnGuardarDespIndividual.setOnClickListener {
            Toast.makeText(this, "Desparasitante guardado en el historial", Toast.LENGTH_SHORT).show()
            llFormularioDesp.visibility = View.GONE
            btnAbrirFormDesp.visibility = View.VISIBLE

            // Limpieza de campos
            etNombreDesp.text.clear()
            etFechaAppDesp.text?.clear()
            etFechaProxDesp.text?.clear()
            findViewById<ImageView>(R.id.iv_foto_desp_dueno).setImageResource(R.drawable.agrega_foto)
            uriFotoDesparasitante = null
        }


        // ==============================================================
        // 6. BOTÓN PRINCIPAL (ACTUALIZAR CARNET COMPLETO)
        // ==============================================================
        val btnGuardarCarnet = findViewById<MaterialButton>(R.id.btn_guardar_carnet)
        btnGuardarCarnet.setOnClickListener {
            // Aquí en el futuro enviarás a tu base de datos si modificaron el nombre/dirección del dueño
            Toast.makeText(this, "¡Datos generales del carnet actualizados!", Toast.LENGTH_SHORT).show()
            finish() // Cierra la pantalla y regresa al menú anterior
        }
    }
}