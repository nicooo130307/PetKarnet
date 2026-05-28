const express = require('express');
const router = express.Router();
const usuariosController = require('../controllers/usuariosController');
const auth = require('../middleware/auth');
const recuperacionController = require('../controllers/recuperacionController');

router.post('/registro', usuariosController.registro);
router.post('/login', usuariosController.login);
router.get('/perfil', auth, usuariosController.perfil);
router.post('/recuperar', recuperacionController.solicitar);
router.get('/recuperar/:token', recuperacionController.restablecer);
router.get('/veterinarios', usuariosController.listarVeterinarios);

module.exports = router;