package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AgendaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_agenda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Enlazamos las vistas
        val calendarAgenda = view.findViewById<CalendarView>(R.id.calendar_agenda)
        val cardCita1 = view.findViewById<MaterialCardView>(R.id.card_cita_1)
        val cardCita2 = view.findViewById<MaterialCardView>(R.id.card_cita_2)
        val cardCita3 = view.findViewById<MaterialCardView>(R.id.card_cita_3)
        val fabNuevaCita = view.findViewById<FloatingActionButton>(R.id.fab_nueva_cita)

        // 2. Lógica del Calendario: Detectar cuando el doctor cambia de día
        calendarAgenda.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Nota: En Java/Kotlin, los meses van del 0 al 11 (Enero es 0, Diciembre es 11).
            // Por eso le sumamos 1 al mes para mostrarlo correctamente al usuario.
            val mesReal = month + 1
            Toast.makeText(requireContext(), "Cargando citas del $dayOfMonth/$mesReal/$year...", Toast.LENGTH_SHORT).show()

            // En el futuro, aquí consultarás tu base de datos MySQL para traer las citas de esa fecha exacta
        }

        // 3. Lógica de las Tarjetas de Citas
        cardCita1.setOnClickListener {
            Toast.makeText(requireContext(), "Viendo detalles de cita: Max", Toast.LENGTH_SHORT).show()
        }

        cardCita2.setOnClickListener {
            Toast.makeText(requireContext(), "Viendo detalles de cita: Luna", Toast.LENGTH_SHORT).show()
        }

        cardCita3.setOnClickListener {
            Toast.makeText(requireContext(), "Viendo detalles de cirugía: Rocky", Toast.LENGTH_SHORT).show()
        }

        // 4. Lógica del Botón "+" (Agendar)
        fabNuevaCita.setOnClickListener {
            Toast.makeText(requireContext(), "Abriendo formulario para agendar cita...", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), AgregarCitaVet::class.java)
            startActivity(intent)

        }
    }
}