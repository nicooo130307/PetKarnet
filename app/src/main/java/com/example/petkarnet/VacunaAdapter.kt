package com.example.petkarnet

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petkarnet.data.model.HistorialEntry

// 1. Creamos esta estructura rápida para juntar la vacuna "ideal" con el dato real de tu base de datos
data class SelloVacuna(
    val nombreIdeal: String,
    val registroReal: HistorialEntry? // Será nulo si el servidor nos dice que aún no tiene esta vacuna
)

// 2. El Adaptador
class VacunaAdapter(
    private val listaSellos: List<SelloVacuna>
) : RecyclerView.Adapter<VacunaAdapter.VacunaViewHolder>() {

    class VacunaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivSello: ImageView = itemView.findViewById(R.id.iv_sello_vacuna)
        val tvNombre: TextView = itemView.findViewById(R.id.tv_nombre_vacuna)
        val tvFecha: TextView = itemView.findViewById(R.id.tv_fecha_vacuna)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacunaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vacuna_album, parent, false)
        return VacunaViewHolder(view)
    }

    override fun onBindViewHolder(holder: VacunaViewHolder, position: Int) {
        val sello = listaSellos[position]

        // Asignamos el nombre de la vacuna (Ej. "Rabia", "Parvovirus")
        holder.tvNombre.text = sello.nombreIdeal

        // LÓGICA DEL ÁLBUM DE SELLOS
        if (sello.registroReal != null) {
            // SÍ LA TIENE: Sello a todo color y mostramos la fecha
            holder.tvFecha.text = sello.registroReal.fecha_aplicacion
            holder.tvFecha.setTextColor(Color.parseColor("#4CAF50")) // Verde para indicar que está lista
            holder.ivSello.alpha = 1.0f // Opacidad al 100% (Brillante)

            // Opcional: si más adelante quieres poner un icono de "Palomita" o cambiar la imagen, es aquí
            // holder.ivSello.setImageResource(R.drawable.ic_vacuna_lista)

        } else {
            // NO LA TIENE: Sello opaco y texto de "Pendiente"
            holder.tvFecha.text = "Pendiente"
            holder.tvFecha.setTextColor(Color.parseColor("#999999")) // Grisáceo
            holder.ivSello.alpha = 0.25f // Opacidad baja para dar efecto de estampa "vacía" o bloqueada
        }
    }

    override fun getItemCount(): Int = listaSellos.size
}