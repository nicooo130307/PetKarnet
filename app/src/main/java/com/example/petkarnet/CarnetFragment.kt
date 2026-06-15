package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

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
                tvNombre.text = mascota.nombre ?: "Falta registrar"

                // --- MAGIA DE LA FECHA AQUÍ ---
                if (!mascota.fecha_nacimiento.isNullOrBlank()) {
                    tvEdad.text = calcularEdadYFormatearFecha(mascota.fecha_nacimiento)
                } else {
                    tvEdad.text = "Falta registrar"
                }

                tvEspecie.text = mascota.especie?.replaceFirstChar { it.uppercase() } ?: "Falta registrar"
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
                Toast.makeText(requireContext(), "Error de datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // --- FUNCIÓN HELPER PARA LA FECHA Y EDAD ---
    private fun calcularEdadYFormatearFecha(fechaISO: String): String {
        return try {
            // 1. Convertir el texto que manda Node.js a un objeto Date de Java
            val formatoEntrada = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            formatoEntrada.timeZone = TimeZone.getTimeZone("UTC")
            val fechaNacimiento = formatoEntrada.parse(fechaISO) ?: return fechaISO

            // 2. Darle el formato bonito para mostrar (ej: 01/06/2026)
            val formatoSalida = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaBonita = formatoSalida.format(fechaNacimiento)

            // 3. Calcular la edad matemática
            val nacimiento = Calendar.getInstance().apply { time = fechaNacimiento }
            val hoy = Calendar.getInstance()

            var anios = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
            var meses = hoy.get(Calendar.MONTH) - nacimiento.get(Calendar.MONTH)
            var dias = hoy.get(Calendar.DAY_OF_MONTH) - nacimiento.get(Calendar.DAY_OF_MONTH)

            // Ajuste matemático si los días o meses son negativos
            if (dias < 0) {
                meses--
                val mesAnterior = Calendar.getInstance()
                mesAnterior.add(Calendar.MONTH, -1)
                dias += mesAnterior.getActualMaximum(Calendar.DAY_OF_MONTH)
            }
            if (meses < 0) {
                anios--
                meses += 12
            }

            // 4. Decidir qué texto mostrar según la edad
            val textoEdad = when {
                anios > 0 -> "$anios año(s)"
                meses > 0 -> "$meses mes(es)"
                dias > 0 -> "$dias día(s)"
                else -> "Recién nacido"
            }

            // Retornamos la combinación perfecta
            "$textoEdad ($fechaBonita)"

        } catch (e: Exception) {
            // Si por alguna razón la fecha llega en otro formato y falla, la mostramos tal cual
            fechaISO
        }
    }
}