package com.example.petkarnet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.petkarnet.util.CloudinaryManager
import com.onesignal.OneSignal

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializaciones globales de tus servicios
        CloudinaryManager.init(this)
        enableEdgeToEdge()
        OneSignal.initWithContext(this, "031959f0-ae4e-49cc-a481-8a127a3dc93d")

        // 2. LA ADUANA: Revisar la "memoria" antes de mostrar nada
        val prefs = getSharedPreferences("petkarnet_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        val mantenerSesion = prefs.getBoolean("mantener_sesion", false)
        val rol = prefs.getString("usuario_rol", null)

        // Verificamos si tiene pase VIP
        if (!token.isNullOrEmpty() && mantenerSesion && !rol.isNullOrEmpty()) {
            navegarSegunRol(rol)
            return // IMPORTANTE: El return detiene la ejecución aquí para que no cargue el XML de bienvenida
        } else {
            // Si el usuario dijo que NO a mantener sesión, limpiamos la memoria por seguridad
            if (!mantenerSesion) {
                prefs.edit().clear().apply()
            }
        }

        // 3. Si no hay sesión activa, ahora sí mostramos los botones normales
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

    // Función auxiliar para saber a qué menú enviarlo dependiendo de su rol
    private fun navegarSegunRol(rol: String) {
        val intent = when (rol) {
            "dueño" -> Intent(this, MenuDueno::class.java)
            "veterinario" -> Intent(this, MenuVeterinario::class.java)
            "admin" -> Intent(this, MenuAdmin::class.java)
            else -> Intent(this, Inicio_Sesion::class.java) // Respaldo de seguridad
        }
        startActivity(intent)
        finish() // Destruimos el MainActivity para que no pueda volver aquí con el botón "Atrás"
    }
}