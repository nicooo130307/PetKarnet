package com.example.petkarnet

import android.content.Context
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


    private lateinit var tvNombreUsuario: TextView
    private lateinit var tvRolUsuario: TextView
    private lateinit var tvContadorMascotas: TextView
    private lateinit var tvContadorVacunas: TextView
    private lateinit var tvContadorCitas: TextView




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvNombreUsuario = view.findViewById(R.id.tv_nombre_usuario)
        tvRolUsuario = view.findViewById(R.id.tv_rol_usuario)
        tvContadorMascotas = view.findViewById(R.id.tv_contador_mascotas)
        tvContadorVacunas = view.findViewById(R.id.tv_contador_vacunas)
        tvContadorCitas = view.findViewById(R.id.tv_contador_citas)



        val opcionInfo = view.findViewById<TextView>(R.id.opcion_info)
        val opcionMascotas = view.findViewById<TextView>(R.id.opcion_mascotas)
        val opcionAcerca = view.findViewById<TextView>(R.id.opcion_acerca)
        val opcionConfig = view.findViewById<TextView>(R.id.opcion_configuracion)
        val btnCerrarSesion = view.findViewById<MaterialButton>(R.id.btn_cerrar_sesion)


        cargarDatosUsuario()



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


    private fun cargarDatosUsuario() {
        val prefs = requireContext().getSharedPreferences("petkarnet_prefs", Context.MODE_PRIVATE)
        val nombre = prefs.getString("usuario_nombre", "Usuario") ?: "Usuario"
        val rol = prefs.getString("usuario_rol", "dueño") ?: "dueño"

        // Mostrar nombre
        tvNombreUsuario.text = nombre

        // Mostrar rol de forma amigable
        tvRolUsuario.text = when (rol) {
            "veterinario" -> "Veterinario"
            "admin" -> "Administrador"
            else -> "Dueño Propietario"
        }

        // (Opcional) Aquí podrías llamar a la API para obtener los contadores reales
        // cargarContadores()
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
        val prefs = requireContext().getSharedPreferences("petkarnet_prefs", Context.MODE_PRIVATE)
        val nombre = prefs.getString("usuario_nombre", "Usuario") ?: "Usuario"

        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas salir de tu cuenta, $nombre?")
            .setPositiveButton("Sí, salir") { dialog, _ ->
                // Limpiar SharedPreferences
                prefs.edit().clear().apply()

                // Redirigir al login (MainActivity u otra)
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
                Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}