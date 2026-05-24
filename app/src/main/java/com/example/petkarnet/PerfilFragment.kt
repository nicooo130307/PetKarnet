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

class PerfilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Buscamos todos los elementos de la vista por su ID
        val opcionInfo = view.findViewById<TextView>(R.id.opcion_info)
        val opcionMascotas = view.findViewById<TextView>(R.id.opcion_mascotas)
        val opcionAcerca = view.findViewById<TextView>(R.id.opcion_acerca)
        val opcionConfig = view.findViewById<TextView>(R.id.opcion_configuracion)
        val btnCerrarSesion = view.findViewById<MaterialButton>(R.id.btn_cerrar_sesion)

        // 2. Lógica de "Mi Información"
        opcionInfo.setOnClickListener {
            Toast.makeText(requireContext(), "Abriendo edición de perfil...", Toast.LENGTH_SHORT).show()
        }


        opcionInfo.setOnClickListener {
            val intent = Intent(requireContext(), mi_informacion::class.java)
            Toast.makeText(requireContext(), "Abriendo edición de perfil...", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        // 3. Lógica de "Mis Mascotas"
        opcionMascotas.setOnClickListener {

            val intent = Intent(requireContext(), Mis_mascotas::class.java)
            Toast.makeText(requireContext(), "Abriendo mis mascotas...", Toast.LENGTH_SHORT).show()
            startActivity(intent)

        }

        // 4. Lógica de "Acerca de PetKarnet" (Abre ventana emergente)
        opcionAcerca.setOnClickListener {
            mostrarDialogoAcercaDe()
        }

        // 5. Lógica de "Configuración"
        opcionConfig.setOnClickListener {
            val intent = Intent(requireContext(), Configuracion::class.java)
            Toast.makeText(requireContext(), "Abriendo configuración del sistema...", Toast.LENGTH_SHORT).show()
            startActivity(intent)

        }

        // 6. Lógica de "Cerrar Sesión" (Abre ventana de confirmación)
        btnCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }
    }

    // --- FUNCIONES PARA CREAR LAS VENTANAS EMERGENTES ---

    private fun mostrarDialogoAcercaDe() {
        AlertDialog.Builder(requireContext())

            .setTitle("Acerca de PetKarnet")
            .setMessage("Versión 1.0\n\nDesarrollado orgullosamente en el CECyT 5 para digitalizar el cuidado de las mascotas.")
            .setPositiveButton("¡Genial!") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas salir de tu cuenta, Nicolas?")
            .setPositiveButton("Sí, salir") { dialog, _ ->
                Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}