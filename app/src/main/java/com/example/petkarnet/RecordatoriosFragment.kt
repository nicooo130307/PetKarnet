package com.example.petkarnet

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
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class RecordatoriosFragment : Fragment() {

    private lateinit var rvCitas: RecyclerView
    private lateinit var layoutVacio: View
    private lateinit var adapter: CitaAdapter

    private lateinit var mis_citas : TextView

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
        adapter = CitaAdapter(emptyList())
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
                        adapter = CitaAdapter(citas)
                        rvCitas.adapter = adapter
                    } else {
                        // Sin citas: mostrar estado vacío, ocultar RecyclerView
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
}