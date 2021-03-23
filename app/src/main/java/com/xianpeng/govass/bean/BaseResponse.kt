package com.xianpeng.govass.bean

data class BaseResponse(
    var code: Int = -1,
    var msg: String = "",
    var data: String = ""
)