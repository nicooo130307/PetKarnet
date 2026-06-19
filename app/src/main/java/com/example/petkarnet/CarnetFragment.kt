package com.example.petkarnet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.common.BitMatrix
import com.example.petkarnet.util.LoadingManager

class CarnetFragment : Fragment() {
    private lateinit var ivQR: ImageView
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

        ivQR = view.findViewById(R.id.iv_qr_carnet)


        val fabEditar = view.findViewById<FloatingActionButton>(R.id.fab_editar_carnet)
        fabEditar.setOnClickListener {
            val intent = Intent(requireContext(), EditarCarnet::class.java)
            startActivity(intent)
        }

        cargarCarnet()
    }

    private fun cargarCarnet() {
        LoadingManager.showLoading(requireActivity(), "Cargando el carnet médico...")


        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(requireContext())

                // 1. Obtener la lista de mascotas
                // ... dentro de cargarCarnet(), justo después de obtener la lista de mascotas:
                val respuestaMascotas = api.listarMascotas()
                if (!respuestaMascotas.isSuccessful || respuestaMascotas.body().isNullOrEmpty()) {
                   LoadingManager.hideLoading(requireActivity())
                    Toast.makeText(requireContext(), "No tienes mascotas registradas", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val sharedPref = requireContext().getSharedPreferences("PetKarnetPrefs", Context.MODE_PRIVATE)
                val idMascotaActiva = sharedPref.getInt("ID_MASCOTA_ACTIVA", -1)

                val mascota = respuestaMascotas.body()!!.find { it.id == idMascotaActiva } ?: respuestaMascotas.body()!!.first()



                val respuestaPerfil = api.perfil()
                val dueno = if (respuestaPerfil.isSuccessful) respuestaPerfil.body() else null

                LoadingManager.hideLoading(requireActivity())

                // 3. Actualizar UI de la Mascota
                tvNombre.text = mascota.nombre ?: "Falta registrar"


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

                // Generar QR para compartir
                val urlPublica = "https://petkarnet.onrender.com/api/mascotas/${mascota.id}/publico"
                val qrBitmap = generarQR(urlPublica)
                ivQR.setImageBitmap(qrBitmap)


            } catch (e: Exception) {
                LoadingManager.hideLoading(requireActivity())
                Toast.makeText(requireContext(), "Error de datos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun calcularEdadYFormatearFecha(fechaISO: String): String {
        return try {

            val formatoEntrada = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            formatoEntrada.timeZone = TimeZone.getTimeZone("UTC")
            val fechaNacimiento = formatoEntrada.parse(fechaISO) ?: return fechaISO


            val formatoSalida = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaBonita = formatoSalida.format(fechaNacimiento)


            val nacimiento = Calendar.getInstance().apply { time = fechaNacimiento }
            val hoy = Calendar.getInstance()

            var anios = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
            var meses = hoy.get(Calendar.MONTH) - nacimiento.get(Calendar.MONTH)
            var dias = hoy.get(Calendar.DAY_OF_MONTH) - nacimiento.get(Calendar.DAY_OF_MONTH)


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

    private fun generarQR(contenido: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix: BitMatrix = writer.encode(contenido, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }
}