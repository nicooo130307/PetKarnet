const db = require('../config/db');

exports.registrar = async (req, res) => {
  const { token_fcm } = req.body;
  const id_usuario = req.usuario.id;

  if (!token_fcm) {
    return res.status(400).json({ error: 'El token FCM es obligatorio' });
  }

  try {
    // evitamos duplicados por si ya se uso el token
    await db.promise().query('DELETE FROM dispositivos WHERE token_fcm = ?', [token_fcm]);

    await db.promise().query(
      'INSERT INTO dispositivos (id_usuario, token_fcm) VALUES (?, ?)',
      [id_usuario, token_fcm]
    );

    res.status(201).json({ mensaje: 'Token FCM registrado exitosamente' });
  } catch (error) {
    console.error('Error al registrar token FCM:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
};