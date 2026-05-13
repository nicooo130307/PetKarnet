package com.example.petkarnet

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import androidx.activity.result.contract.ActivityResultContracts


data class RazaMascota(val nombre: String, val imagenAId: Int) {
    // Sobrescribimos el toString para que al seleccionar, solo ponga el nombre en la caja
    override fun toString(): String {
        return nombre
    }
}


class RegistroMascota : AppCompatActivity() {

    private var uriFotoSeleccionada: Uri? = null

    private val abrirGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            // Si el usuario sí eligió una foto, la guardamos
            uriFotoSeleccionada = uri

            // Enlazamos la imagen y se la asignamos
            val ivFotoMascota = findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.iv_foto_mascota)
            ivFotoMascota.setImageURI(uri)

            // Pro-Tip: Cambiamos cómo se ajusta la imagen para que llene el cuadro
            ivFotoMascota.scaleType = ImageView.ScaleType.CENTER_CROP
        }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_mascota)
        enableEdgeToEdge()


        val ivFotoMascota = findViewById<ImageView>(R.id.iv_foto_mascota)

        ivFotoMascota.setOnClickListener {
            // Lanzamos el contrato pidiendo específicamente imágenes
            abrirGaleria.launch("image/*")
        }


        // 1. Enlazamos las vistas
        val autoCompleteRaza = findViewById<AutoCompleteTextView>(R.id.et_raza)
        val rgEspecie = findViewById<RadioGroup>(R.id.rg_especie)


        val tilNombre = findViewById<TextInputLayout>(R.id.til_nombre_mascota)
        val etNombre = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_nombre_mascota)

        val tilRaza = findViewById<TextInputLayout>(R.id.til_raza)
        // OJO AQUÍ: Raza ahora es un AutoCompleteTextView
        val etRaza = findViewById<AutoCompleteTextView>(R.id.et_raza)

        val tilSexo = findViewById<TextInputLayout>(R.id.til_sexo)
        // OJO AQUÍ: Sexo también es un AutoCompleteTextView
        val etSexo = findViewById<AutoCompleteTextView>(R.id.et_sexo)

        val tilColor = findViewById<TextInputLayout>(R.id.til_color)
        val etColor = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_color)

        val tilEdad = findViewById<TextInputLayout>(R.id.til_edad)
        val etEdad = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_edad)

        val tilPeso = findViewById<TextInputLayout>(R.id.til_peso)
        val etPeso = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_peso)


        val tilNombreDueno = findViewById<TextInputLayout>(R.id.til_nombre_dueno)
        val etNombreDueno = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_nombre_dueno)

        val tilTelefono = findViewById<TextInputLayout>(R.id.til_telefono_dueno)
        val etTelefono = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_telefono_dueno)

        val tilDireccion = findViewById<TextInputLayout>(R.id.til_direccion_dueno)
        val etDireccion = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_direccion_dueno)


        val btnGuardar = findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_guardar_mascota)



        btnGuardar.setOnClickListener {

            // Limpiamos los mensajes de error previos
            tilNombre.error = null
            tilRaza.error = null
            tilSexo.error = null
            tilColor.error = null
            tilEdad.error = null
            tilPeso.error = null
            tilNombreDueno.error = null
            tilTelefono.error = null
            tilDireccion.error = null


            // Extraemos lo que el usuario escribió o seleccionó
            val nombre = etNombre.text.toString().trim()
            val raza = etRaza.text.toString().trim()
            val sexo = etSexo.text.toString().trim()
            val color = etColor.text.toString().trim()
            val edad = etEdad.text.toString().trim()
            val peso = etPeso.text.toString().trim()
            val nombreDueno = etNombreDueno.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()

            var formularioValido = true

            // 3. Validaciones individuales
            if (nombre.isEmpty()) {
                tilNombre.error = "Ingresa el nombre de la mascota"
                formularioValido = false
            }

            if (raza.isEmpty()) {
                tilRaza.error = "Selecciona una raza de la lista"
                formularioValido = false
            }

            if (sexo.isEmpty()) {
                tilSexo.error = "Selecciona el sexo"
                formularioValido = false
            }

            if (color.isEmpty()) {
                tilColor.error = "Ingresa el color o señas particulares"
                formularioValido = false
            }

            if (edad.isEmpty()) {
                tilEdad.error = "Ingresa la edad aproximada"
                formularioValido = false
            }

            if (peso.isEmpty()) {
                tilPeso.error = "El peso es obligatorio para dosis médicas"
                formularioValido = false
            }


            if (nombreDueno.isEmpty()) {
                tilNombreDueno.error = "Ingresa el nombre del responsable"
                formularioValido = false
            }

            if (telefono.isEmpty() || telefono.length < 10) {
                tilTelefono.error = "Ingresa un número válido de 10 dígitos"
                formularioValido = false
            }

            if (direccion.isEmpty()) {
                tilDireccion.error = "La dirección es necesaria para emergencias"
                formularioValido = false
            }


            if (formularioValido) {
                // Aquí en el futuro enviaremos esto a la base de datos
                println("¡Perfil creado con éxito para $nombre!")
                println("Datos: $raza, $sexo, $color, $edad, $peso Kg")

                val intent = android.content.Intent(this, MenuDueno::class.java)

                // 2. Iniciamos el viaje
                startActivity(intent)

                // 3. (Pro-Tip) Destruimos la pantalla de registro
                finish()
            }
        }



        // 2. Creamos nuestras listas de razas
        val razasPerro = listOf(
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
            RazaMascota( "Otro...", R.drawable.otro)
        )

        val razasGato = listOf(
            RazaMascota("Mestizo (Sin raza específica)", R.drawable.mestizon),
            RazaMascota("Siamés",R.drawable.siames),
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
            RazaMascota( "Otro...", R.drawable.otron)

        )


        // 1. Enlazamos la vista del Sexo
        val autoCompleteSexo = findViewById<AutoCompleteTextView>(R.id.et_sexo)

        // 2. Creamos las opciones estandarizadas
        val opcionesSexo = arrayOf("Macho", "Hembra")

        // 3. Creamos un adaptador simple de texto (nativa de Android)
        val adapterSexo = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            opcionesSexo
        )

        // 4. Se lo conectamos a la caja de texto
        autoCompleteSexo.setAdapter(adapterSexo)




        // 3. Función auxiliar para cambiar el adaptador del menú
        fun actualizarMenuRazas(razas: List<RazaMascota>) {
            val adapter = RazaAdapter(this, razas)
            autoCompleteRaza.setAdapter(adapter)
            autoCompleteRaza.setText(razas[0].nombre, false)
        }

        // 4. Inicializamos el menú con Perros (porque en tu XML rb_perro es checked="true")
        actualizarMenuRazas(razasPerro)

        // 5. Escuchamos si el usuario cambia entre Perro y Gato
        rgEspecie.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_perro) {
                actualizarMenuRazas(razasPerro)
            } else if (checkedId == R.id.rb_gato) {
                actualizarMenuRazas(razasGato)
            }
        }
    }
}

class RazaAdapter(context: Context, private val razas: List<RazaMascota>) :
    ArrayAdapter<RazaMascota>(context, 0, razas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return crearFila(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return crearFila(position, convertView, parent)
    }

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


