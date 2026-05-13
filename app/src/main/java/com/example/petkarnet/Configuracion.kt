package com.example.petkarnet

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class Configuracion : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)

        // 1. Enlazamos las vistas
        val switchModoOscuro = findViewById<SwitchMaterial>(R.id.switch_modo_oscuro)
        val switchRecordatorios = findViewById<SwitchMaterial>(R.id.switch_recordatorios)
        val switchPromociones = findViewById<SwitchMaterial>(R.id.switch_promociones)

        val btnIdioma = findViewById<LinearLayout>(R.id.btn_idioma)
        val btnPrivacidad = findViewById<TextView>(R.id.btn_privacidad)
        val btnEliminarCuenta = findViewById<TextView>(R.id.btn_eliminar_cuenta)

        // 2. Lógica de los Switches
        switchModoOscuro.setOnCheckedChangeListener { _, isChecked ->
            val estado = if (isChecked) "activado" else "desactivado"
            Toast.makeText(this, "Modo oscuro $estado", Toast.LENGTH_SHORT).show()
        }

        switchRecordatorios.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                Toast.makeText(this, "¡Cuidado! Podrías olvidar vacunas importantes.", Toast.LENGTH_LONG).show()
            }
        }

        switchPromociones.setOnCheckedChangeListener { _, isChecked ->
            val estado = if (isChecked) "activadas" else "desactivadas"
            Toast.makeText(this, "Alertas de tienda $estado", Toast.LENGTH_SHORT).show()
        }

        // 3. Lógica de Botones
        btnIdioma.setOnClickListener {
            Toast.makeText(this, "Próximamente: Configuración multilenguaje", Toast.LENGTH_SHORT).show()
        }

        btnPrivacidad.setOnClickListener {
            // Aquí conectarás con la justificación de tu tesis (LFPDPPP)
            Toast.makeText(this, "Abriendo Aviso de Privacidad...", Toast.LENGTH_SHORT).show()
        }

        btnEliminarCuenta.setOnClickListener {
            mostrarAlertaEliminarCuenta()
        }
    }

    // Función para crear la advertencia de eliminación de cuenta
    private fun mostrarAlertaEliminarCuenta() {
        AlertDialog.Builder(this)
            .setTitle("⚠️ Eliminar Cuenta")
            .setMessage("¿Estás seguro de que deseas eliminar tu cuenta de PetKarnet?\n\nSe borrará todo el historial de vacunas y citas de tus mascotas. Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                Toast.makeText(this, "Cuenta eliminada. Cerrando sesión...", Toast.LENGTH_SHORT).show()
                // En el futuro, aquí borras de MySQL y mandas al Login
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}