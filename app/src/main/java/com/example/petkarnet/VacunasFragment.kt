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

    // 1. EL ÁLBUM IDEAL: Esta es la lista de las vacunas base que queremos que el usuario complete
    private val vacunasBase = listOf(
        "Rabia",
        "Parvovirus",
        "Moquillo",
        "Leptospirosis",
        "Adenovirus",
        "Desparasitación"
    )

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

        cargarAlbumVacunas()
    }

    private fun cargarAlbumVacunas() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(requireContext())

                // 2. Primero obtenemos a la mascota (Usamos la misma lógica que en CarnetFragment)
                val respuestaMascotas = api.listarMascotas()
                if (!respuestaMascotas.isSuccessful || respuestaMascotas.body().isNullOrEmpty()) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "No tienes mascotas registradas", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val mascota = respuestaMascotas.body()!!.first()


                val fabAgregar = requireView().findViewById<FloatingActionButton>(R.id.fab_agregar_vacuna)
                fabAgregar.setOnClickListener {
                    val intent = Intent(requireContext(), Agregar_Vacuna::class.java)
                    intent.putExtra("ID_MASCOTA", mascota.id) // Le mandamos el ID por debajo del agua
                    startActivity(intent)
                }

                // 3. Obtenemos el historial real desde tu backend usando tu nuevo endpoint
                val respuestaHistorial = api.obtenerHistorial(mascota.id)
                val historialReal = if (respuestaHistorial.isSuccessful) respuestaHistorial.body() ?: emptyList() else emptyList()

                // 4. LA MAGIA: Cruzamos la lista ideal con el historial real
                val listaSellos = vacunasBase.map { nombreIdeal ->

                    // Buscamos si en la base de datos hay una vacuna que se llame igual
                    // (Usamos ignoreCase = true por si en tu BD guardaron "rabia" en minúsculas)
                    val registroEncontrado = historialReal.find {
                        it.tipo_vacuna.equals(nombreIdeal, ignoreCase = true)
                    }

                    // Empaquetamos ambos datos. Si no la encontró, "registroEncontrado" valdrá null
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