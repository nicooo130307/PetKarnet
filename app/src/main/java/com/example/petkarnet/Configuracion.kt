package com.example.petkarnet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import com.example.petkarnet.util.LoadingManager

class Configuracion : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)
        enableEdgeToEdge()

        // 1. Enlazamos las vistas
        val switchModoOscuro = findViewById<SwitchMaterial>(R.id.switch_modo_oscuro)
        val switchRecordatorios = findViewById<SwitchMaterial>(R.id.switch_recordatorios)

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

        // 3. Lógica de Botones
        btnIdioma.setOnClickListener {
            Toast.makeText(this, "Próximamente: Configuración multilenguaje", Toast.LENGTH_SHORT).show()
        }

        btnPrivacidad.setOnClickListener {
            val intent = Intent(this, TerminosActivity::class.java)
            startActivity(intent)
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
            .setMessage("¿Estás seguro de que deseas eliminar tu cuenta de PetKarnet?\n\nSe desactivará tu cuenta y ya no podrás acceder a ella. Tus datos permanecerán almacenados por seguridad.")
            .setPositiveButton("Eliminar") { dialog, _ ->
                dialog.dismiss()
                eliminarCuenta()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun eliminarCuenta() {
        LoadingManager.showLoading(this, "Eliminando cuenta...")
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@Configuracion)
                val respuesta = api.eliminarCuenta()

                LoadingManager.hideLoading(this@Configuracion)

                if (respuesta.isSuccessful) {
                    Toast.makeText(this@Configuracion, "Cuenta eliminada exitosamente", Toast.LENGTH_LONG).show()

                    // Limpiar SharedPreferences (token y datos del usuario)
                    val prefs = getSharedPreferences("petkarnet_prefs", Context.MODE_PRIVATE)
                    prefs.edit().clear().apply()

                    // Redirigir al inicio (MainActivity) y cerrar todas las actividades anteriores
                    val intent = Intent(this@Configuracion, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = respuesta.errorBody()?.string()
                    Toast.makeText(this@Configuracion, "Error: $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                LoadingManager.hideLoading(this@Configuracion)
                Toast.makeText(this@Configuracion, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}