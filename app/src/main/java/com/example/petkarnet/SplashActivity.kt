package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // 1. Enlazamos la imagen de tu logo
        val logoSplash = findViewById<ImageView>(R.id.iv_logo_splash)

        // 2. Cargamos la animación que acabamos de crear
        val animacionFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // 3. ¡Hacemos que el logo inicie la animación!
        logoSplash.startAnimation(animacionFadeIn)

        // 4. El temporizador de 2.5 segundos (igual que antes)
        Handler(Looper.getMainLooper()).postDelayed({

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }, 2500)
    }
}