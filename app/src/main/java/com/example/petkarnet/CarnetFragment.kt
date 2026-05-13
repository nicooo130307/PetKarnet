package com.example.petkarnet



import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CarnetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Dibujamos el nuevo carnet en modo lectura
        return inflater.inflate(R.layout.fragment_carnet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Buscamos el botón flotante
        val fabEditar = view.findViewById<FloatingActionButton>(R.id.fab_editar_carnet)

        // Al tocarlo, abrimos la pantalla de edición que creaste antes
        fabEditar.setOnClickListener {
            val intent = Intent(requireContext(), EditarCarnet::class.java)
            startActivity(intent)
        }
    }
}