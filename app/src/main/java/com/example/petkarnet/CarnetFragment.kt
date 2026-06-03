package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch

class CarnetFragment : Fragment() {

    private lateinit var ivFoto: ShapeableImageView

    // Variables para la mascota
    private lateinit var tvNombre: TextView
    private lateinit var tvEdad: TextView
    private lateinit var tvEspecie: TextView
    private lateinit var tvRaza: TextView
    private lateinit var tvSexo: TextView
    private lateinit var tvPeso: TextView

    // Variables para el dueño
    private lateinit var tvDueno: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var tvDireccion: TextView

    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_carnet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enlace de la UI de Mascota
        ivFoto = view.findViewById(R.id.iv_perfil_mascota_carnet)
        tvNombre = view.findViewById(R.id.tv_nombre_mascota_carnet)
        tvEdad = view.findViewById(R.id.tv_edad_carnet)
        tvEspecie = view.findViewById(R.id.tv_especie_carnet)
        tvRaza = view.findViewById(R.id.tv_raza_carnet)
        tvSexo = view.findViewById(R.id.tv_sexo_carnet)
        tvPeso = view.findViewById(R.id.tv_peso_carnet)

        // Enlace de la UI del Dueño
        tvDueno = view.findViewById(R.id.tv_dueno_carnet)
        tvTelefono = view.findViewById(R.id.tv_telefono_carnet)
        tvDireccion = view.findViewById(R.id.tv_direccion_carnet)

        progressBar = view.findViewById(R.id.progress_bar_carnet)

        val fabEditar = view.findViewById<FloatingActionButton>(R.id.fab_editar_carnet)
        fabEditar.setOnClickListener {
            val intent = Intent(requireContext(), EditarCarnet::class.java)
            startActivity(intent)
        }

        cargarCarnet()
    }

    private fun cargarCarnet() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(requireContext())

                // 1. Obtener la lista de mascotas
                val respuestaMascotas = api.listarMascotas()
                if (!respuestaMascotas.isSuccessful || respuestaMascotas.body().isNullOrEmpty()) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "No tienes mascotas registradas", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val mascota = respuestaMascotas.body()!!.first()

                // 2. Obtener el perfil del dueño
                val respuestaPerfil = api.perfil()
                val dueno = if (respuestaPerfil.isSuccessful) respuestaPerfil.body() else null

                progressBar.visibility = View.GONE

                // 3. Actualizar UI de la Mascota
                // Nota: Asegúrate de que tu modelo 'Mascota' tenga las variables especie, sexo y peso escritas así.
                tvNombre.text = mascota.nombre ?: "Falta registrar"
                tvEdad.text = mascota.fecha_nacimiento ?: "Falta registrar"
                tvEspecie.text = mascota.especie ?: "Falta registrar"
                tvRaza.text = mascota.raza ?: "Falta registrar"
                tvSexo.text = mascota.sexo ?: "Falta registrar"

                if (mascota.peso != null) {
                    tvPeso.text = "${mascota.peso} kg"
                } else {
                    tvPeso.text = "Falta registrar"
                }

                // Cargar imagen con Glide
                if (!mascota.foto.isNullOrBlank()) {
                    Glide.with(this@CarnetFragment)
                        .load(mascota.foto)
                        .placeholder(R.drawable.ic_huella)
                        .into(ivFoto)
                } else {
                    ivFoto.setImageResource(R.drawable.ic_huella)
                }

                // 4. Datos del dueño y Verificación
                val nombreDueno = dueno?.nombre ?: "Falta registrar"

                if (dueno != null && dueno.isVerificado()) {
                    tvDueno.text = "$nombreDueno ✓ (Verificado)"
                } else {
                    tvDueno.text = nombreDueno
                }

                tvTelefono.text = dueno?.telefono ?: "Falta registrar"
                tvDireccion.text = dueno?.direccion ?: "Falta registrar"

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                // Capturamos error de red
                Toast.makeText(requireContext(), "Error de datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}