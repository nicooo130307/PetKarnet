package com.example.petkarnet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class inicio_veterinario : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout para este fragmento
        return inflater.inflate(R.layout.fragment_inicio_veterinario, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Enlazamos las vistas
        val etBuscar = view.findViewById<TextInputEditText>(R.id.et_buscar_paciente)
        val btnEscanearQR = view.findViewById<MaterialButton>(R.id.btn_escanear_qr)

        // 2. Lógica del Botón QR
        btnEscanearQR.setOnClickListener {
            // Aquí en el futuro abriremos la cámara usando la librería de ZXing o ML Kit de Google
            Toast.makeText(requireContext(), "Iniciando cámara para escanear QR...", Toast.LENGTH_LONG).show()
        }

        // 3. Lógica del Buscador (simulamos que presiona buscar)
        // Puedes agregar un botón o un evento al teclado más adelante, por ahora un clic simple en el campo
        etBuscar.setOnClickListener {
            Toast.makeText(requireContext(), "Escribe el nombre del paciente o dueño", Toast.LENGTH_SHORT).show()
        }
    }
}