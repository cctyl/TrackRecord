var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var jwt = require("./utils/jwt") //jwt生成
var R = require("./utils/R")
var app = express();

//------------------中间件配置区域-----------------------
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({extended: false}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));


//----------------------一级路由配置区域  start-----------------------------


//设置跨域访问
app.all("*", function (req, res, next) {

    res.header("Access-Control-Allow-Origin", req.headers.origin || '*');
    //允许的header类型
    res.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,token") //跨域允许的请求方式
    res.header("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS")
    res.header("Access-Control-Allow-Credentials", true)//可以带cookies
    if (req.method == 'OPTIONS') {
        res.sendStatus(200);
    } else {
        next();
    }
})

//--------不需要携带token访问 start----------


//--------不需要携带token访问 end----------

//这里输入你的token
let accessToken = "你的token";

/**
 * token拦截器，这个方法后面的所有接口都需要携带token
 */
app.use((req, res, next) => {
    //1.从请求头中拿到token
    let token = req.headers.token
    //2.token非空判断
    if (token) {
        let result = (token == accessToken)
        if (result) {
            next()
        } else {
            res.json(R.error().setMessage("请登录后再访问"))
        }
    } else {
        //没携带token访问，不放行
        res.json(R.error().setMessage("请登录后再访问"))
    }

})


//--------需要携带token访问 start----------
var gpsRouter = require("./routes/gps")
app.use("/gps", gpsRouter)

//--------需要携带token访问 end----------


//----------------------一级路由配置区域  end-----------------------------

// 处理404 找不到 路由 / 文件异常
app.use(function (req, res, next) {
    //这里放行后，会走到最下面的全局异常处理方法
    next(createError(404));
});

// 全局异常处理
app.use(function (err, req, res, next) {
    console.log("异常处理被触发")
    //打印异常日志
    console.log(err)
    //发送错误响应
    res.json(R.error().setCode(err.status || 500).setMessage(err.message))
});

module.exports = app;
