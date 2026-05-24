package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.*

class Tipo_consulta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tipo_consulta)


        val btn_aceptar = findViewById<Button>(R.id.btn_aceptar)

        btn_aceptar.setOnClickListener {

        val rgTipoUsuario = findViewById<RadioGroup>(R.id.rb_motivo)

        // Verificamos cuál de los dos botones está seleccionado
        if (rgTipoUsuario.checkedRadioButtonId == R.id.rb_motivo_vacuna) {

            // Viaje para el DUEÑO
            val intentDueno = Intent(this@Tipo_consulta, Vacuna::class.java)
            startActivity(intentDueno)
            finish() // Cerramos la pantalla de registro para que no pueda volver atrás con el botón del celular

        } else if (rgTipoUsuario.checkedRadioButtonId == R.id.rb_motivo_estetica) {

            // Viaje para el VETERINARIO
            val intentVeterinario = Intent(this@Tipo_consulta, Estetica::class.java)
            startActivity(intentVeterinario)
            finish()
        }
        else if (rgTipoUsuario.checkedRadioButtonId == R.id.rb_motivo_consulta) {

            val intentVeterinario = Intent(this@Tipo_consulta, Consulta::class.java)
            startActivity(intentVeterinario)
            finish()


        }




        }
    }
}