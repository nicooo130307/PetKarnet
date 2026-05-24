package com.example.petkarnet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MenuVeterinario : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_veterinario)
        enableEdgeToEdge()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation_vet)

        // Al abrir la app, cargamos el fragmento de Inicio por defecto
        reemplazarFragmento(inicio_veterinario())

        // Escuchamos los clics del menú
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio_vet -> {
                    reemplazarFragmento(inicio_veterinario())
                    true
                }
                R.id.nav_pacientes -> {
                    // Aquí iría el fragmento de la lista de pacientes
                    reemplazarFragmento(PacientesFragment())
                    true
                }

                R.id.nav_consulta -> {
                    // Aquí iría el fragmento de la consulta
                    reemplazarFragmento(ConsultaFragment())
                    true
                }
                R.id.nav_agenda -> {
                    // Aquí iría el fragmento del calendario
                    reemplazarFragmento(AgendaFragment())
                    true
                }
                R.id.nav_perfil_vet -> {
                    // Aquí iría el fragmento del perfil del doctor
                    reemplazarFragmento(PerfilVetFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Función mágica para intercambiar fragmentos sin repetir código
    private fun reemplazarFragmento(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_vet, fragment)
            .commit()
    }
}