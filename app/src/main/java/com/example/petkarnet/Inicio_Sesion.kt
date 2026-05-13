package com.example.petkarnet

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class Inicio_Sesion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio_sesion)

        val tilCorreo = findViewById<TextInputLayout>(R.id.til_login_correo)
        val etCorreo = findViewById<TextInputEditText>(R.id.et_login_correo)

        val tilPassword = findViewById<TextInputLayout>(R.id.til_login_password)
        val etPassword = findViewById<TextInputEditText>(R.id.et_login_password)

        val btnIngresar = findViewById<Button>(R.id.btn_ingresar)

        // 2. Escuchamos el clic
        btnIngresar.setOnClickListener {

            // Limpiamos errores previos
            tilCorreo.error = null
            tilPassword.error = null

            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString().trim()

            var esValido = true

            // 3. Validar Correo
            if (correo.isEmpty()) {
                tilCorreo.error = "Ingresa tu correo"
                esValido = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                tilCorreo.error = "Formato de correo inválido"
                esValido = false
            }

            // 4. Validar Contraseña
            if (password.isEmpty()) {
                tilPassword.error = "Ingresa tu contraseña"
                esValido = false
            }

            // 5. Si todo está bien, simulamos el inicio
            if (esValido) {
                // Aquí irá la conexión a tu base de datos para verificar las credenciales
                println("Todo listo para iniciar sesión con $correo")
            }
        }
    }
}

