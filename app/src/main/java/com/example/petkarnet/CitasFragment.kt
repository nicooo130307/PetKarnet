package com.example.petkarnet

import android.app.AlertDialog
import android.content.Context
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
import com.example.petkarnet.data.model.Cita
import com.example.petkarnet.data.model.EstadoCitaRequest
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class CitasFragment : Fragment() {

    private lateinit var rvCitas: RecyclerView
    private lateinit var layoutVacio: View
    private lateinit var adapter: CitaAdapter
    private lateinit var mis_citas: TextView

    // Variable global para guardar el ID de la mascota activa
    private var idMascotaActiva: Int = -1

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

        // 1. Leemos la mascota activa desde la "memoria" de SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("PetKarnetPrefs", Context.MODE_PRIVATE)
        idMascotaActiva = sharedPref.getInt("ID_MASCOTA_ACTIVA", -1)

        fabAgregar.setOnClickListener {
            if (idMascotaActiva != -1) {
                val intent = Intent(requireContext(), AgregarCita::class.java)
                // 2. Mandamos el ID al formulario para que asigne la cita a la mascota correcta
                intent.putExtra("ID_MASCOTA", idMascotaActiva)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "Por favor, selecciona una mascota primero", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar RecyclerView
        rvCitas.layoutManager = LinearLayoutManager(requireContext())

        adapter = CitaAdapter(emptyList()) { citaSeleccionada ->
            mostrarBottomSheetDetalle(citaSeleccionada)
        }
        rvCitas.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // Moví cargarCitas() a onResume para que la lista se refresque
        // automáticamente si el usuario regresa de "AgregarCita"
        cargarCitas()
    }

    private fun cargarCitas() {
        // Bloqueo de seguridad: si no hay mascota activa, no mostramos nada
        if (idMascotaActiva == -1) {
            mis_citas.visibility = View.GONE
            layoutVacio.visibility = View.VISIBLE
            rvCitas.visibility = View.GONE
            return
        }

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(requireContext())
                val respuesta = api.listarCitas()

                if (respuesta.isSuccessful) {
                    val todasLasCitas = respuesta.body() ?: emptyList()

                    // 3. EL FILTRO INTELIGENTE: Nos quedamos solo con las citas de esta mascota
                    // Nota: Asegúrate de que en tu data class 'Cita' tengas la variable 'id_mascota'
                    val citasFiltradas = todasLasCitas.filter { it.id_mascota == idMascotaActiva }

                    if (citasFiltradas.isNotEmpty()) {
                        mis_citas.visibility = View.VISIBLE
                        layoutVacio.visibility = View.GONE
                        rvCitas.visibility = View.VISIBLE

                        adapter = CitaAdapter(citasFiltradas) { citaSeleccionada ->
                            mostrarBottomSheetDetalle(citaSeleccionada)
                        }
                        rvCitas.adapter = adapter
                    } else {
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

        tvMascota.text = "🐾 Mascota: ${cita.mascota_nombre ?: "Desconocida"}"
        tvVeterinario.text = "👨‍⚕️ Veterinario: ${cita.veterinario_nombre ?: "No asignado"}"
        tvFechaHora.text = "📅 Fecha y Hora: ${cita.fecha_hora}"
        tvMotivo.text = "🩺 Motivo: ${cita.tipo_cita}"

        btnCancelar.setOnClickListener {
            bottomSheetDialog.dismiss()
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
                    cargarCitas()
                } else {
                    Toast.makeText(requireContext(), "Error al cancelar la cita", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}