let multer = require("multer")
let path = require("path")
let moment = require("moment")
const fse = require('fs-extra')
const fs = require('fs')

//对 multer 上传进行一个配置
const storage = multer.diskStorage({

    //设置上传的目录
    destination: function (req, file, cb) {

        //1. 获取当前日期
        let dataStr = moment().format('YYYYMMDD');
        console.log(dataStr)
        //1.1 拼接成一个目录，这种方式可以帮你处理中间的斜杠
        let dir = path.join("public/uploads", dataStr)

        //2. 按照日期生成图片存储目录
        //2.1先创建目录，因为多层级, 用一个第三方库来实现
        fse.ensureDir(dir, err => {
            if (!err) {
                console.log("目录创建成功")
                cb(null, dir)
            }
        })

    },

    //修改上传后的文件名
    filename: function (req, file, cb) {
        //1. 获取后缀名
        let extname = path.extname(file.originalname);
        //2. 根据时间戳生成文件名
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9)
        cb(null, file.fieldname + '-' + uniqueSuffix + extname)
    }
})
const upload = multer({storage: storage})


module.exports = upload