const express = require('express');
const router = express.Router();
const citasController = require('../controllers/citasController');
const auth = require('../middleware/auth');

router.post('/', auth, citasController.agendar);
router.get('/', auth, citasController.listar);
router.patch('/:id/estado', auth, citasController.cambiarEstado);

module.exports = router;