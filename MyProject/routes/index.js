var express = require('express');
var router = express.Router();

var hello_jk = require('../controllers/hello_jk');

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.post('/hello_jk/read_ipr', hello_jk.read_ipr);
router.post('/hello_jk/read_csv', hello_jk.read_csv);
module.exports = router;

