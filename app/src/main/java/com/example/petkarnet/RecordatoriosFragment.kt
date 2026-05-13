package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RecordatoriosFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recordatorios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Buscamos el botón de agregar
        val fabAgregar = view.findViewById<FloatingActionButton>(R.id.fab_agregar_cita)

        // Al tocarlo, viajamos a la nueva Activity
        fabAgregar.setOnClickListener {
            val intent = Intent(requireContext(), AgregarCita::class.java)
            startActivity(intent)
        }
    }
}