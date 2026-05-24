package com.example.petkarnet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.widget.*

class ConsultaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_consulta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        val btn_aceptar = view.findViewById<Button>(R.id.btn_aceptar)

        btn_aceptar.setOnClickListener {

            val rgTipoUsuario = view.findViewById<RadioGroup>(R.id.rb_motivo)

            // Verificamos cuál de los dos botones está seleccionado
            if (rgTipoUsuario.checkedRadioButtonId == R.id.rb_motivo_vacuna) {

                // Viaje para el DUEÑO
                val intentDueno = Intent(requireContext(), Vacuna::class.java)
                startActivity(intentDueno)
                 // Cerramos la pantalla de registro para que no pueda volver atrás con el botón del celular

            } else if (rgTipoUsuario.checkedRadioButtonId == R.id.rb_motivo_estetica) {

                // Viaje para el VETERINARIO
                val intentVeterinario = Intent(requireContext(), Estetica::class.java)
                startActivity(intentVeterinario)

            } else if (rgTipoUsuario.checkedRadioButtonId == R.id.rb_motivo_consulta) {

                val intentVeterinario = Intent(requireContext(), Consulta::class.java)
                startActivity(intentVeterinario)



            }




        }
    }
}