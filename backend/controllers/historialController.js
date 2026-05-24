const db = require('../config/db');

// agregue vacunas (solo veterinarios autenticados)
exports.agregarVacuna = async (req, res) => {
  // verifica que el usuario autenticado sea veterinario
  if (req.usuario.rol !== 'veterinario') {
    return res.status(403).json({ error: 'Solo los veterinarios pueden registrar vacunas' });
  }

  const { id_mascota, tipo_vacuna, fecha_aplicacion, proxima_dosis, foto_comprobante, notas } = req.body;
  const id_veterinario = req.usuario.id;

  if (!id_mascota || !tipo_vacuna || !fecha_aplicacion) {
    return res.status(400).json({ error: 'Mascota, tipo de vacuna y fecha de aplicación son obligatorios' });
  }

  try {
    //  registro
    const [resultado] = await db.promise().query(
      `INSERT INTO historial_vacunacion 
       (id_mascota, id_veterinario, tipo_vacuna, fecha_aplicacion, proxima_dosis, foto_comprobante, notas)
       VALUES (?, ?, ?, ?, ?, ?, ?)`,
      [id_mascota, id_veterinario, tipo_vacuna, fecha_aplicacion, proxima_dosis || null, foto_comprobante || null, notas || null]
    );

    res.status(201).json({ mensaje: 'Vacuna registrada exitosamente', id: resultado.insertId });
  } catch (error) {
    console.error('Error al registrar vacuna:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
};
