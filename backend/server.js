const express = require('express');
const cors = require('cors');
require('dotenv').config();

const db = require('./config/db');
const nodemailer = require('nodemailer');

const app = express();

// middlewares json y cors
app.use(cors());
app.use(express.json());


const usuariosRoutes = require('./routes/usuarios');
app.use('/api/usuarios', usuariosRoutes);

const mascotasRoutes = require('./routes/mascotas');
app.use('/api/mascotas', mascotasRoutes);

const historialRoutes = require('./routes/historial');
app.use('/api/historial', historialRoutes);

const citasRoutes = require('./routes/citas');
app.use('/api/citas', citasRoutes);

const dispositivosRoutes = require('./routes/dispositivos');
app.use('/api/dispositivos', dispositivosRoutes);


app.get('/', (req, res) => {
  res.send('¡PetKarnet API funcionando!');
});


// En server.js, agrega esto temporalmente (antes de app.listen)
app.get('/test-email', async (req, res) => {
  const transporter = nodemailer.createTransport({
    host: '74.125.200.109',
    port: 465,
    secure: true,
    auth: {
      user: process.env.EMAIL_USER,
      pass: process.env.EMAIL_PASS
    }
  });

  try {
    await transporter.sendMail({
      from: process.env.EMAIL_USER,
      to: process.env.EMAIL_USER, // enviar a ti mismo
      subject: 'Test de conexión SMTP',
      text: 'Si ves esto, la conexión SMTP funciona.'
    });
    res.json({ mensaje: 'Correo enviado exitosamente' });
  } catch (error) {
    console.error('Error en test-email:', error);
    res.status(500).json({ error: error.message, code: error.code });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Servidor corriendo en http://localhost:${PORT}`);
});