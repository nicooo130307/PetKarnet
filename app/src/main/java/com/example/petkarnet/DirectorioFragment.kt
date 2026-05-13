package com.example.petkarnet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.card.MaterialCardView

// 1. Agregamos OnMapReadyCallback a la firma de la clase
class DirectorioFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapa: GoogleMap
    private lateinit var tarjetaClinica: MaterialCardView
    private lateinit var textoNombreClinica: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_directorio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enlazamos las vistas
        tarjetaClinica = view.findViewById(R.id.card_detalle_clinica)
        textoNombreClinica = view.findViewById(R.id.tv_nombre_clinica)

        // 2. Buscamos el mapa y lo inicializamos asíncronamente
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapa_directorio) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // 3. Esta función se ejecuta mágicamente cuando Google termina de cargar el mapa
    override fun onMapReady(googleMap: GoogleMap) {
        mapa = googleMap

        // Coordenadas centrales de CDMX
        val cdmx = LatLng(19.4326, -99.1332)

        // Coordenadas de un par de clínicas ficticias cercanas
        val clinica1 = LatLng(19.4400, -99.1400)
        val clinica2 = LatLng(19.4200, -99.1200)

        // Agregamos los marcadores al mapa
        mapa.addMarker(MarkerOptions().position(clinica1).title("Veterinaria PetHealth CECyT"))
        mapa.addMarker(MarkerOptions().position(clinica2).title("Hospital Animal de Especialidades"))

        // Movemos la cámara del usuario para que arranque en CDMX con buen zoom (13f)
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(cdmx, 13f))

        // --- LÓGICA DE INTERACCIÓN ---

        // ¿Qué pasa al tocar un globito?
        mapa.setOnMarkerClickListener { marker ->
            // Actualizamos el nombre en la tarjeta con el del marcador
            textoNombreClinica.text = marker.title
            // Hacemos visible la tarjeta deslizando desde abajo
            tarjetaClinica.visibility = View.VISIBLE
            true // Evitamos que la cámara haga un zoom automático no deseado
        }

        // ¿Qué pasa al tocar cualquier otra parte del mapa vacía?
        mapa.setOnMapClickListener {
            // Ocultamos la tarjeta
            tarjetaClinica.visibility = View.GONE
        }
    }
}