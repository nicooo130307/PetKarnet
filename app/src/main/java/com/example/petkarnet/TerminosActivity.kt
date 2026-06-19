package com.example.petkarnet

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class TerminosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminos)

        val tvTerminos = findViewById<TextView>(R.id.tv_terminos)
        val btnVolver = findViewById<MaterialButton>(R.id.btn_volver_terminos)

        tvTerminos.text = "tvTerminos.text = \"\"\"\n" +
                "TÉRMINOS Y CONDICIONES DE USO – PETKARNET\n" +
                "\n" +
                "Última actualización: 18 de junio de 2026\n" +
                "\n" +
                "POR FAVOR, LEE DETENIDAMENTE ESTOS TÉRMINOS ANTES DE UTILIZAR LA APLICACIÓN.\n" +
                "\n" +
                "1. INTRODUCCIÓN\n" +
                "1.1. Estos Términos y Condiciones regulan el uso de la aplicación PetKarnet (en adelante, \"la App\"), desarrollada como proyecto académico en el CECyT 5. La App está diseñada para facilitar la gestión de información de mascotas, incluyendo historial de vacunación, agendamiento de citas con veterinarios y localización de clínicas veterinarias.\n" +
                "1.2. Al descargar, instalar o utilizar la App, aceptas íntegramente estos Términos. Si no estás de acuerdo con alguno de los puntos, debes desinstalar la App inmediatamente.\n" +
                "\n" +
                "2. DEFINICIONES\n" +
                "2.1. \"Usuario\": Persona natural mayor de 18 años que se registra en la App, ya sea como dueño de mascota, veterinario o administrador.\n" +
                "2.2. \"Dueño\": Usuario que registra una o varias mascotas para gestionar su información.\n" +
                "2.3. \"Veterinario\": Profesional que ofrece servicios a través de la App, responsable de la veracidad de su cédula profesional y de la información médica que registra.\n" +
                "2.4. \"Contenido\": Todo dato, texto, imagen o archivo que el Usuario sube o genera dentro de la App.\n" +
                "\n" +
                "3. REGISTRO Y CUENTA\n" +
                "3.1. Para usar la App, el Usuario debe crear una cuenta proporcionando nombre, correo electrónico y una contraseña segura. La información debe ser veraz y actualizada.\n" +
                "3.2. El Usuario es responsable de mantener la confidencialidad de sus credenciales. Cualquier actividad realizada desde su cuenta se considera efectuada por él.\n" +
                "3.3. La App se reserva el derecho de suspender o cancelar cuentas que infrinjan estos Términos o que contengan información falsa.\n" +
                "\n" +
                "4. PRIVACIDAD Y TRATAMIENTO DE DATOS PERSONALES\n" +
                "4.1. La App recopila los siguientes datos personales: nombre, correo electrónico, número telefónico (opcional), dirección (opcional), fotografía de perfil (opcional) y, en el caso de veterinarios, imagen de la cédula profesional.\n" +
                "4.2. También se recopila información de las mascotas: nombre, especie, raza, fecha de nacimiento, fotografía e historial médico (vacunas, citas, notas veterinarias).\n" +
                "4.3. Los datos se almacenan en una base de datos MySQL segura alojada en Aiven, con conexiones cifradas mediante SSL. Las contraseñas se almacenan utilizando algoritmos de hash (bcrypt) y nunca en texto plano.\n" +
                "4.4. La App no vende, alquila ni comparte los datos personales con terceros, salvo obligación legal expresa.\n" +
                "4.5. Las fotografías de perfil, mascotas y comprobantes de vacunación se almacenan en servicios de nube de terceros (Cloudinary), bajo sus respectivas políticas de seguridad.\n" +
                "4.6. El Usuario puede solicitar la eliminación de sus datos en cualquier momento. La App procederá a la desactivación lógica de la cuenta, manteniendo los registros médicos anonimizados por razones de integridad sanitaria.\n" +
                "\n" +
                "5. USO DE LA APLICACIÓN\n" +
                "5.1. La App está destinada exclusivamente a la gestión de información de mascotas. No debe utilizarse para fines ilegales, fraudulentos o no autorizados.\n" +
                "5.2. El Usuario se compromete a no subir contenido ofensivo, discriminatorio o que infrinja derechos de terceros.\n" +
                "5.3. El Usuario es el único responsable del contenido que genera, incluyendo la veracidad de los datos de sus mascotas y de las credenciales profesionales.\n" +
                "\n" +
                "6. FUNCIONALIDADES Y SERVICIOS\n" +
                "6.1. La App ofrece, entre otras, las siguientes funcionalidades: registro de mascotas, carnet digital de vacunación, agendamiento de citas, geolocalización de clínicas veterinarias y notificaciones de recordatorio.\n" +
                "6.2. La geolocalización utiliza Google Maps API. El uso de esta funcionalidad implica la aceptación de los términos de servicio de Google.\n" +
                "6.3. Las notificaciones push se envían a través de OneSignal. El Usuario puede desactivarlas desde los ajustes de su dispositivo.\n" +
                "6.4. La App puede incluir enlaces a sitios web de terceros (por ejemplo, Google Maps). No nos hacemos responsables del contenido o las políticas de privacidad de dichos sitios.\n" +
                "\n" +
                "7. RESPONSABILIDADES DEL VETERINARIO\n" +
                "7.1. El veterinario se compromete a mantener actualizada su información profesional, incluyendo su cédula y dirección de la clínica.\n" +
                "7.2. El veterinario es el único responsable de los diagnósticos, tratamientos y recomendaciones que emita a través de la App.\n" +
                "7.3. La App no verifica activamente la validez de las cédulas profesionales más allá de la revisión inicial por parte del administrador.\n" +
                "\n" +
                "8. PROPIEDAD INTELECTUAL\n" +
                "8.1. La App, incluyendo su código fuente, diseño, logotipo y textos originales, es propiedad de los desarrolladores. No se concede ninguna licencia sobre los mismos más allá del uso personal de la App.\n" +
                "8.2. El Usuario conserva todos los derechos sobre el contenido que sube (fotos, notas). Al subir contenido, otorga a la App una licencia limitada para almacenarlo y mostrarlo dentro de la plataforma.\n" +
                "\n" +
                "9. LIMITACIÓN DE RESPONSABILIDAD\n" +
                "9.1. La App se proporciona \"tal cual\", sin garantías de disponibilidad continua o ausencia de errores.\n" +
                "9.2. Los desarrolladores no serán responsables por daños directos o indirectos derivados del uso o imposibilidad de uso de la App, incluyendo pérdida de datos, fallos técnicos o errores en la información proporcionada por los usuarios.\n" +
                "9.3. La App no sustituye el consejo veterinario profesional. Ante cualquier emergencia, el dueño debe acudir a un veterinario físico.\n" +
                "\n" +
                "10. MODIFICACIONES DE LOS TÉRMINOS\n" +
                "10.1. Los desarrolladores se reservan el derecho de modificar estos Términos en cualquier momento. Las modificaciones serán notificadas a través de la App y entrarán en vigor en el momento de su publicación.\n" +
                "10.2. El uso continuado de la App después de una modificación implica la aceptación de los nuevos Términos.\n" +
                "\n" +
                "11. ELIMINACIÓN DE LA CUENTA\n" +
                "11.1. El Usuario puede solicitar la eliminación de su cuenta desde la sección de Configuración de la App.\n" +
                "11.2. La eliminación implica la desactivación lógica de la cuenta y la anonimización de los datos personales asociados. Los registros médicos históricos se conservarán de forma anónima para mantener la integridad del historial de las mascotas.\n" +
                "\n" +
                "12. LEGISLACIÓN APLICABLE\n" +
                "12.1. Estos Términos se rigen por la legislación de México. Cualquier controversia se someterá a los tribunales competentes de la Ciudad de México.\n" +
                "\n" +
                "13. CONTACTO\n" +
                "13.1. Para cualquier consulta sobre estos Términos, puedes escribir a: angelemiliorr@gmail.com.\n" +
                "\n" +
                "Al utilizar PetKarnet, confirmas que has leído, entendido y aceptado estos Términos y Condiciones.\n"


        btnVolver.setOnClickListener {
            finish()
        }
    }
}