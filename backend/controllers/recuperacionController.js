const crypto = require('crypto');
const nodemailer = require('nodemailer');
const bcrypt = require('bcrypt');
const db = require('../config/db');

// Configurar el transporte de Gmail UNA SOLA VEZ (fuera de la función)
const transporter = nodemailer.createTransport({
  host: 'smtp.gmail.com',
  port: 465,
  secure: true,
  auth: {
    user: process.env.EMAIL_USER,
    pass: process.env.EMAIL_PASS
  },
  tls: {
    // Forzar IPv4
    family: 4
  }
});

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

    // Enlace real (ajusta la URL base según tu dominio final)
    const enlace = `https://petkarnet.onrender.com/api/usuarios/recuperar/${token}`;

    // Enviar correo real
    await transporter.sendMail({
      from: `"PetKarnet" <${process.env.EMAIL_USER}>`,
      to: email,
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
    });

    console.log(`Correo de recuperación enviado a ${email}`);

    res.json({ mensaje: 'Si el email está registrado, recibirás un enlace de recuperación.' });
  } catch (error) {
    console.error('Error al solicitar recuperación:', error);
    res.status(500).json({ error: 'Error interno del servidor' });
  }
};

// restablecimiento de la contraseña (con  token)
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