package com.example.petkarnet

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class Consulta : Fragment() {

    private lateinit var llOpcionesIdentificacion: LinearLayout
    private lateinit var llPacienteSeleccionado: LinearLayout
    private lateinit var llSeccionMotivo: LinearLayout
    private lateinit var tvNombrePacienteActivo: TextView

    // Lanzador para recibir el nombre de la mascota desde la actividad de registro manual
    private val registrarMascotaLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val nombreMascota = result.data?.getStringExtra("nombre_mascota") ?: "Paciente Nuevo"
            tvNombrePacienteActivo.text = nombreMascota
            llOpcionesIdentificacion.visibility = View.GONE
            llPacienteSeleccionado.visibility = View.VISIBLE
            llSeccionMotivo.visibility = View.VISIBLE
        }
    }

    // Lanzador para seleccionar la foto de la etiqueta de la vacuna desde la galería
    private var uriFotoVacuna: Uri? = null
    private val abrirGaleriaVacuna = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uriFotoVacuna = uri
            val ivFotoVacuna = requireView().findViewById<ImageView>(R.id.iv_foto_vacuna)
            ivFotoVacuna.setImageURI(uri)
            ivFotoVacuna.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private var uriFotoDesparasitante: Uri? = null
    private val abrirGaleriaDesparasitante = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uriFotoDesparasitante = uri
            val ivFotoDesp = requireView().findViewById<ImageView>(R.id.iv_foto_desparasitante)
            ivFotoDesp.setImageURI(uri)
            ivFotoDesp.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_consulta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Enlazamos Contenedores Principales
        llOpcionesIdentificacion = view.findViewById(R.id.ll_opciones_identificacion)
        llPacienteSeleccionado = view.findViewById(R.id.ll_paciente_seleccionado)
        llSeccionMotivo = view.findViewById(R.id.ll_seccion_motivo)
        val llSeccionDetalles = view.findViewById<LinearLayout>(R.id.ll_seccion_detalles)

        // Enlazamos Contenedores Dinámicos (Hermanos independientes)
        val llCamposClinicos = view.findViewById<LinearLayout>(R.id.ll_campos_clinicos)
        val llCamposVacunacion = view.findViewById<LinearLayout>(R.id.ll_campos_vacunacion)
        val llCamposDesparasitacion = view.findViewById<LinearLayout>(R.id.ll_campos_desparasitacion)

        // 2. Enlazamos Componentes de Identificación y Motivo
        tvNombrePacienteActivo = view.findViewById(R.id.tv_nombre_paciente_activo)
        val btnEscanearQR = view.findViewById<MaterialButton>(R.id.btn_escanear_qr_consulta)
        val btnRegistroManual = view.findViewById<MaterialButton>(R.id.btn_buscar_manual_consulta)
        val btnAceptarMotivo = view.findViewById<MaterialButton>(R.id.btn_aceptar_motivo)
        val btnFinalizarConsulta = view.findViewById<MaterialButton>(R.id.btn_finalizar_consulta)
        val rgMotivo = view.findViewById<RadioGroup>(R.id.rg_motivo_consulta)

        // 3. Enlazamos Componentes Específicos de Vacunación
        val ivFotoVacuna = view.findViewById<ImageView>(R.id.iv_foto_vacuna)
        val etNombreVacuna = view.findViewById<AutoCompleteTextView>(R.id.et_nombre_vacuna)
        val etFechaAplicacion = view.findViewById<TextInputEditText>(R.id.et_fecha_aplicacion)
        val etFechaProxima = view.findViewById<TextInputEditText>(R.id.et_fecha_proxima)

        // Desparasitación: Foto, Fechas y Menú
        val ivFotoDesp = view.findViewById<ImageView>(R.id.iv_foto_desparasitante)
        val etNombreDesp = view.findViewById<AutoCompleteTextView>(R.id.et_nombre_desparasitante)
        val etFechaAppDesp = view.findViewById<TextInputEditText>(R.id.et_fecha_aplicacion_desp)
        val etFechaProxDesp = view.findViewById<TextInputEditText>(R.id.et_fecha_proxima_desp)
        val etComentariosDesp = view.findViewById<TextInputEditText>(R.id.et_comentarios_desp)


        // ==============================================================
        // LÓGICA PASO 1: Identificación del Paciente
        // ==============================================================
        btnEscanearQR.setOnClickListener {
            Toast.makeText(requireContext(), "Escaneando QR...", Toast.LENGTH_SHORT).show()
            tvNombrePacienteActivo.text = "Max (QR)"
            llOpcionesIdentificacion.visibility = View.GONE
            llPacienteSeleccionado.visibility = View.VISIBLE
            llSeccionMotivo.visibility = View.VISIBLE
        }

        btnRegistroManual.setOnClickListener {
            val intent = Intent(requireContext(), registro_mascota_vet::class.java)
            registrarMascotaLauncher.launch(intent)
        }

        // ==============================================================
        // LÓGICA DE SELECTORES DE FECHA (DatePickerDialog)
        // ==============================================================
        fun mostrarCalendario(editText: TextInputEditText) {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.TemaCalendarioPet,
                { _, yearSeleccionado, monthSeleccionado, daySeleccionado ->
                    val mesReal = monthSeleccionado + 1
                    val fechaFormateada = "$daySeleccionado/$mesReal/$yearSeleccionado"
                    editText.setText(fechaFormateada)
                },
                anio, mes, dia
            )
            datePickerDialog.show()
        }

        etFechaAplicacion.setOnClickListener { mostrarCalendario(etFechaAplicacion) }
        etFechaProxima.setOnClickListener { mostrarCalendario(etFechaProxima) }

        etFechaAppDesp.setOnClickListener { mostrarCalendario(etFechaAppDesp) }
        etFechaProxDesp.setOnClickListener { mostrarCalendario(etFechaProxDesp) }

        // ==============================================================
        // CONFIGURACIÓN DEL MENÚ DESPLEGABLE DE VACUNAS
        // ==============================================================
        ivFotoVacuna.setOnClickListener {
            abrirGaleriaVacuna.launch("image/*")
        }

        ivFotoDesp.setOnClickListener {
            abrirGaleriaDesparasitante.launch("image/*")
        }

        val listaVacunasComunes = arrayOf(
            "Múltiple (Sextuple)",
            "Rabia",
            "Bordetella (Tos de las perreras)",
            "Parvovirus/Moquillo",
            "Leucemia Felina",
            "Triple Felina"
        )
        val adapterVacunas = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, listaVacunasComunes)
        etNombreVacuna.setAdapter(adapterVacunas)

        val listaDesparasitantes = arrayOf(
            "NexGard Spectra",
            "Bravecto",
            "Simparica Trio",
            "Drontal Plus",
            "Endogard",
            "Profender (Felino)",
            "Broadline (Felino)"
        )


        val adapterDesp = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, listaDesparasitantes)
        etNombreDesp.setAdapter(adapterDesp)


        // ==============================================================
        // LÓGICA PASO 2: Selección y Despliegue Dinámico de Campos
        // ==============================================================
        btnAceptarMotivo.setOnClickListener {
            val opcionSeleccionada = rgMotivo.checkedRadioButtonId
            if (opcionSeleccionada == -1) {
                Toast.makeText(requireContext(), "Por favor selecciona un motivo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            llSeccionDetalles.visibility = View.VISIBLE

            // Ocultamos ambos contenedores antes de evaluar para limpiar la pantalla
            llCamposClinicos.visibility = View.GONE
            llCamposVacunacion.visibility = View.GONE
            llCamposDesparasitacion.visibility = View.GONE

            // Desplegamos únicamente lo que corresponde al motivo seleccionado
            when (opcionSeleccionada) {
                R.id.rb_vacuna -> {
                    llCamposVacunacion.visibility = View.VISIBLE
                }

                R.id.rb_desparasitacion -> {
                    llCamposDesparasitacion.visibility = View.VISIBLE
                }

                R.id.rb_general -> {
                    llCamposClinicos.visibility = View.VISIBLE
                }
                R.id.rb_estetica-> {
                    // Estas opciones no muestran campos extra, sólo mantienen visible el Peso General
                }
            }
        }

        // ==============================================================
        // LÓGICA PASO 3: Guardar Consulta y Reiniciar Formulario
        // ==============================================================
        btnFinalizarConsulta.setOnClickListener {
            Toast.makeText(requireContext(), "Consulta guardada exitosamente", Toast.LENGTH_LONG).show()

            // Colapsamos todas las secciones dinámicas
            llOpcionesIdentificacion.visibility = View.VISIBLE
            llPacienteSeleccionado.visibility = View.GONE
            llSeccionMotivo.visibility = View.GONE
            llSeccionDetalles.visibility = View.GONE

            // Limpiamos los campos del formulario
            rgMotivo.clearCheck()
            ivFotoVacuna.setImageResource(android.R.drawable.ic_menu_camera)
            uriFotoVacuna = null
            etNombreVacuna.text.clear()
            etFechaAplicacion.text?.clear()
            etFechaProxima.text?.clear()

            // Limpiar campos de desparasitación
            ivFotoDesp.setImageResource(R.drawable.agrega_foto) // Asegúrate de usar el mismo drawable de tu XML
            uriFotoDesparasitante = null
            etNombreDesp.text.clear()
            etFechaAppDesp.text?.clear()
            etFechaProxDesp.text?.clear()
            etComentariosDesp.text?.clear()


        }
    }
}