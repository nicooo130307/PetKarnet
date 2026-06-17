const db = require('../config/db');

// crea una mascota (requiere dueño autenticado)
exports.crear = async (req, res) => {
  const { nombre, especie, raza, fecha_nacimiento, foto, sexo, peso } = req.body;
  const id_usuario = req.usuario.id; // del token JWT

  if (!nombre || !especie) {
    return res.status(400).json({ error: 'Nombre y especie son obligatorios' });
  }

  const especiesPermitidas = ['perro', 'gato', 'otro', 'Perro', 'Gato', 'Otro'];
  if (!especiesPermitidas.includes(especie)) {
    return res.status(400).json({ error: 'Especie no válida' });
  }

  try {
    const [resultado] = await db.promise().query(
      'INSERT INTO mascotas (id_usuario, nombre, especie, raza, fecha_nacimiento, foto, sexo, peso) VALUES (?, ?, ?, ?, ?, ?,?,?)',
      [id_usuario, nombre, especie, raza || null, fecha_nacimiento || null, foto || null, sexo, peso]
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
  const { nombre, especie, raza, fecha_nacimiento, foto, sexo, peso } = req.body;

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
    // Preparar valores que respeten los originales si vienen vacíos o nulos
    const nuevoSexo = (sexo != null && sexo.trim() !== '') ? sexo.trim() : mascotas[0].sexo;
    let nuevoPeso;
    if (peso != null && peso.trim() !== '') {
      const parsed = parseFloat(peso);
      nuevoPeso = isNaN(parsed) ? mascotas[0].peso : parsed;
    } else {
      nuevoPeso = mascotas[0].peso;
    }

    await db.promise().query(
      'UPDATE mascotas SET nombre = ?, especie = ?, raza = ?, fecha_nacimiento = ?, foto = ?, sexo = ?, peso = ? WHERE id = ?',
      [
        nombre || mascotas[0].nombre,
        especie || mascotas[0].especie,
        raza !== undefined ? raza : mascotas[0].raza,
        fecha_nacimiento !== undefined ? fecha_nacimiento : mascotas[0].fecha_nacimiento,
        foto !== undefined ? foto : mascotas[0].foto,
        nuevoSexo,
        nuevoPeso,
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

    // --- NUEVO: Limpiamos las tablas dependientes primero ---
    await db.promise().query('DELETE FROM historial_vacunacion WHERE id_mascota = ?', [id]);
     await db.promise().query('DELETE FROM citas WHERE id_mascota = ?', [id]);
    // --------------------------------------------------------

    // Ahora sí, eliminamos a la mascota con seguridad
    await db.promise().query('DELETE FROM mascotas WHERE id = ?', [id]);

    res.json({ mensaje: 'Mascota y su historial eliminados exitosamente' });
  } catch (error) {
    console.error('Error al eliminar mascota:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
};

// Obtener página pública de una mascota (HTML para QR)
exports.infoPublica = async (req, res) => {
  const { id } = req.params;

  try {
    const [mascotas] = await db.promise().query(
      `SELECT m.id, m.nombre, m.especie, m.raza, m.fecha_nacimiento, m.foto, m.sexo, m.peso,
              u.nombre as dueno_nombre, u.telefono as dueno_telefono, u.direccion as dueno_direccion
       FROM mascotas m
       JOIN usuarios u ON m.id_usuario = u.id
       WHERE m.id = ?`,
      [id]
    );

    if (mascotas.length === 0) {
      return res.status(404).send('<h1>Mascota no encontrada</h1>');
    }

    const mascota = mascotas[0];

    // Obtener las últimas 3 vacunas
    const [vacunas] = await db.promise().query(
      `SELECT tipo_vacuna, fecha_aplicacion, proxima_dosis
       FROM historial_vacunacion
       WHERE id_mascota = ?
       ORDER BY fecha_aplicacion DESC
       LIMIT 3`,
      [id]
    );

    // Construir filas de vacunas
    let vacunasHTML = '';
    if (vacunas.length > 0) {
      vacunas.forEach(v => {
        vacunasHTML += `
          <tr>
            <td>${v.tipo_vacuna}</td>
            <td>${v.fecha_aplicacion}</td>
            <td>${v.proxima_dosis || 'No programada'}</td>
          </tr>`;
      });
    } else {
      vacunasHTML = '<tr><td colspan="3">Sin vacunas registradas</td></tr>';
    }

    // Foto de la mascota o placeholder
    const fotoSrc = mascota.foto || 'https://via.placeholder.com/150/cccccc/ffffff?text=Sin+foto';

    // HTML completo
    const html = `
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>PetKarnet - ${mascota.nombre}</title>
  <style>
   * { margin: 0; padding: 0; box-sizing: border-box; }
   body { font-family: Arial, sans-serif; background: #F3F4F6; padding: 20px; }
   .card { max-width: 500px; margin: 0 auto; background: white; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
   .header { background: #2196F3; padding: 30px 20px 20px; text-align: center; border-radius: 0 0 20px 20px; }
   .header h1 { color: white; font-size: 28px; margin-bottom: 10px; }
   .header .paw { font-size: 40px; }
   .content { padding: 20px; }
   .foto { width: 120px; height: 120px; border-radius: 50%; border: 4px solid #FFC107; margin: 0 auto 20px; display: block; background: white; object-fit: cover; }
   h2 { color: #333; margin-bottom: 16px; }
   .info { margin-bottom: 20px; }
   .info p { margin: 8px 0; color: #555; font-size: 16px; }
   .info strong { color: #333; }
   table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
   th { background: #2196F3; color: white; padding: 10px; text-align: left; }
   td { padding: 10px; border-bottom: 1px solid #ddd; }
   .dueno { background: #FFF9C4; padding: 15px; border-radius: 12px; margin-bottom: 20px; }
   .footer { text-align: center; color: #999; font-size: 14px; padding: 20px; }
   .footer img { height: 30px; vertical-align: middle; }
  </style>
</head>
<body>
  <div class="card">
    <div class="header">
      <div class="paw">🐾</div>
      <h1>${mascota.nombre}</h1>
    </div>
    <div class="content">
      <img class="foto" src="${fotoSrc}" alt="${mascota.nombre}">
      <h2>Información de la mascota</h2>
      <div class="info">
        <p><strong>Especie:</strong> ${mascota.especie}</p>
        <p><strong>Raza:</strong> ${mascota.raza || 'No especificada'}</p>
        <p><strong>Sexo:</strong> ${mascota.sexo || 'No especificado'}</p>
        <p><strong>Peso:</strong> ${mascota.peso ? mascota.peso + ' kg' : 'No registrado'}</p>
        <p><strong>Fecha de nacimiento:</strong> ${mascota.fecha_nacimiento || 'No registrada'}</p>
      </div>
      <h2>Vacunas recientes</h2>
      <table>
        <tr><th>Vacuna</th><th>Aplicada</th><th>Próxima dosis</th></tr>
        ${vacunasHTML}
      </table>
      <h2>Contacto del dueño</h2>
      <div class="dueno">
        <p><strong>👤 Nombre:</strong> ${mascota.dueno_nombre}</p>
        <p><strong>📞 Teléfono:</strong> ${mascota.dueno_telefono || 'No registrado'}</p>
        <p><strong>🏠 Dirección:</strong> ${mascota.dueno_direccion || 'No registrada'}</p>
      </div>
    </div>
    <div class="footer">
      <p>Generado por <strong>🐾 PetKarnet</strong></p>
      <p>Carnet digital de vacunación</p>
    </div>
  </div>
</body>
</html>`;

    res.setHeader('Content-Type', 'text/html; charset=utf-8');
    res.send(html);
  } catch (error) {
    console.error('Error al generar página pública de mascota:', error);
    res.status(500).send('<h1>Error interno del servidor</h1>');
  }
};