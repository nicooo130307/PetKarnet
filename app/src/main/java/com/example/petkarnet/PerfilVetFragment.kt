package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton



class PerfilVetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil_vet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Enlazamos las vistas
        val btnDatosClinica = view.findViewById<TextView>(R.id.btn_datos_clinica)
        val btnHorarios = view.findViewById<TextView>(R.id.btn_horarios_servicios)
        val btnAcercaDe = view.findViewById<TextView>(R.id.btn_acerca_de_vet)
        val btnConfiguracion = view.findViewById<TextView>(R.id.btn_configuracion_vet)
        val btnCerrarSesion = view.findViewById<MaterialButton>(R.id.btn_cerrar_sesion_vet)

        // 2. Lógica de los botones del negocio
        btnDatosClinica.setOnClickListener {
            Toast.makeText(requireContext(), "Abriendo edición de datos y cédula...", Toast.LENGTH_SHORT).show()
        }

        btnHorarios.setOnClickListener {
            Toast.makeText(requireContext(), "Configurando horarios de atención...", Toast.LENGTH_SHORT).show()
        }

        // 3. Reutilizando pantallas (DRY - Don't Repeat Yourself)
        btnConfiguracion.setOnClickListener {
            // ¡Reutilizamos la Activity de Configuración que ya tienes!
            val intent = Intent(requireContext(), Configuracion::class.java)
            startActivity(intent)
        }

        btnAcercaDe.setOnClickListener {
            mostrarDialogoAcercaDe()
        }

        // 4. Lógica de Cerrar Sesión con confirmación
        btnCerrarSesion.setOnClickListener {
            mostrarAlertaCerrarSesion()
        }
    }

    private fun mostrarAlertaCerrarSesion() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas salir de tu cuenta de veterinario?")
            .setPositiveButton("Sí, salir") { _, _ ->
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun mostrarDialogoAcercaDe() {
        AlertDialog.Builder(requireContext())

            .setTitle("Acerca de PetKarnet")
            .setMessage("Versión 1.0\n\nDesarrollado orgullosamente en el CECyT 5 para digitalizar el cuidado de las mascotas.")
            .setPositiveButton("¡Genial!") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }



}

