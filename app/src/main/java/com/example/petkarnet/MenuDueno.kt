package com.example.petkarnet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast

class MenuDueno : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_dueno)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_dueno)

        // 1. Cargamos el fragmento principal por defecto al abrir la pantalla
        // Así no nos recibe una pantalla en blanco
        if (savedInstanceState == null) {
            reemplazarFragmento(CarnetFragment())
        }

        // 2. Escuchamos los clics en los botones del menú de abajo
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_carnet -> {
                    reemplazarFragmento(CarnetFragment())
                    true
                }
                R.id.nav_recordatorios -> {
                    reemplazarFragmento(RecordatoriosFragment())
                    true
                }
                R.id.nav_directorio -> {
                    reemplazarFragmento(DirectorioFragment())
                    true
                }
                R.id.nav_perfil -> {
                    reemplazarFragmento(PerfilFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun reemplazarFragmento(fragment: Fragment) {
        val transaccion = supportFragmentManager.beginTransaction()
        // Cambiamos lo que haya en el contenedor blanco por el nuevo fragmento
        transaccion.replace(R.id.contenedor_fragments, fragment)
        // Guardamos los cambios
        transaccion.commit()
    }
}
