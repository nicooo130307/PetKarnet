package com.example.petkarnet

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petkarnet.data.model.Cita // Asegúrate de que esta importación coincida con tu modelo
import com.example.petkarnet.data.model.EstadoCitaRequest
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class RecordatoriosFragment : Fragment() {

    private lateinit var rvCitas: RecyclerView
    private lateinit var layoutVacio: View
    private lateinit var adapter: CitaAdapter
    private lateinit var mis_citas: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recordatorios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mis_citas = view.findViewById(R.id.tv_titulo_mis_citas)
        rvCitas = view.findViewById(R.id.rv_citas)
        layoutVacio = view.findViewById(R.id.layout_estado_vacio)
        val fabAgregar = view.findViewById<FloatingActionButton>(R.id.fab_agregar_cita)

        fabAgregar.setOnClickListener {
            startActivity(Intent(requireContext(), AgregarCita::class.java))
        }

        // Configurar RecyclerView
        rvCitas.layoutManager = LinearLayoutManager(requireContext())

        // Inicializamos el adapter pasándole la lista vacía y la acción del clic
        adapter = CitaAdapter(emptyList()) { citaSeleccionada ->
            mostrarBottomSheetDetalle(citaSeleccionada)
        }
        rvCitas.adapter = adapter

        cargarCitas()
    }

    private fun cargarCitas() {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(requireContext())
                val respuesta = api.listarCitas()
                if (respuesta.isSuccessful) {
                    val citas = respuesta.body() ?: emptyList()
                    if (citas.isNotEmpty()) {
                        // Hay citas: ocultar estado vacío, mostrar RecyclerView
                        mis_citas.visibility = View.VISIBLE
                        layoutVacio.visibility = View.GONE
                        rvCitas.visibility = View.VISIBLE

                        // Actualizamos el adapter con las citas y la acción de clic
                        adapter = CitaAdapter(citas) { citaSeleccionada ->
                            mostrarBottomSheetDetalle(citaSeleccionada)
                        }
                        rvCitas.adapter = adapter
                    } else {
                        // Sin citas: mostrar estado vacío, ocultar RecyclerView
                        mis_citas.visibility = View.GONE
                        layoutVacio.visibility = View.VISIBLE
                        rvCitas.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al cargar citas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarBottomSheetDetalle(cita: Cita) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_detalle_cita, null)

        val tvMascota = view.findViewById<TextView>(R.id.tv_bs_mascota)
        val tvVeterinario = view.findViewById<TextView>(R.id.tv_bs_veterinario)
        val tvFechaHora = view.findViewById<TextView>(R.id.tv_bs_fecha_hora)
        val tvMotivo = view.findViewById<TextView>(R.id.tv_bs_motivo)
        val btnCancelar = view.findViewById<MaterialButton>(R.id.btn_bs_cancelar_cita)

        // Nota: Asegúrate de que los nombres de las variables (nombreMascota, nombreVeterinario, etc.)
        // coincidan exactamente con cómo las tienes declaradas en tu data class Cita
        tvMascota.text = "🐾 Mascota: ${cita.mascota_nombre ?: "Desconocida"}"
        tvVeterinario.text = "👨‍⚕️ Veterinario: ${cita.veterinario_nombre ?: "No asignado"}"
        tvFechaHora.text = "📅 Fecha y Hora: ${cita.fecha_hora}"
        tvMotivo.text = "🩺 Motivo: ${cita.tipo_cita}"

        btnCancelar.setOnClickListener {
            bottomSheetDialog.dismiss() // Cerramos el panel inferior
            mostrarDialogoConfirmacion(cita.id, cita.mascota_nombre)
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun mostrarDialogoConfirmacion(idCita: Int, nombreMascota: String?) {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancelar Cita")
            .setMessage("¿Estás seguro de que deseas cancelar la cita de ${nombreMascota ?: "esta mascota"}? Esta acción no se puede deshacer.")
            .setPositiveButton("Sí, cancelar") { dialog, _ ->
                dialog.dismiss()
                ejecutarCancelacion(idCita)
            }
            .setNegativeButton("No, mantener", null)
            .show()
    }

    private fun ejecutarCancelacion(idCita: Int) {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(requireContext())

                val cuerpo = EstadoCitaRequest(estado = "cancelada")
                val respuesta = api.cancelarCita(idCita, cuerpo)

                if (respuesta.isSuccessful) {
                    Toast.makeText(requireContext(), "Cita cancelada correctamente", Toast.LENGTH_SHORT).show()
                    cargarCitas() // Recargamos la lista para que desaparezca la cita cancelada
                } else {
                    Toast.makeText(requireContext(), "Error al cancelar la cita", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}