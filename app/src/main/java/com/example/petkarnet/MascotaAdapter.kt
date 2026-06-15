package com.example.petkarnet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petkarnet.data.model.Mascota
import com.google.android.material.imageview.ShapeableImageView
import java.text.SimpleDateFormat
import java.util.*

class MascotaAdapter(
    private val listaMascotas: MutableList<Mascota>,
    private val onMascotaClick: (Mascota) -> Unit
) : RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_mascota, parent, false)
        return MascotaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = listaMascotas[position]
        holder.bind(mascota, onMascotaClick)
    }

    override fun getItemCount(): Int = listaMascotas.size

    // Funciones helper para gestionar la lista desde la Actividad
    fun getMascotaAt(position: Int): Mascota = listaMascotas[position]

    fun eliminarMascotaDeLista(position: Int) {
        listaMascotas.removeAt(position)
        notifyItemRemoved(position)
    }

    class MascotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivFoto: ShapeableImageView = itemView.findViewById(R.id.iv_foto_item_mascota)
        private val tvNombre: TextView = itemView.findViewById(R.id.tv_nombre_item_mascota)
        private val tvRaza: TextView = itemView.findViewById(R.id.tv_raza_item_mascota)
        private val tvEdadPeso: TextView = itemView.findViewById(R.id.tv_edad_peso_item_mascota)

        fun bind(mascota: Mascota, onMascotaClick: (Mascota) -> Unit) {
            tvNombre.text = mascota.nombre

            val especieLimpia = mascota.especie?.replaceFirstChar { it.uppercase() } ?: "Mascota"
            tvRaza.text = "$especieLimpia • ${mascota.raza ?: "Sin raza"}"

            val textoEdad = calcularEdadTexto(mascota.fecha_nacimiento)
            val textoPeso = if (mascota.peso != null) "${mascota.peso} kg" else "Sin peso"
            tvEdadPeso.text = "$textoEdad | $textoPeso"

            if (!mascota.foto.isNullOrBlank()) {
                Glide.with(itemView.context)
                    .load(mascota.foto)
                    .placeholder(R.drawable.ic_huella)
                    .into(ivFoto)
            } else {
                ivFoto.setImageResource(R.drawable.ic_huella)
            }

            itemView.setOnClickListener { onMascotaClick(mascota) }
        }

        private fun calcularEdadTexto(fechaISO: String?): String {
            if (fechaISO.isNullOrBlank()) return "Edad desconocida"
            return try {
                val formatoEntrada = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                formatoEntrada.timeZone = TimeZone.getTimeZone("UTC")
                val fechaNacimiento = formatoEntrada.parse(fechaISO) ?: return "Edad desconocida"

                val nacimiento = Calendar.getInstance().apply { time = fechaNacimiento }
                val hoy = Calendar.getInstance()

                var anios = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
                var meses = hoy.get(Calendar.MONTH) - nacimiento.get(Calendar.MONTH)
                var dias = hoy.get(Calendar.DAY_OF_MONTH) - nacimiento.get(Calendar.DAY_OF_MONTH)

                if (dias < 0) {
                    meses--
                    val mesAnterior = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
                    dias += mesAnterior.getActualMaximum(Calendar.DAY_OF_MONTH)
                }
                if (meses < 0) {
                    anios--
                    meses += 12
                }

                when {
                    anios > 0 -> "$anios año(s)"
                    meses > 0 -> "$meses mes(es)"
                    else -> "$dias día(s)"
                }
            } catch (e: Exception) {
                "Edad desconocida"
            }
        }
    }
}