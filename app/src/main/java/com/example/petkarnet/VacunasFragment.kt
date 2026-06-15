package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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

                // 1. Primero obtenemos a la mascota
                val respuestaMascotas = api.listarMascotas()
                if (!respuestaMascotas.isSuccessful || respuestaMascotas.body().isNullOrEmpty()) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "No tienes mascotas registradas", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val mascota = respuestaMascotas.body()!!.first()

                // Actualizamos nuestras variables globales con los datos reales
                idMascotaActual = mascota.id
                especieMascotaActual = mascota.especie ?: "Perro"

                // --- 2. EL ÁLBUM DINÁMICO (LA MAGIA DE LAS ESPECIES) ---
                val vacunasBase = if (especieMascotaActual.equals("gato", ignoreCase = true)) {
                    listOf(
                        "Rabia",
                        "Triple Felina",
                        "Leucemia Felina",
                        "Desparasitación"
                    )
                } else {
                    // Por defecto, perros (o la categoría "otro")
                    listOf(
                        "Rabia",
                        "Parvovirus",
                        "Moquillo",
                        "Leptospirosis",
                        "Adenovirus",
                        "Desparasitación"
                    )
                }

                // 3. Obtenemos el historial real desde tu backend usando tu endpoint
                val respuestaHistorial = api.obtenerHistorial(mascota.id)

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

                // 5. Se lo pasamos a nuestro Adapter para que lo dibuje
                val adapter = VacunaAdapter(listaSellos)
                rvVacunas.adapter = adapter

                progressBar.visibility = View.GONE

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}