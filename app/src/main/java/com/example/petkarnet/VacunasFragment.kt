package com.example.petkarnet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class VacunasFragment : Fragment() {

    private lateinit var rvVacunas: RecyclerView
    private lateinit var progressBar: ProgressBar

    // Variables globales para que el botón las pueda usar
    private var idMascotaActual: Int = -1
    private var especieMascotaActual: String = "" // <-- NUEVA VARIABLE

    override fun onResume() {
        super.onResume()
        // onResume se ejecuta CADA VEZ que la pantalla vuelve a ser visible para el usuario
        cargarAlbumVacunas()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vacunas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvVacunas = view.findViewById(R.id.rv_album_vacunas)
        progressBar = view.findViewById(R.id.progress_bar_vacunas)

        // Reforzamos el formato de cuadrícula de 2 columnas
        rvVacunas.layoutManager = GridLayoutManager(requireContext(), 2)

        val fabAgregar = view.findViewById<FloatingActionButton>(R.id.fab_agregar_vacuna)
        fabAgregar.setOnClickListener {
            if (idMascotaActual != -1) {
                val intent = Intent(requireContext(), Agregar_Vacuna::class.java)
                intent.putExtra("ID_MASCOTA", idMascotaActual)
                intent.putExtra("ESPECIE_MASCOTA", especieMascotaActual) // <-- LE MANDAMOS LA ESPECIE A LA OTRA PANTALLA
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Cargando datos de la mascota...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarAlbumVacunas() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(requireContext())

                val sharedPref = requireContext().getSharedPreferences("PetKarnetPrefs", Context.MODE_PRIVATE)
                idMascotaActual = sharedPref.getInt("ID_MASCOTA_ACTIVA", -1)
                especieMascotaActual = sharedPref.getString("ESPECIE_MASCOTA_ACTIVA", "Perro") ?: "Perro"

                if (idMascotaActual == -1) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Por favor, selecciona una mascota primero", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val vacunasBase = if (especieMascotaActual.equals("gato", ignoreCase = true)) {
                    listOf("Rabia", "Triple Felina", "Leucemia Felina", "Desparasitación")
                } else {
                    listOf("Rabia", "Parvovirus", "Moquillo", "Leptospirosis", "Adenovirus", "Desparasitación")
                }

                val respuestaHistorial = api.obtenerHistorial(idMascotaActual)


                if (!respuestaHistorial.isSuccessful) {
                    Toast.makeText(requireContext(), "Error API: Código ${respuestaHistorial.code()}", Toast.LENGTH_LONG).show()
                }

                val historialReal = if (respuestaHistorial.isSuccessful) respuestaHistorial.body() ?: emptyList() else emptyList()

                // 4. Cruzamos la lista ideal elegida con el historial real
                val listaSellos = vacunasBase.map { nombreIdeal ->
                    val registroEncontrado = historialReal.find {
                        it.tipo_vacuna.equals(nombreIdeal, ignoreCase = true)
                    }
                    SelloVacuna(nombreIdeal, registroEncontrado)
                }

                // 5. Se lo pasamos a nuestro Adapter incluyendo la acción del clic
                val adapter = VacunaAdapter(listaSellos) { selloSeleccionado ->
                    mostrarBottomSheetDetalle(selloSeleccionado)
                }
                rvVacunas.adapter = adapter

                progressBar.visibility = View.GONE

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun mostrarBottomSheetDetalle(sello: SelloVacuna) {
        val bottomSheetDialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val vistaBS = layoutInflater.inflate(R.layout.bottom_sheet_detalle_vacuna, null)

        val tvNombre = vistaBS.findViewById<TextView>(R.id.tv_bs_nombre_vacuna)
        val tvEstado = vistaBS.findViewById<TextView>(R.id.tv_bs_estado_vacuna)
        val tvFechaAplicacion = vistaBS.findViewById<TextView>(R.id.tv_bs_fecha_aplicacion)
        val tvProximaDosis = vistaBS.findViewById<TextView>(R.id.tv_bs_proxima_dosis)
        val tvNotas = vistaBS.findViewById<TextView>(R.id.tv_bs_notas_vacuna)
        val btnCerrar = vistaBS.findViewById<View>(R.id.btn_bs_cerrar_vacuna)

        tvNombre.text = "Vacuna: ${sello.nombreIdeal}"

        val historial = sello.registroReal

        if (historial != null) {
            // Caso 1: La vacuna SÍ está aplicada
            tvEstado.text = "Estado: Aplicada ✓"
            tvEstado.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
            tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"))

            // Formateamos las fechas de YYYY-MM-DD (Base de datos) a DD/MM/YYYY para el usuario
            tvFechaAplicacion.text = "📅 Fecha de aplicación: ${formatearFechaAMostrar(historial.fecha_aplicacion)}"

            if (!historial.proxima_dosis.isNullOrBlank()) {
                tvProximaDosis.text = "⏳ Próxima dosis: ${formatearFechaAMostrar(historial.proxima_dosis)}"
            } else {
                tvProximaDosis.text = "⏳ Próxima dosis: No requerida"
            }

            tvNotas.text = "📝 Notas: ${historial.notas ?: "Sin anotaciones adicionales."}"
        } else {
            // Caso 2: La vacuna ESTÁ PENDIENTE
            tvEstado.text = "Estado: Pendiente ⏳"
            tvEstado.setTextColor(android.graphics.Color.parseColor("#C62828"))
            tvEstado.setBackgroundColor(android.graphics.Color.parseColor("#FFEBEE"))

            tvFechaAplicacion.text = "📅 Fecha de aplicación: Pendiente de registrar"
            tvProximaDosis.text = "⏳ Próxima dosis: —"
            tvNotas.text = "📝 Notas: Esta inmunización aún no ha sido administrada por tu veterinario."
        }

        btnCerrar.setOnClickListener { bottomSheetDialog.dismiss() }

        bottomSheetDialog.setContentView(vistaBS)
        bottomSheetDialog.show()
    }

    // Función helper rápida para poner las fechas bonitas en el panel
    private fun formatearFechaAMostrar(fechaSQL: String?): String {
        if (fechaSQL.isNullOrBlank()) return "—"
        return try {
            // Si viene con formato ISO completo de Node.js o solo fecha
            val formatoEntrada = if (fechaSQL.contains("T")) {
                java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault()).apply {
                    timeZone = java.util.TimeZone.getTimeZone("UTC")
                }
            } else {
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            }

            val date = formatoEntrada.parse(fechaSQL)
            val formatoSalida = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            formatoSalida.format(date!!)
        } catch (e: Exception) {
            fechaSQL // Respaldo por si viene con otro formato
        }
    }

}