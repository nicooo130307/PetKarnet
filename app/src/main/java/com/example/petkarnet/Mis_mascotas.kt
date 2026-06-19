package com.example.petkarnet

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View

import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petkarnet.data.network.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import com.example.petkarnet.util.LoadingManager


class Mis_mascotas : AppCompatActivity() {

    private lateinit var rvMascotas: RecyclerView
    private lateinit var tvSinMascotas: TextView
    private lateinit var adapter: MascotaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mis_mascotas)

        rvMascotas = findViewById(R.id.rv_mis_mascotas)
        tvSinMascotas = findViewById(R.id.tv_sin_mascotas)
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fab_agregar_mascota)

        rvMascotas.layoutManager = LinearLayoutManager(this)

        fabAgregar.setOnClickListener {
            val intent = Intent(this, RegistroMascota::class.java)
            startActivity(intent)
        }

        configurarSwipeParaEliminar()
    }

    override fun onResume() {
        super.onResume()
        cargarMascotasReales()
    }

    private fun cargarMascotasReales() {
        LoadingManager.showLoading(this, "Buscando tus mascotas...")
        tvSinMascotas.visibility = View.GONE
        rvMascotas.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@Mis_mascotas)
                val respuesta = api.listarMascotas()

                LoadingManager.hideLoading(this@Mis_mascotas)

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val listaMutable = respuesta.body()!!.toMutableList()

                    // 1. VALIDACIÓN DEL ESTADO VACÍO
                    if (listaMutable.isEmpty()) {
                        tvSinMascotas.visibility = View.VISIBLE
                        rvMascotas.visibility = View.GONE
                    } else {
                        tvSinMascotas.visibility = View.GONE
                        rvMascotas.visibility = View.VISIBLE

                        adapter = MascotaAdapter(listaMutable) { mascotaSeleccionada ->
                            // 1. Abrimos la "caja fuerte" de SharedPreferences
                            val sharedPref = getSharedPreferences("PetKarnetPrefs", Context.MODE_PRIVATE)

                            // 2. Guardamos los datos de la mascota seleccionada
                            with(sharedPref.edit()) {
                                putInt("ID_MASCOTA_ACTIVA", mascotaSeleccionada.id)
                                putString("ESPECIE_MASCOTA_ACTIVA", mascotaSeleccionada.especie)
                                apply() // Guarda los cambios de forma invisible en segundo plano
                            }

                            Toast.makeText(this@Mis_mascotas, "Cargando perfil de ${mascotaSeleccionada.nombre}", Toast.LENGTH_SHORT).show()

                            // 3. Redirigimos al menú principal donde están los fragmentos
                            val intent = Intent(this@Mis_mascotas, MenuDueno::class.java)
                            startActivity(intent)
                            finish() // Cerramos esta pantalla para que no se quede acumulada en el historial
                        }
                        rvMascotas.adapter = adapter
                    }
                } else {
                    Toast.makeText(this@Mis_mascotas, "Error al cargar mascotas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                LoadingManager.hideLoading(this@Mis_mascotas)
                Toast.makeText(this@Mis_mascotas, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarSwipeParaEliminar() {
        // 2. RESTRINGIR EL DESLIZAMIENTO: Ahora solo acepta "LEFT" (De derecha a izquierda)
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val pincel = Paint()

                    if (dX < 0) {
                        // Fondo rojo
                        pincel.color = Color.parseColor("#EF4444")
                        c.drawRect(
                            itemView.right.toFloat() + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat(),
                            pincel
                        )

                        // 3. LA TIPOGRAFÍA PERSONALIZADA
                        pincel.color = Color.WHITE
                        pincel.textSize = 42f
                        pincel.isAntiAlias = true
                        pincel.textAlign = Paint.Align.RIGHT

                        // Cargar tu fuente (¡Asegúrate de cambiar R.font.nombre_de_tu_fuente por la correcta!)
                        val typeface = ResourcesCompat.getFont(this@Mis_mascotas, R.font.montserrat)
                        if (typeface != null) {
                            pincel.typeface = typeface
                        }

                        val centroY = (itemView.top + itemView.bottom) / 2f + (pincel.textSize / 3f)
                        val margenDerecho = itemView.right.toFloat() - 60f

                        c.drawText("Eliminar", margenDerecho, centroY, pincel)
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val mascota = adapter.getMascotaAt(position)

                AlertDialog.Builder(this@Mis_mascotas, R.style.TemaCalendarioPet)
                    .setTitle("¿Eliminar a ${mascota.nombre}?")
                    .setMessage("Se borrará de forma permanente todo su historial médico, vacunas y citas de la plataforma. Esta acción no se puede deshacer.")
                    .setCancelable(false)
                    .setPositiveButton("Sí, eliminar") { _, _ ->
                        eliminarMascotaDelServidor(mascota.id, position)
                    }
                    .setNegativeButton("Cancelar") { dialogo, _ ->
                        dialogo.dismiss()
                        adapter.notifyItemChanged(position)
                    }
                    .show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(rvMascotas)
    }

    private fun eliminarMascotaDelServidor(idMascota: Int, position: Int) {
        LoadingManager.showLoading(this, "Eliminando mascota...")

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@Mis_mascotas)
                val respuesta = api.eliminarMascota(idMascota)

                LoadingManager.hideLoading(this@Mis_mascotas)

                if (respuesta.isSuccessful) {
                    Toast.makeText(this@Mis_mascotas, "Mascota eliminada correctamente", Toast.LENGTH_SHORT).show()
                    adapter.eliminarMascotaDeLista(position)

                    // REVISIÓN DEL ESTADO VACÍO DESPUÉS DE ELIMINAR
                    if (adapter.itemCount == 0) {
                        tvSinMascotas.visibility = View.VISIBLE
                        rvMascotas.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(this@Mis_mascotas, "No se pudo eliminar en el servidor", Toast.LENGTH_SHORT).show()
                    adapter.notifyItemChanged(position)
                }
            } catch (e: Exception) {
                LoadingManager.hideLoading(this@Mis_mascotas)
                Toast.makeText(this@Mis_mascotas, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                adapter.notifyItemChanged(position)
            }
        }
    }
}