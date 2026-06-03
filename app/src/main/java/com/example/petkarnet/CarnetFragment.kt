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
    private lateinit var tvNombre: TextView
    private lateinit var tvDetalles: TextView
    private lateinit var tvDueno: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var tvDireccion: TextView
    private lateinit var progressBar: ProgressBar

    // Añadimos una referencia al icono de verificado si lo tienes en tu XML
    private var ivIconoVerificado: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_carnet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivFoto = view.findViewById(R.id.iv_perfil_mascota_carnet)
        tvNombre = view.findViewById(R.id.tv_nombre_mascota_carnet)
        tvDetalles = view.findViewById(R.id.tv_detalles_mascota_carnet)
        tvDueno = view.findViewById(R.id.tv_dueno_carnet)
        tvTelefono = view.findViewById(R.id.tv_telefono_carnet)
        tvDireccion = view.findViewById(R.id.tv_direccion_carnet)
        progressBar = view.findViewById(R.id.progress_bar_carnet)

        // Si tienes un icono de "check" en tu layout, enlázalo aquí
        // ivIconoVerificado = view.findViewById(R.id.iv_verificado_dueno)

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
                // Gracias al cambio a 'Any?' en Usuario.kt, esto ya no lanzará IllegalStateException
                val respuestaPerfil = api.perfil()
                val dueno = if (respuestaPerfil.isSuccessful) respuestaPerfil.body() else null

                progressBar.visibility = View.GONE

                // 3. Actualizar UI de la Mascota
                tvNombre.text = mascota.nombre
                tvDetalles.text = "${mascota.raza ?: "Sin raza"} • ${mascota.fecha_nacimiento ?: "Edad desconocida"}"

                if (!mascota.foto.isNullOrBlank()) {
                    Glide.with(this@CarnetFragment)
                        .load(mascota.foto)
                        .placeholder(R.drawable.ic_huella)
                        .into(ivFoto)
                } else {
                    ivFoto.setImageResource(R.drawable.ic_huella)
                }

                // 4. Datos del dueño y Verificación
                val nombreDueno = dueno?.nombre ?: "No registrado"
                val telefono = dueno?.telefono ?: "Sin teléfono"
                val direccion = dueno?.direccion ?: "Sin dirección"

                // AQUI ESTÁ EL CAMBIO IMPORTANTE:
                // Usamos la función isVerificado() que creamos en el modelo
                if (dueno != null && dueno.isVerificado()) {
                    tvDueno.text = "👤 Dueño: $nombreDueno ✓ (Verificado)"
                    // Si tienes un icono, podrías mostrarlo:
                    ivIconoVerificado?.visibility = View.VISIBLE
                } else {
                    tvDueno.text = "👤 Dueño: $nombreDueno"
                    ivIconoVerificado?.visibility = View.GONE
                }

                tvTelefono.text = "📞 Tel: $telefono"
                tvDireccion.text = "🏠 Dirección: $direccion"

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                // Si el error persiste, aquí lo capturamos sin que la App se cierre
                Toast.makeText(requireContext(), "Error de datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}