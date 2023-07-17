/*
*
* 统一响应工具类
*
* */

/**
 * 一个code 枚举，所有的code都从这里取出
 * @type {{}}
 */
let code = {
    ok: 20000,//通用成功响应
    error: 20001,// 通用失败响应
    unregistered:20002,//未登陆响应


}

function R() {
    //响应码
    this.code = 200;
    //响应信息
    this.message = "";
    //响应数据
    this.data = {};

    /**
     * 设置响应信息
     * @param message
     */
    this.setMessage = function (message) {
        this.message = message
        return this //返回自己，形成链式调用
    }

    /**
     * 设置响应码
     * @param code
     * @returns {setCode}
     */
    this.setCode = function (code) {
        this.code = code
        return this //返回自己，形成链式调用
    }

    /**
     * 设置响应的数据
     * @param data
     * @returns {R}
     */
    this.setData = function (data) {
        this.data = data
        return this //返回自己，形成链式调用
    }
}

/**
 * 响应成功时调用的方法
 */
function ok() {
    let r = new R();
    r.code = code.ok;
    r.message = "成功"

    return r;
}

/**
 * 响应成功时调用的方法
 */
function error() {
    let r = new R();
    r.code = code.error;
    r.message = "失败"

    return r;
}


module.exports = {
    error,
    ok,
    code
}