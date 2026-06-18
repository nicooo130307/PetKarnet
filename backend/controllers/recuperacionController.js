const crypto = require('crypto');
const bcrypt = require('bcrypt');
const db = require('../config/db');
const sgMail = require('@sendgrid/mail');


sgMail.setApiKey(process.env.SENDGRID_API_KEY);

exports.solicitar = async (req, res) => {
  const { email } = req.body;

  if (!email) {
    return res.status(400).json({ error: 'El email es obligatorio' });
  }

  try {
    const [usuarios] = await db.promise().query(
      'SELECT id, nombre FROM usuarios WHERE email = ? AND activo = TRUE',
      [email]
    );

    if (usuarios.length === 0) {
      return res.json({ mensaje: 'Si el email está registrado, recibirás un enlace de recuperación.' });
    }

    const usuario = usuarios[0];
    const token = crypto.randomBytes(32).toString('hex');
    const expiracion = new Date(Date.now() + 30 * 60 * 1000); // 30 minutos

    await db.promise().query(
      'UPDATE usuarios SET token_recuperacion = ?, token_expiracion = ? WHERE id = ?',
      [token, expiracion, usuario.id]
    );

    const enlace = `https://petkarnet.onrender.com/recuperar?token=${token}`;

    
    const msg = {
      to: email,                                 
      from: 'angelemiliorr@gmail.com',  
      subject: 'Recuperación de contraseña - PetKarnet',
      html: `
        <h2>Hola ${usuario.nombre},</h2>
        <p>Has solicitado restablecer tu contraseña en <strong>PetKarnet</strong>.</p>
        <p>Haz clic en el siguiente enlace para crear una nueva contraseña (válido por 30 minutos):</p>
        <p><a href="${enlace}" style="background-color:#4CAF50;color:white;padding:10px 20px;text-decoration:none;border-radius:5px;">Restablecer contraseña</a></p>
        <p>Si no solicitaste este cambio, ignora este mensaje.</p>
        <br/>
        <p>Atentamente,<br/>El equipo de PetKarnet 🐾</p>
      `
    };

    await sgMail.send(msg);
    console.log(`Correo de recuperación enviado a ${email}`);

    res.json({ mensaje: 'Si el email está registrado, recibirás un enlace de recuperación.' });
  } catch (error) {
    console.error('Error al solicitar recuperación:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
};


exports.restablecer = async (req, res) => {
  const { token } = req.params;
  const { nueva_password } = req.body;

  if (!nueva_password || nueva_password.length < 6) {
    return res.status(400).json({ error: 'La nueva contraseña debe tener al menos 6 caracteres' });
  }

  try {
    const [usuarios] = await db.promise().query(
      'SELECT id FROM usuarios WHERE token_recuperacion = ? AND token_expiracion > NOW()',
      [token]
    );

    if (usuarios.length === 0) {
      return res.status(400).json({ error: 'Token inválido o expirado' });
    }

    const passwordHash = await bcrypt.hash(nueva_password, 10);
    await db.promise().query(
      'UPDATE usuarios SET password_hash = ?, token_recuperacion = NULL, token_expiracion = NULL WHERE id = ?',
      [passwordHash, usuarios[0].id]
    );

    res.json({ mensaje: 'Contraseña restablecida exitosamente' });
  } catch (error) {
    console.error('Error al restablecer contraseña:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
}; 