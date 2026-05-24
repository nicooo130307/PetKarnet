const express = require('express');
const router = express.Router();
const dispositivosController = require('../controllers/dispositivosController');
const auth = require('../middleware/auth');

router.post('/', auth, dispositivosController.registrar);

module.exports = router;