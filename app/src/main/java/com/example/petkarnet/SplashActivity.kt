package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Temporizador de 2500 milisegundos (2.5 segundos)
        Handler(Looper.getMainLooper()).postDelayed({

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish()

        }, 2500)
    }
}