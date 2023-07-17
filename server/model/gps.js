const mongoose = require("./db")

//定义schema
//字段和类型都需要和表中一一对应
var GPSSchema = mongoose.Schema({

    latitude:Number,
    longitude:Number,
    model:String,
    time:Date

},{
    timestamps: {
        createdAt: 'created',
        updatedAt: 'updated'
    }
})

// 第三个参数是数据库中集合的真实名字
var GPSModel =  mongoose.model("gps",GPSSchema,"gps")



//向外暴露模型，因为实际只需要模型来操作数据库
module.exports = GPSModel
