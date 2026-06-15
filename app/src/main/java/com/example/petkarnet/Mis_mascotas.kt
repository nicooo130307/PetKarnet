package com.example.petkarnet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class Mis_mascotas : AppCompatActivity() {

    private lateinit var rvMascotas: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: MascotaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mis_mascotas)

        rvMascotas = findViewById(R.id.rv_mis_mascotas)
        progressBar = findViewById(R.id.progress_bar_mascotas)
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fab_agregar_mascota)

        rvMascotas.layoutManager = LinearLayoutManager(this)

        fabAgregar.setOnClickListener {
            val intent = Intent(this, RegistroMascota::class.java)
            startActivity(intent)
        }

        // Activamos el escuchador del deslizamiento
        configurarSwipeParaEliminar()
    }

    override fun onResume() {
        super.onResume()
        // Recargar la lista siempre que la pantalla vuelva a estar visible
        cargarMascotasReales()
    }

    private fun cargarMascotasReales() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@Mis_mascotas)
                val respuesta = api.listarMascotas()

                progressBar.visibility = View.GONE

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val listaMutable = respuesta.body()!!.toMutableList()

                    adapter = MascotaAdapter(listaMutable) { mascotaSeleccionada ->
                        // Al tocar la tarjeta, abrimos el menú principal o carnet enviando el ID
                        Toast.makeText(this@Mis_mascotas, "Abriendo perfil de ${mascotaSeleccionada.nombre}", Toast.LENGTH_SHORT).show()
                        // Aquí puedes añadir tu Intent para ir al MenuDueno si lo necesitas
                    }
                    rvMascotas.adapter = adapter
                } else {
                    Toast.makeText(this@Mis_mascotas, "Error al cargar mascotas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@Mis_mascotas, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarSwipeParaEliminar() {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false // No nos interesa mover tarjetas arriba/abajo

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val mascota = adapter.getMascotaAt(position)

                // Crear el diálogo de advertencia estética
                AlertDialog.Builder(this@Mis_mascotas, R.style.TemaCalendarioPet) // Reutilizamos tu estilo para mantener los colores de la app
                    .setTitle("¿Eliminar a ${mascota.nombre}?")
                    .setMessage("Se borrará de forma permanente todo su historial médico, vacunas y citas de la plataforma. Esta acción no se puede deshacer.")
                    .setCancelable(false) // Obligamos a elegir una opción
                    .setPositiveButton("Sí, eliminar") { _, _ ->
                    eliminarMascotaDelServidor(mascota.id, position)
                }
                    .setNegativeButton("Cancelar") { dialogo, _ ->
                        dialogo.dismiss()
                        // ¡TRUCO DE UX! Si cancela, rebotamos la tarjeta a su sitio original
                        adapter.notifyItemChanged(position)
                    }
                    .show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(rvMascotas)
    }

    private fun eliminarMascotaDelServidor(idMascota: Int, position: Int) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@Mis_mascotas)
                // Llamamos a tu endpoint DELETE de Retrofit
                val respuesta = api.eliminarMascota(idMascota)

                progressBar.visibility = View.GONE

                if (respuesta.isSuccessful) {
                    Toast.makeText(this@Mis_mascotas, "Mascota eliminada correctamente", Toast.LENGTH_SHORT).show()
                    adapter.eliminarMascotaDeLista(position)
                } else {
                    Toast.makeText(this@Mis_mascotas, "No se pudo eliminar en el servidor", Toast.LENGTH_SHORT).show()
                    adapter.notifyItemChanged(position) // Rebotar si falla
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@Mis_mascotas, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                adapter.notifyItemChanged(position) // Rebotar si falla de red
            }
        }
    }
}