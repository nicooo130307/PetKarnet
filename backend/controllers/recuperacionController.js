const crypto = require('crypto');
const nodemailer = require('nodemailer');
const bcrypt = require('bcrypt');
const db = require('../config/db');

// solicita token de recuperación
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

    // configurar transporte de correo (usamos Ethereal como ejemplo; después puedes cambiarlo por Gmail)
    const testAccount = await nodemailer.createTestAccount();
    const transporter = nodemailer.createTransport({
      host: 'smtp.ethereal.email',
      port: 587,
      secure: false,
      auth: {
        user: testAccount.user,
        pass: testAccount.pass
      }
    });

    const enlace = `http://localhost:3000/api/usuarios/recuperar/${token}`;

    await transporter.sendMail({
      from: '"PetKarnet" <no-reply@petkarnet.com>',
      to: email,
      subject: 'Recuperación de contraseña',
      text: `Hola ${usuario.nombre},\n\nPara restablecer tu contraseña, haz clic en el siguiente enlace (válido por 30 minutos):\n${enlace}\n\nSi no solicitaste esto, ignora este mensaje.`
    });

    console.log('Correo de prueba enviado a:', testAccount.user);
    console.log('Enlace de recuperación:', enlace);

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