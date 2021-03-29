package com.xianpeng.govass.bean

data class BaseGovassResponse(
    var code: Int = -1,
    var msg: String = "",
    var data: LoginData?
) {
    data class LoginData(
        var expire: Int = -1,
        var token: String = ""
    )
}