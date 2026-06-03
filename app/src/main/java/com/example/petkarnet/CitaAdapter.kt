package com.example.petkarnet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petkarnet.data.model.Cita

class CitaAdapter(private val citas: List<Cita>) : RecyclerView.Adapter<CitaAdapter.CitaViewHolder>() {

    class CitaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Usamos los IDs exactos de tu item_cita.xml
        val tvTipo: TextView = view.findViewById(R.id.tv_tipo_cita)
        val tvFecha: TextView = view.findViewById(R.id.tv_fecha_cita)
        val tvMascota: TextView = view.findViewById(R.id.tv_mascota_cita)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cita, parent, false)
        return CitaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = citas[position]

        // 1. Tipo de cita (Ponemos la primera letra en mayúscula)
        holder.tvTipo.text = cita.tipo_cita.replaceFirstChar { it.uppercase() }

        // 2. Fecha y Hora (Le agregamos los emojis para que se vea como tu diseño)
        // Nota: cita.fecha_hora viene del servidor como "yyyy-MM-dd HH:mm:ss"
        holder.tvFecha.text = "📅 ${cita.fecha_hora}"

        // 3. Nombre de la mascota
        // Asegúrate de que el objeto Cita traiga el nombre (o usa el ID si no lo tienes)
        holder.tvMascota.text = cita.mascota_nombre ?: "Mascota ID: ${cita.id_mascota}"
    }

    override fun getItemCount() = citas.size
}