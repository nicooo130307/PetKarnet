package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petkarnet.util.CloudinaryManager
import com.cloudinary.android.callback.ErrorInfo
import com.onesignal.OneSignal


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CloudinaryManager.init(this)
        enableEdgeToEdge()
        // Forma correcta para la versión 5.x
        OneSignal.initWithContext(this, "031959f0-ae4e-49cc-a481-8a127a3dc93d")
        setContentView(R.layout.activity_main)
        val btn_registro = findViewById<Button>(R.id.btn_registrarse)

        val btn_login = findViewById<Button>(R.id.btn_login)

        btn_registro.setOnClickListener {

            val intent = Intent(this, Registro::class.java)
            startActivity(intent)


        }

        btn_login.setOnClickListener {
            val intent = Intent(this, Inicio_Sesion::class.java)
            startActivity(intent)
        }

    }
}