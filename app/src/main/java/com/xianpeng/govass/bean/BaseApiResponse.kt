package com.xianpeng.govass.bean

data class BaseApiResponse(
    var code: Int = -1,
    var msg: String? = null,
    var data: Any = ""
)