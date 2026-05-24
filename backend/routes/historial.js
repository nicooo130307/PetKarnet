const express = require('express');
const router = express.Router();
const historialController = require('../controllers/historialController');
const auth = require('../middleware/auth');

// registro de vacunas (solo veterinarios)
router.post('/', auth, historialController.agregarVacuna);

// ver historial de una mascota router.get('/:id_mascota', auth, historialController.obtenerHistorial);

module.exports = router;