package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.button.MaterialButton
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class EscanearQRActivity : AppCompatActivity() {

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                iniciarEscaneo()
            } else {
                Toast.makeText(this, "Se necesita permiso de cámara para escanear QR", Toast.LENGTH_LONG).show()
                finish()
            }
        }

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show()
            finish()
        } else {
            procesarQR(result.contents)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escanear_qractivity)

        val btnCancelar = findViewById<MaterialButton>(R.id.btn_cancelar_escaner)
        btnCancelar.setOnClickListener {
            finish()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            iniciarEscaneo()
        }
    }

    private fun iniciarEscaneo() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("Escanea el código QR del paciente")
            setBeepEnabled(false)
            setOrientationLocked(false)
            setTimeout(0)
        }
        scanLauncher.launch(options)
    }

    private fun procesarQR(contenido: String) {
        var idMascota: Int? = null

        if (contenido.startsWith("mascota:")) {
            idMascota = contenido.removePrefix("mascota:").toIntOrNull()
        } else if (contenido.toIntOrNull() != null) {
            idMascota = contenido.toInt()
        } else if (contenido.contains("/api/mascotas/")) {
            val patron = Regex("/api/mascotas/(\\d+)")
            val match = patron.find(contenido)
            if (match != null) {
                idMascota = match.groupValues[1].toIntOrNull()
            }
        } else if (contenido.contains("\"id_mascota\"")) {
            try {
                val json = org.json.JSONObject(contenido)
                idMascota = json.optInt("id_mascota", -1).takeIf { it != -1 }
            } catch (e: Exception) { }
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