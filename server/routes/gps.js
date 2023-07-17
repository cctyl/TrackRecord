var express = require('express');
var router = express.Router();
let R = require("../utils/R") //响应工具类
let gpsService = require('../service/GpsService');

router.post('/add', async function (req, res, next) {
    let body = req.body;
    body.time = new Date(body.time)
    console.log(body)
    if (!body.latitude){
        res.json(R.error().setMessage("数据不完整"))
        return;
    }
    gpsService.saveExample(body)
    console.log("gps数据如下：" + JSON.stringify(body))
    res.json(R.ok())
});

/**
 * 一次添加多个gps数据
 */
router.post('/addMany', async function (req, res, next) {
    let body = req.body;
    if (body.length<1){
        res.json(R.error().setMessage("数据不完整"))
        return;
    }

    gpsService.saveMany(body)
    res.json(R.ok())
});



module.exports = router;
