const jwt = require('jsonwebtoken');
let sercret = "kvyte_1@snp84";
const Token = {
    /**
     * 根据userId 获取token
     * @param userId
     * @returns {undefined|*}
     */
    encrypt: function (userId) {

        //token 过期时间, 单位是秒
        let expireTime = 604800  //  60 * 60 * 24 * 7

        return jwt.sign({userId}, sercret, {expiresIn: expireTime})
    },


    /**
     * 根据token解析出userId
     * 如果成功，那么能从对象中取出userId。
     * 如果失败，userId为undefined，并且有 errmsg
     * @param token
     * @returns {{data: *}|{errmsg: string}}
     */
    decrypt: function (token) {
        try {
            let data = jwt.verify(token, sercret);
            return data

        } catch (e) {
            if (e.name == "TokenExpiredError") {
                console.log("token已过期")
                return {
                    errmsg: "token已过期"
                }
            } else if (e.name == "JsonWebTokenError") {
                console.log("非法的token")
                return {
                    errmsg: "非法的token"
                }
            } else {
                console.log("json解析出错：" + e.message)
                return {
                    errmsg: "json解析出错：" + e.message
                }
            }
        }
    }
}

module.exports = Token;




