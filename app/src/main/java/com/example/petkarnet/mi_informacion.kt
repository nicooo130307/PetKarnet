package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch


class mi_informacion : AppCompatActivity() {


    private lateinit var tvNombre: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var tvDireccion: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvPassword: TextView
    private lateinit var progressBar: ProgressBar





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mi_informacion)

        tvNombre = findViewById(R.id.tv_nombre_usuario)
        tvTelefono = findViewById(R.id.tv_telefono_usuario)
        tvDireccion = findViewById(R.id.tv_direccion_usuario)
        tvCorreo = findViewById(R.id.tv_correo_usuario)
        tvPassword = findViewById(R.id.tv_password_usuario)
        progressBar = findViewById(R.id.progress_bar)




        val fabEditar = findViewById<FloatingActionButton>(R.id.fab_editar_usuario)

        fabEditar.setOnClickListener {
            // Abrimos la pantalla de edición (la que tiene los TextInputLayout)
            val intent = Intent(this, EditarInformacion::class.java)
            startActivity(intent)
        }

        cargarPerfil()

    }

    private fun cargarPerfil() {
        // Mostrar carga
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@mi_informacion)
                val respuesta = api.perfil()  // Llama a GET /api/usuarios/perfil

                progressBar.visibility = View.GONE

                if (respuesta.isSuccessful) {
                    val usuario = respuesta.body()
                    usuario?.let {
                        // Actualizar los campos con los datos reales
                        tvNombre.text = it.nombre
                        tvTelefono.text = it.telefono ?: "No registrado"
                        tvDireccion.text = it.direccion ?: "No registrada"
                        tvCorreo.text = it.email
                        // La contraseña nunca se muestra, solo asteriscos
                        tvPassword.text = "••••••••••••"
                    }
                } else {
                    Toast.makeText(this@mi_informacion, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@mi_informacion, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}


