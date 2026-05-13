package com.example.petkarnet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.util.Patterns
import android.content.Intent

class Registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        val tilNombre = findViewById<TextInputLayout>(R.id.til_nombre)
        val etNombre = findViewById<TextInputEditText>(R.id.et_nombre)

        val tilCorreo = findViewById<TextInputLayout>(R.id.til_correo)
        val etCorreo = findViewById<TextInputEditText>(R.id.et_correo)

        val tilPassword = findViewById<TextInputLayout>(R.id.til_password)
        val etPassword = findViewById<TextInputEditText>(R.id.et_password)

        val tilConfirmar = findViewById<TextInputLayout>(R.id.til_confirmar_password)
        val etConfirmar = findViewById<TextInputEditText>(R.id.et_confirmar_password)

        val btnRegistrar = findViewById<Button>(R.id.btn_registrar_usuario)

        // 2. Escuchamos el clic del botón
        btnRegistrar.setOnClickListener {

            // Limpiamos los errores anteriores antes de volver a validar
            tilNombre.error = null
            tilCorreo.error = null
            tilPassword.error = null
            tilConfirmar.error = null
            // Extraemos lo que el usuario escribió (quitando espacios en blanco a los lados)
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmarPassword = etConfirmar.text.toString().trim()

            var formularioValido = true

            // 3. Validamos el Nombre
            if (nombre.isEmpty()) {
                tilNombre.error = "Por favor, ingresa tu nombre completo"
                formularioValido = false
            }

            // 4. Validamos el Correo (Que no esté vacío y tenga formato de email)
            if (correo.isEmpty()) {
                tilCorreo.error = "El correo es obligatorio"
                formularioValido = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                tilCorreo.error = "Ingresa un correo válido (ej. usuario@mail.com)"
                formularioValido = false
            }

            // 5. Validamos la Contraseña (Que no esté vacía y tenga mínimo 6 caracteres)
            if (password.isEmpty()) {
                tilPassword.error = "La contraseña es obligatoria"
                formularioValido = false
            } else if (password.length < 6) {
                tilPassword.error = "La contraseña debe tener al menos 6 caracteres"
                formularioValido = false
            }

            if (confirmarPassword.isEmpty()) {
                tilConfirmar.error = "Debes confirmar tu contraseña"
                formularioValido = false
            } else if (password != confirmarPassword) {
                tilConfirmar.error = "Las contraseñas no coinciden"
                tilPassword.error = "Las contraseñas no coinciden"
                formularioValido = false
            }
            // 6. Si todo está perfecto, avanzamos
            if (formularioValido) {

                println("¡Todo correcto! Listo para registrar a $nombre")
                val rgTipoUsuario = findViewById<RadioGroup>(R.id.rg_tipo_usuario)

                // Verificamos cuál de los dos botones está seleccionado
                if (rgTipoUsuario.checkedRadioButtonId == R.id.rb_dueno) {

                    // Viaje para el DUEÑO
                    val intentDueno = Intent(this@Registro, RegistroMascota::class.java)
                    startActivity(intentDueno)
                    finish() // Cerramos la pantalla de registro para que no pueda volver atrás con el botón del celular

                } else if (rgTipoUsuario.checkedRadioButtonId == R.id.rb_veterinario) {

                    // Viaje para el VETERINARIO
                    val intentVeterinario = Intent(this@Registro, PerfilVeterinario::class.java)
                    startActivity(intentVeterinario)
                    finish()
                }

            }
        }
    }
}





