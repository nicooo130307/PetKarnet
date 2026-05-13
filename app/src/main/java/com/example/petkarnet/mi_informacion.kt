package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.activity.enableEdgeToEdge


class mi_informacion : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mi_informacion)

        val fabEditar = findViewById<FloatingActionButton>(R.id.fab_editar_usuario)

        fabEditar.setOnClickListener {
            // Abrimos la pantalla de edición (la que tiene los TextInputLayout)
            val intent = Intent(this, EditarInformacion::class.java)
            startActivity(intent)

        }
    }
}