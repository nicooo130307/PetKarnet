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