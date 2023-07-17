let gpsModel = require("../model/gps")

module.exports = {

    //编写service方法时注意，需要用promise包裹住

    /**
     * 保存自己
     * @param exampleData
     * @returns {Promise<unknown>}
     */
    saveExample(exampleData) {
        return new Promise((resolve, reject) => {
            let e = new gpsModel(exampleData)

            e.save(function (err, doc) {
                if (err)
                    resolve(false)
                else
                    resolve(doc)
            })

        })
    },

    /**
     * 存储多条数据
     * @param exampleData
     * @returns {Promise<unknown>}
     */
    saveMany(rawList) {

        //处理好数据
        for (let i = 0; i < rawList.length; i++) {
            let tempGps = rawList[i]
            tempGps.time = new Date(tempGps.time)
        }

        return new Promise((resolve, reject) => {
            gpsModel.insertMany(rawList, function (err, doc) {
                if (err)
                    resolve(false)
                else
                    resolve(doc)
            })

        })
    },


    findGpsByTime(startTime, endTime) {
        return new Promise((resolve, reject) => {

            gpsModel.find(
                {

                    "created": {
                        $gt: new Date(startTime * 1),
                        $lt: new Date(endTime * 1)
                    }
                },
                (error, result) => {

                    if (error) {

                        console.log(error)
                        resolve(false)
                    } else {
                        resolve(result)
                    }
                }
            )
        })
    }


}
