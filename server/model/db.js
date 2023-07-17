/*

用于连接数据库

 */
const mongoose = require('mongoose');

/*
mongoose.connect('mongodb://账号:密码@127.0.0.1:27017/gps?authSource=admin',function (err) {

    if (err)
        console.log(err)
    else
        console.log("数据库连接成功")
});
*/
//无密码时：
mongoose.connect('mongodb://localhost:27017/gps',function (err) {

    if (err)
        console.log(err)
    else
        console.log("数据库连接成功")
});


module.exports = mongoose
