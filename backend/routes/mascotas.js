const express = require('express');
const router = express.Router();
const mascotasController = require('../controllers/mascotasController');
const auth = require('../middleware/auth');

//  tokens para las rutaas
router.post('/', auth, mascotasController.crear);
router.get('/', auth, mascotasController.listar);
router.get('/:id', auth, mascotasController.obtenerPorId);
router.put('/:id', auth, mascotasController.actualizar);
router.delete('/:id', auth, mascotasController.eliminar);

module.exports = router;