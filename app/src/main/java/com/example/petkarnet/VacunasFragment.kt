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

    // Variable global para guardar el ID y que el botón lo pueda usar
    private var idMascotaActual: Int = -1

    // 1. EL ÁLBUM IDEAL: Esta es la lista de las vacunas base que queremos que el usuario complete
    private val vacunasBase = listOf(
        "Rabia",
        "Parvovirus",
        "Moquillo",
        "Leptospirosis",
        "Adenovirus",
        "Desparasitación"
    )

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

        // PRO-TIP: El botón se inicializa una sola vez al crear la vista
        val fabAgregar = view.findViewById<FloatingActionButton>(R.id.fab_agregar_vacuna)
        fabAgregar.setOnClickListener {
            if (idMascotaActual != -1) {
                val intent = Intent(requireContext(), Agregar_Vacuna::class.java)
                intent.putExtra("ID_MASCOTA", idMascotaActual)
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

                // 2. Primero obtenemos a la mascota
                val respuestaMascotas = api.listarMascotas()
                if (!respuestaMascotas.isSuccessful || respuestaMascotas.body().isNullOrEmpty()) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "No tienes mascotas registradas", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val mascota = respuestaMascotas.body()!!.first()

                // Actualizamos nuestra variable global con el ID real
                idMascotaActual = mascota.id

                // 3. Obtenemos el historial real desde tu backend usando tu nuevo endpoint
                val respuestaHistorial = api.obtenerHistorial(mascota.id)

                if (!respuestaHistorial.isSuccessful) {
                    Toast.makeText(requireContext(), "Error API: Código ${respuestaHistorial.code()}", Toast.LENGTH_LONG).show()
                }
                val historialReal = if (respuestaHistorial.isSuccessful) respuestaHistorial.body() ?: emptyList() else emptyList()
                Toast.makeText(requireContext(), "Vacunas descargadas: ${historialReal.size}", Toast.LENGTH_LONG).show()
                // 4. LA MAGIA: Cruzamos la lista ideal con el historial real
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