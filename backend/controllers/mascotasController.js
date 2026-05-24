const db = require('../config/db');

// crea una mascota (requiere dueño autenticado)
exports.crear = async (req, res) => {
  const { nombre, especie, raza, fecha_nacimiento, foto } = req.body;
  const id_usuario = req.usuario.id; // del token JWT

  if (!nombre || !especie) {
    return res.status(400).json({ error: 'Nombre y especie son obligatorios' });
  }

  const especiesPermitidas = ['perro', 'gato', 'otro'];
  if (!especiesPermitidas.includes(especie)) {
    return res.status(400).json({ error: 'Especie no válida' });
  }

  try {
    const [resultado] = await db.promise().query(
      'INSERT INTO mascotas (id_usuario, nombre, especie, raza, fecha_nacimiento, foto) VALUES (?, ?, ?, ?, ?, ?)',
      [id_usuario, nombre, especie, raza || null, fecha_nacimiento || null, foto || null]
    );

    res.status(201).json({ mensaje: 'Mascota registrada exitosamente', id: resultado.insertId });
  } catch (error) {
    console.error('Error al crear mascota:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
};

//   las mascotas del dueño autenticado
exports.listar = async (req, res) => {
  const id_usuario = req.usuario.id;

  try {
    const [mascotas] = await db.promise().query(
      'SELECT * FROM mascotas WHERE id_usuario = ? ORDER BY nombre ASC',
      [id_usuario]
    );
    res.json(mascotas);
  } catch (error) {
    console.error('Error al listar mascotas:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
};

// obtener una mascota por su ID (solo si pertenece al dueño)
exports.obtenerPorId = async (req, res) => {
  const { id } = req.params;
  const id_usuario = req.usuario.id;

  try {
    const [mascotas] = await db.promise().query(
      'SELECT * FROM mascotas WHERE id = ? AND id_usuario = ?',
      [id, id_usuario]
    );

    if (mascotas.length === 0) {
      return res.status(404).json({ error: 'Mascota no encontrada' });
    }

    res.json(mascotas[0]);
  } catch (error) {
    console.error('Error al obtener mascota:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
};

// actualizar una mascota
exports.actualizar = async (req, res) => {
  const { id } = req.params;
  const id_usuario = req.usuario.id;
  const { nombre, especie, raza, fecha_nacimiento, foto } = req.body;

  try {
    // verifica que la mascota pertenezca al dueño
    const [mascotas] = await db.promise().query(
      'SELECT * FROM mascotas WHERE id = ? AND id_usuario = ?',
      [id, id_usuario]
    );

    if (mascotas.length === 0) {
      return res.status(404).json({ error: 'Mascota no encontrada o no tienes permiso' });
    }

    // query dinámica
    await db.promise().query(
      'UPDATE mascotas SET nombre = ?, especie = ?, raza = ?, fecha_nacimiento = ?, foto = ? WHERE id = ?',
      [
        nombre || mascotas[0].nombre,
        especie || mascotas[0].especie,
        raza !== undefined ? raza : mascotas[0].raza,
        fecha_nacimiento !== undefined ? fecha_nacimiento : mascotas[0].fecha_nacimiento,
        foto !== undefined ? foto : mascotas[0].foto,
        id
      ]
    );

    res.json({ mensaje: 'Mascota actualizada exitosamente' });
  } catch (error) {
    console.error('Error al actualizar mascota:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
};

// eliminar una mascota
exports.eliminar = async (req, res) => {
  const { id } = req.params;
  const id_usuario = req.usuario.id;

  try {
    const [mascotas] = await db.promise().query(
      'SELECT * FROM mascotas WHERE id = ? AND id_usuario = ?',
      [id, id_usuario]
    );

    if (mascotas.length === 0) {
      return res.status(404).json({ error: 'Mascota no encontrada o no tienes permiso' });
    }

    await db.promise().query('DELETE FROM mascotas WHERE id = ?', [id]);

    res.json({ mensaje: 'Mascota eliminada exitosamente' });
  } catch (error) {
    console.error('Error al eliminar mascota:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
};