const express = require('express');
const cors = require('cors');
require('dotenv').config();

const db = require('./config/db');


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


const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Servidor corriendo en http://localhost:${PORT}`);
});