const db = require('../config/db');

// agendar una cita solo si el dueño esta autenticado
exports.agendar = async (req, res) => {
  const { id_mascota, id_veterinario, tipo_cita, fecha_hora, notas } = req.body;
  const id_dueno = req.usuario.id;

  // validaciones
  if (!id_mascota || !id_veterinario || !tipo_cita || !fecha_hora) {
    return res.status(400).json({ error: 'Mascota, veterinario, tipo y fecha/hora son obligatorios' });
  }

  const tiposPermitidos = ['revision', 'estetica', 'vacunacion', 'otro'];
  if (!tiposPermitidos.includes(tipo_cita)) {
    return res.status(400).json({ error: 'Tipo de cita no válido' });
  }

  try {
    // verificar que la mascota pertenece al dueño
    const [mascotas] = await db.promise().query(
      'SELECT id FROM mascotas WHERE id = ? AND id_usuario = ?',
      [id_mascota, id_dueno]
    );
    if (mascotas.length === 0) {
      return res.status(403).json({ error: 'La mascota no te pertenece o no existe' });
    }

    // verificar que el veterinario exista y sea veterinario
    const [veterinarios] = await db.promise().query(
      'SELECT id FROM usuarios WHERE id = ? AND rol = ?',
      [id_veterinario, 'veterinario']
    );
    if (veterinarios.length === 0) {
      return res.status(404).json({ error: 'Veterinario no encontrado' });
    }

    // iinsertar cita
    const [resultado] = await db.promise().query(
      `INSERT INTO citas (id_mascota, id_dueno, id_veterinario, tipo_cita, fecha_hora, notas)
       VALUES (?, ?, ?, ?, ?, ?)`,
      [id_mascota, id_dueno, id_veterinario, tipo_cita, fecha_hora, notas || null]
    );

    // registrar automáticamente al dueño como cliente del veterinario (si no existe ya)
    await db.promise().query(
      `INSERT IGNORE INTO clientes_veterinario (id_veterinario, id_dueno) VALUES (?, ?)`,
      [id_veterinario, id_dueno]
    );

    res.status(201).json({ mensaje: 'Cita agendada exitosamente', id: resultado.insertId });
  } catch (error) {
    console.error('Error al agendar cita:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
};

// lista las citas del usuario autenticado
exports.listar = async (req, res) => {
  const userId = req.usuario.id;
  const rol = req.usuario.rol;

  try {
    let citas = [];
    if (rol === 'dueño') {
      [citas] = await db.promise().query(
        `SELECT c.*, m.nombre as mascota_nombre, u.nombre as veterinario_nombre
         FROM citas c
         JOIN mascotas m ON c.id_mascota = m.id
         JOIN usuarios u ON c.id_veterinario = u.id
         WHERE c.id_dueno = ?
         ORDER BY c.fecha_hora DESC`,
        [userId]
      );
    } else if (rol === 'veterinario') {
      [citas] = await db.promise().query(
        `SELECT c.*, m.nombre as mascota_nombre, u.nombre as dueno_nombre
         FROM citas c
         JOIN mascotas m ON c.id_mascota = m.id
         JOIN usuarios u ON c.id_dueno = u.id
         WHERE c.id_veterinario = ?
         ORDER BY c.fecha_hora DESC`,
        [userId]
      );
    } else {
      return res.status(403).json({ error: 'Rol no autorizado para listar citas' });
    }

    res.json(citas);
  } catch (error) {
    console.error('Error al listar citas:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
};

// cambiar el  estado de la cita (confirmar, cancelar, completar)
exports.cambiarEstado = async (req, res) => {
  const { id } = req.params;
  const { estado } = req.body;
  const userId = req.usuario.id;
  const rol = req.usuario.rol;

  const estadosPermitidos = ['confirmada', 'cancelada', 'completada'];
  if (!estadosPermitidos.includes(estado)) {
    return res.status(400).json({ error: 'Estado no válido' });
  }

  try {
    // obtener la cita
    const [citas] = await db.promise().query('SELECT * FROM citas WHERE id = ?', [id]);
    if (citas.length === 0) {
      return res.status(404).json({ error: 'Cita no encontrada' });
    }

    const cita = citas[0];

    // permisos: el dueño puede cancelar solo sus citas; el veterinario puede cambiar a confirmada/completada/cancelar
    if (rol === 'dueño') {
      if (cita.id_dueno !== userId) {
        return res.status(403).json({ error: 'No puedes modificar esta cita' });
      }
      // Un dueño solo puede cancelar
      if (estado !== 'cancelada') {
        return res.status(403).json({ error: 'Solo puedes cancelar la cita' });
      }
    } else if (rol === 'veterinario') {
      if (cita.id_veterinario !== userId) {
        return res.status(403).json({ error: 'No puedes modificar esta cita' });
      }
      // el veterinario puede confirmar, completar o cancelar
    } else {
      return res.status(403).json({ error: 'Rol no autorizado' });
    }

    await db.promise().query('UPDATE citas SET estado = ? WHERE id = ?', [estado, id]);

    res.json({ mensaje: 'Estado de cita actualizado exitosamente' });
  } catch (error) {
    console.error('Error al cambiar estado de cita:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
};