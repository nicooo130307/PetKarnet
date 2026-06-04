const db = require('../config/db');

// Registro de vacunas (Adaptado para que el dueño también pueda registrar)
exports.agregarVacuna = async (req, res) => {
  const { id_mascota, tipo_vacuna, fecha_aplicacion, proxima_dosis, foto_comprobante, notas } = req.body;

  // Si el usuario es veterinario, guardamos su ID para "certificar" la vacuna.
  // Si es el dueño, se guarda como null (auto-reportada).
  const id_veterinario = req.usuario.rol === 'veterinario' ? req.usuario.id : null;

  if (!id_mascota || !tipo_vacuna || !fecha_aplicacion) {
    return res.status(400).json({ error: 'Mascota, tipo de vacuna y fecha de aplicación son obligatorios' });
  }

  try {
    // Ejecutamos el registro en la base de datos
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


// Endpoint para obtener el historial de vacunas de una mascota
exports.obtenerHistorial = async (req, res) => {
  const { idMascota } = req.params;

  try {
    // Usamos LEFT JOIN para que traiga la vacuna aunque el id_veterinario sea NULL
    const [historial] = await db.promise().query(`
      SELECT
        h.id,
        h.id_mascota,
        h.id_veterinario,
        h.tipo_vacuna,
        DATE_FORMAT(h.fecha_aplicacion, '%d/%m/%Y') as fecha_aplicacion,
        DATE_FORMAT(h.proxima_dosis, '%d/%m/%Y') as proxima_dosis,
        h.foto_comprobante,
        h.notas,
        u.nombre as nombre_veterinario
      FROM historial_vacunacion h
      LEFT JOIN usuarios u ON h.id_veterinario = u.id
      WHERE h.id_mascota = ?
      ORDER BY h.fecha_aplicacion DESC
    `, [idMascota]);

    res.status(200).json(historial);
  } catch (error) {
    console.error('Error al obtener el historial:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
};