package com.xianpeng.govass.bean

data class BaseResponse(
    var errorCode: Int = 0,
    var errorMsg: String = "",
    var data: Object?
) {
    data class DetailInfo(
        var aipre: Int = 0,
        var contain: String = "",
        var explain: String = "",
        var name: String = "",
        var tip: String = "",
        var type: Int = 0       // 0 - 可回收垃圾 1 - 有害垃圾 2 - 厨余垃圾 3 - 其他垃圾
    )
}