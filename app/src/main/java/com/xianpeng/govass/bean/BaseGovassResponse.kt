package com.xianpeng.govass.bean

data class BaseGovassResponse(
    var code: Int = 0,
    var msg: String = "",
    var data: LoginData?
) {
    data class LoginData(
        var expire: Int = 0,
        var token: String = ""
    )
}