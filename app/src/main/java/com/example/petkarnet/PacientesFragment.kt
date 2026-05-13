package com.example.petkarnet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AlertDialog
import android.content.Intent



class PacientesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pacientes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enlazamos las tarjetas de expedientes
        val cardPaciente1 = view.findViewById<MaterialCardView>(R.id.card_paciente_1)
        val cardPaciente2 = view.findViewById<MaterialCardView>(R.id.card_paciente_2)
        val fabAltaPaciente = view.findViewById<FloatingActionButton>(R.id.fab_alta_paciente)

        // Enlazamos los filtros
        val filtroPerros = view.findViewById<MaterialButton>(R.id.filtro_perros)
        val filtroGatos = view.findViewById<MaterialButton>(R.id.filtro_gatos)


        fabAltaPaciente.setOnClickListener {
            mostrarOpcionesDeAlta()

        }

        // Lógica de apertura de expediente
        cardPaciente1.setOnClickListener {
            Toast.makeText(requireContext(), "Abriendo expediente clínico de Max...", Toast.LENGTH_SHORT).show()
        }

        cardPaciente2.setOnClickListener {
            Toast.makeText(requireContext(), "Abriendo expediente clínico de Luna...", Toast.LENGTH_SHORT).show()
        }

        // Lógica de filtros
        filtroPerros.setOnClickListener {
            Toast.makeText(requireContext(), "Filtrando: Solo Perros", Toast.LENGTH_SHORT).show()
        }

        filtroGatos.setOnClickListener {
            Toast.makeText(requireContext(), "Filtrando: Solo Gatos", Toast.LENGTH_SHORT).show()
        }

        // Lógica para dar de alta a un paciente nuevo

    }

    // Función para crear y mostrar la ventanita emergente
    private fun mostrarOpcionesDeAlta() {
        // 1. Inflamos el diseño de nuestro Custom Dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_opciones_alta, null)

        // 2. Creamos la alerta usando el diseño
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Hacemos que el fondo del cuadro sea transparente para que se vean los bordes curvos de nuestra tarjeta
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 3. Enlazamos las opciones dentro del diálogo
        val opcionQR = dialogView.findViewById<MaterialCardView>(R.id.opcion_qr)
        val opcionManual = dialogView.findViewById<MaterialCardView>(R.id.opcion_manual)

        // 4. Lógica si elige Escanear QR
        opcionQR.setOnClickListener {
            alertDialog.dismiss() // Cerramos la ventanita
            Toast.makeText(requireContext(), "Abriendo escáner de cámara...", Toast.LENGTH_SHORT).show()
            // Aquí en el futuro llamaremos a la cámara
        }

        // 5. Lógica si elige Registro Manual
        opcionManual.setOnClickListener {
            alertDialog.dismiss() // Cerramos la ventanita
            Toast.makeText(requireContext(), "Abriendo formulario de registro...", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), registro_mascota_vet::class.java)
            startActivity(intent)
        }

        // 6. Mostramos el diálogo en pantalla
        alertDialog.show()
    }
}


