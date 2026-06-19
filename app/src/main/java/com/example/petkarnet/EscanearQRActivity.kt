package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class EscanearQRActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escanear_qractivity)

        val btnCancelar = findViewById<MaterialButton>(R.id.btn_cancelar_escaner)
        btnCancelar.setOnClickListener {
            finish()
        }

        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Escanea el código QR del paciente")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show()
                finish()
            } else {
                val contenidoQR = result.contents
                procesarQR(contenidoQR)
            }
        }
    }

    private fun procesarQR(contenido: String) {
        var idMascota: Int? = null


        if (contenido.startsWith("mascota:")) {
            idMascota = contenido.removePrefix("mascota:").toIntOrNull()
        }

        else if (contenido.toIntOrNull() != null) {
            idMascota = contenido.toInt()
        }

        else if (contenido.contains("/api/mascotas/")) {
            val patron = Regex("/api/mascotas/(\\d+)")
            val match = patron.find(contenido)
            if (match != null) {
                idMascota = match.groupValues[1].toIntOrNull()
            }
        }

        else if (contenido.contains("\"id_mascota\"")) {
            try {
                val json = org.json.JSONObject(contenido)
                idMascota = json.optInt("id_mascota", -1).takeIf { it != -1 }
            } catch (e: Exception) {

            }
        }

        if (idMascota != null) {
            val intent = Intent(this, CarnetFragment::class.java)
            intent.putExtra("id_mascota", idMascota)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "QR no válido para PetKarnet", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}