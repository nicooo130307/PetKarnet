package com.example.petkarnet

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.petkarnet.data.network.RetrofitClient
import com.example.petkarnet.util.CloudinaryManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch

class PerfilFragment : Fragment() {

    private lateinit var tvNombreUsuario: TextView
    private lateinit var tvRolUsuario: TextView


    private lateinit var ivFotoPerfil: ShapeableImageView
    private var uriFotoSeleccionada: Uri? = null

    private val abrirGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uriFotoSeleccionada = uri
            ivFotoPerfil.setImageURI(uri)
            subirFotoPerfil(uri)
        }
    }

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
        ivFotoPerfil = view.findViewById(R.id.iv_foto_perfil)

        ivFotoPerfil.setOnClickListener {
            abrirGaleria.launch("image/*")
        }

        val opcionInfo = view.findViewById<TextView>(R.id.opcion_info)
        val opcionMascotas = view.findViewById<TextView>(R.id.opcion_mascotas)
        val opcionAcerca = view.findViewById<TextView>(R.id.opcion_acerca)
        val opcionConfig = view.findViewById<TextView>(R.id.opcion_configuracion)
        val btnCerrarSesion = view.findViewById<MaterialButton>(R.id.btn_cerrar_sesion)
        val opcionTerminos = view.findViewById<TextView>(R.id.opcion_terminos)

        cargarDatosUsuario()

        // Lógica de "Mi Información"
        opcionInfo.setOnClickListener {
            val intent = Intent(requireContext(), mi_informacion::class.java)
            startActivity(intent)
        }

        // Lógica de "Mis Mascotas"
        opcionMascotas.setOnClickListener {
            val intent = Intent(requireContext(), Mis_mascotas::class.java)
            startActivity(intent)
        }


        opcionAcerca.setOnClickListener {
            mostrarDialogoAcercaDe()
        }
        opcionTerminos.setOnClickListener {
            val intent = Intent(requireContext(), TerminosActivity::class.java)
            startActivity(intent)
        }

        // Lógica de "Configuración"
        opcionConfig.setOnClickListener {
            val intent = Intent(requireContext(), Configuracion::class.java)
            startActivity(intent)
        }

        // Lógica de "Cerrar Sesión"
        btnCerrarSesion.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }
    }

    private fun cargarDatosUsuario() {
        val prefs = requireContext().getSharedPreferences("petkarnet_prefs", Context.MODE_PRIVATE)
        val nombre = prefs.getString("usuario_nombre", "Usuario") ?: "Usuario"
        val rol = prefs.getString("usuario_rol", "dueño") ?: "dueño"
        val fotoUrl = prefs.getString("usuario_foto", null)

        tvNombreUsuario.text = nombre
        tvRolUsuario.text = when (rol) {
            "veterinario" -> "Veterinario"
            "admin" -> "Administrador"
            else -> "Dueño Propietario"
        }

        // Cargar foto de perfil con Glide
        if (!fotoUrl.isNullOrBlank()) {
            Glide.with(requireContext())
                .load(fotoUrl)
                .placeholder(R.drawable.ic_menu_camera)
                .error(R.drawable.ic_menu_camera)
                .into(ivFotoPerfil)
        }
    }

    // --- DIÁLOGOS ---

    private fun mostrarDialogoAcercaDe() {
        AlertDialog.Builder(requireContext())
            .setTitle("Acerca de PetKarnet")
            .setMessage("Versión 1.0\n\nDesarrollado orgullosamente en el CECyT 5 para digitalizar el cuidado de las mascotas.")
            .setPositiveButton("¡Genial!") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun mostrarDialogoCerrarSesion() {
        val prefs = requireContext().getSharedPreferences("petkarnet_prefs", Context.MODE_PRIVATE)
        val nombre = prefs.getString("usuario_nombre", "Usuario") ?: "Usuario"

        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que deseas salir de tu cuenta, $nombre?")
            .setPositiveButton("Sí, salir") { dialog, _ ->
                prefs.edit().clear().apply()

                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
                Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // --- SUBIR FOTO DE PERFIL ---

    private fun subirFotoPerfil(uri: Uri) {
        val imageFile = CloudinaryManager.getFileFromUri(requireContext(), uri)
        if (imageFile == null) {
            Toast.makeText(requireContext(), "No se pudo acceder a la imagen", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(requireContext(), "Subiendo foto...", Toast.LENGTH_SHORT).show()

        CloudinaryManager.uploadImage(imageFile) { url ->
            if (url != null) {
                guardarFotoPerfil(url)
            } else {
                Toast.makeText(requireContext(), "Error al subir la foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarFotoPerfil(urlFoto: String) {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(requireContext())
                val respuesta = api.actualizarFotoPerfil(mapOf("foto_perfil" to urlFoto))

                if (respuesta.isSuccessful) {
                    val prefs = requireContext().getSharedPreferences("petkarnet_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("usuario_foto", urlFoto).apply()

                    Toast.makeText(requireContext(), "¡Foto de perfil actualizada!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error al guardar la foto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}