package com.example.petkarnet

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.petkarnet.data.model.ActualizarPerfilRequest
import com.example.petkarnet.data.model.MascotaRequest
import com.example.petkarnet.data.network.RetrofitClient
import com.example.petkarnet.util.CloudinaryManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import com.example.petkarnet.util.LoadingManager

class EditarCarnet : AppCompatActivity() {

    private lateinit var ivFoto: ShapeableImageView
    private lateinit var fabCambiarFoto: FloatingActionButton
    private lateinit var etNombre: TextInputEditText

    // Componentes nuevos/actualizados
    private lateinit var rgEspecie: RadioGroup
    private lateinit var rbPerro: RadioButton
    private lateinit var rbGato: RadioButton
    private lateinit var etRaza: AutoCompleteTextView
    private lateinit var etSexo: AutoCompleteTextView
    private lateinit var etEdad: TextInputEditText

    private lateinit var etPeso: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var etDireccion: TextInputEditText
    private lateinit var btnGuardar: MaterialButton


    private var mascotaId: Int = 0
    private var uriFotoSeleccionada: Uri? = null
    private var urlFotoActual: String? = null
    private var fotoCambiada: Boolean = false

    // Listas de razas
    private val razasPerro = listOf(
        RazaMascota("Mestizo (Sin raza específica)", R.drawable.mestizo),
        RazaMascota("Pug", R.drawable.pug),
        RazaMascota("Chihuahua", R.drawable.chihuahua),
        RazaMascota("Golden Retriever", R.drawable.golden),
        RazaMascota("Labrador", R.drawable.labrador),
        RazaMascota("Pastor Aleman", R.drawable.pastor_aleman),
        RazaMascota("Husky Siberiano", R.drawable.husky),
        RazaMascota("BullDog Francés", R.drawable.bulldog),
        RazaMascota("Poodle", R.drawable.poodle),
        RazaMascota("Schnauzer", R.drawable.schnauzer),
        RazaMascota("Pitbull", R.drawable.pitbull),
        RazaMascota("Yorkshire Terrier", R.drawable.terrier),
        RazaMascota("Otro...", R.drawable.otro)
    )

    private val razasGato = listOf(
        RazaMascota("Mestizo (Sin raza específica)", R.drawable.mestizon),
        RazaMascota("Siamés", R.drawable.siames),
        RazaMascota("Persa", R.drawable.persa),
        RazaMascota("Maine Coon", R.drawable.maine_coon),
        RazaMascota("Bengala", R.drawable.bengala),
        RazaMascota("Sphynx (Esfinge)", R.drawable.esfinge),
        RazaMascota("Azul Ruso", R.drawable.ruso),
        RazaMascota("Ragdoll", R.drawable.ragdoll),
        RazaMascota("Angora", R.drawable.angora),
        RazaMascota("British Shorthair", R.drawable.british_shorthair),
        RazaMascota("Gato Europeo", R.drawable.gato_europeo),
        RazaMascota("Munchkin", R.drawable.munchkin),
        RazaMascota("Otro...", R.drawable.otron)
    )

    private val abrirGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            uriFotoSeleccionada = uri
            ivFoto.setImageURI(uri)
            fotoCambiada = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_carnet)

        ivFoto = findViewById(R.id.iv_foto_mascota_editar)
        fabCambiarFoto = findViewById(R.id.fab_cambiar_foto)
        etNombre = findViewById(R.id.et_nombre_mascota_editar)
        rgEspecie = findViewById(R.id.rg_especie_editar)
        rbPerro = findViewById(R.id.rb_perro_editar)
        rbGato = findViewById(R.id.rb_gato_editar)
        etRaza = findViewById(R.id.et_raza_mascota_editar)
        etSexo = findViewById(R.id.et_sexo_mascota_editar)
        etPeso = findViewById(R.id.et_peso_mascota_editar)
        etEdad = findViewById(R.id.et_edad_mascota_editar)
        etTelefono = findViewById(R.id.et_telefono_editar)
        etDireccion = findViewById(R.id.et_direccion_editar)
        btnGuardar = findViewById(R.id.btn_guardar_cambios_carnet)


        // Configurar Sexo Dropdown
        val opcionesSexo = arrayOf("Macho", "Hembra")
        val adapterSexo = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opcionesSexo)
        etSexo.setAdapter(adapterSexo)

        // Lógica de los RadioButtons para actualizar la lista de razas
        rgEspecie.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_perro_editar) {
                actualizarMenuRazas(razasPerro)
            } else if (checkedId == R.id.rb_gato_editar) {
                actualizarMenuRazas(razasGato)
            }
        }

        // Configurar Calendario para la edad
        etEdad.setOnClickListener {
            val calendario = Calendar.getInstance()
            DatePickerDialog(this, R.style.TemaCalendarioPet, { _, year, month, day ->
                val mesFormateado = String.format("%02d", month + 1)
                val diaFormateado = String.format("%02d", day)
                etEdad.setText("$year-$mesFormateado-$diaFormateado")
            }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show()
        }

        fabCambiarFoto.setOnClickListener { abrirGaleria.launch("image/*") }
        btnGuardar.setOnClickListener { guardarCambios() }

        cargarDatosMascota()
    }

    private fun actualizarMenuRazas(razas: List<RazaMascota>) {
        val adapter = RazaAdapter(this, razas)
        etRaza.setAdapter(adapter)
        etRaza.setText(razas[0].nombre, false)
    }

    private fun formatearFechaParaEditar(fechaISO: String?): String {
        if (fechaISO.isNullOrBlank()) return ""
        return try {
            val formatoEntrada = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            formatoEntrada.timeZone = TimeZone.getTimeZone("UTC")
            val date = formatoEntrada.parse(fechaISO)

            val formatoSalida = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formatoSalida.format(date!!)
        } catch (e: Exception) {
            fechaISO
        }
    }

    private fun cargarDatosMascota() {
        LoadingManager.showLoading(this, "Cargando datos...")

        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@EditarCarnet)
                val respuestaMascotas = api.listarMascotas()

                if (respuestaMascotas.isSuccessful && !respuestaMascotas.body().isNullOrEmpty()) {
                    val mascota = respuestaMascotas.body()!!.first()
                    mascotaId = mascota.id
                    urlFotoActual = mascota.foto

                    etNombre.setText(mascota.nombre)
                    etPeso.setText(mascota.peso?.toString() ?: "")

                    // Formatear la fecha para que el usuario la vea limpia (YYYY-MM-DD)
                    etEdad.setText(formatearFechaParaEditar(mascota.fecha_nacimiento))

                    // Seleccionar el RadioButton correcto y cargar la raza
                    if (mascota.especie.equals("gato", ignoreCase = true)) {
                        rbGato.isChecked = true
                        actualizarMenuRazas(razasGato)
                    } else {
                        rbPerro.isChecked = true
                        actualizarMenuRazas(razasPerro)
                    }

                    // Establecer texto del dropdown sin abrirlo
                    etRaza.setText(mascota.raza, false)
                    etSexo.setText(mascota.sexo, false)

                    if (!mascota.foto.isNullOrBlank()) {
                        Glide.with(this@EditarCarnet).load(mascota.foto).placeholder(R.drawable.ic_huella).into(ivFoto)
                    }
                }

                val respuestaPerfil = api.perfil()
                if (respuestaPerfil.isSuccessful) {
                    val usuario = respuestaPerfil.body()
                    etTelefono.setText(usuario?.telefono ?: "")
                    etDireccion.setText(usuario?.direccion ?: "")
                }
                LoadingManager.hideLoading(this@EditarCarnet)
            } catch (e: Exception) {
                LoadingManager.hideLoading(this@EditarCarnet)
                Toast.makeText(this@EditarCarnet, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarCambios() {
        val nombre = etNombre.text.toString().trim()
        val raza = etRaza.text.toString().trim()
        val sexo = etSexo.text.toString().trim()
        val peso = etPeso.text.toString().trim()
        val edad = etEdad.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()

        val especie = if (rbGato.isChecked) "Gato" else "Perro"

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre de la mascota es obligatorio", Toast.LENGTH_SHORT).show()
            return
        }

        LoadingManager.showLoading(this, "Guardando cambios...")
        btnGuardar.isEnabled = false

        if (fotoCambiada && uriFotoSeleccionada != null) {
            subirFotoYGuardar(uriFotoSeleccionada!!, nombre, especie, raza, sexo, peso, edad, telefono, direccion)
        } else {
            guardarTodo(nombre, especie, raza, sexo, peso, edad, urlFotoActual, telefono, direccion)
        }
    }

    private fun subirFotoYGuardar(uri: Uri, nombre: String, especie: String, raza: String, sexo: String, peso: String, edad: String, telefono: String, direccion: String) {
        val imageFile = CloudinaryManager.getFileFromUri(this, uri)
        if (imageFile == null) {
            LoadingManager.hideLoading(this)
            btnGuardar.isEnabled = true
            Toast.makeText(this, "No se pudo acceder a la imagen", Toast.LENGTH_SHORT).show()
            return
        }

        CloudinaryManager.uploadImage(imageFile) { url ->
            if (url != null) {
                guardarTodo(nombre, especie, raza, sexo, peso, edad, url, telefono, direccion)
            } else {
                LoadingManager.hideLoading(this)
                btnGuardar.isEnabled = true
                Toast.makeText(this, "Error al subir la foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarTodo(nombre: String, especie: String, raza: String, sexo: String, peso: String, edad: String, urlFoto: String?, telefono: String, direccion: String) {
        lifecycleScope.launch {
            try {
                val api = RetrofitClient.create(this@EditarCarnet)

                val requestMascota = MascotaRequest(
                    nombre = nombre, especie = especie, raza = raza.ifBlank { null },
                    fecha_nacimiento = edad.ifBlank { null }, foto = urlFoto,
                    sexo = sexo.ifBlank { null },
                    peso = peso.ifBlank { null }
                )

                val respuestaMascota = api.actualizarMascota(mascotaId, requestMascota)
                if (!respuestaMascota.isSuccessful) {
                    LoadingManager.hideLoading(this@EditarCarnet)
                    btnGuardar.isEnabled = true
                    Toast.makeText(this@EditarCarnet, "Error al guardar mascota", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val requestPerfil = ActualizarPerfilRequest(telefono = telefono.ifBlank { null }, direccion = direccion.ifBlank { null })
                val respuestaPerfil = api.actualizarPerfil(requestPerfil)

                LoadingManager.hideLoading(this@EditarCarnet)
                btnGuardar.isEnabled = true

                if (respuestaPerfil.isSuccessful) {
                    Toast.makeText(this@EditarCarnet, "¡Cambios guardados exitosamente!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditarCarnet, "Error al guardar contacto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                LoadingManager.hideLoading(this@EditarCarnet)
                btnGuardar.isEnabled = true
                Toast.makeText(this@EditarCarnet, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Adaptador interno para las fotos de las razas
    class RazaAdapter(context: Context, private val razas: List<RazaMascota>) : ArrayAdapter<RazaMascota>(context, 0, razas) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View = crearFila(position, convertView, parent)
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View = crearFila(position, convertView, parent)

        private fun crearFila(position: Int, convertView: View?, parent: ViewGroup): View {
            val fila = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_raza, parent, false)
            val razaActual = getItem(position)
            val ivFoto = fila.findViewById<ImageView>(R.id.iv_raza_foto)
            val tvNombre = fila.findViewById<TextView>(R.id.tv_raza_nombre)

            razaActual?.let {
                ivFoto.setImageResource(it.imagenAId)
                tvNombre.text = it.nombre
            }
            return fila
        }
    }
}
