package com.example.petkarnet

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent
class Mis_mascotas : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_mascotas)
        enableEdgeToEdge()


        // 1. Enlazamos las vistas
        val cardMax = findViewById<MaterialCardView>(R.id.card_mascota_1)
        val cardLuna = findViewById<MaterialCardView>(R.id.card_mascota_2)
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fab_agregar_mascota)

        // 2. Lógica al tocar la tarjeta de Max
        cardMax.setOnClickListener {
            // En el futuro, aquí mandaremos el ID de Max a la pantalla principal del Carnet
            Toast.makeText(this, "Cargando perfil de Max...", Toast.LENGTH_SHORT).show()
        }

        // 3. Lógica al tocar la tarjeta de Luna
        cardLuna.setOnClickListener {
            Toast.makeText(this, "Cargando perfil de Luna...", Toast.LENGTH_SHORT).show()
        }

        // 4. Lógica para el botón de agregar nueva mascota
        fabAgregar.setOnClickListener {
            // Aquí abriremos el formulario para registrar un nuevo animalito
            Toast.makeText(this, "Abriendo registro de nueva mascota...", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, RegistroMascota::class.java)
            startActivity(intent)

        }
    }
}