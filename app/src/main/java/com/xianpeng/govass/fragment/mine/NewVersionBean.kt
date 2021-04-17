package com.xianpeng.govass.fragment.mine

class NewVersionBean(
    var code: Int = -1,
    var msg: String? = null,
    var data: NewVersionDetail? = null
) {
    data class NewVersionDetail(
        var id: Int = -1,
        var device: Int = -1,
        var appVersion: String? = null,
        var changeLog: String? = null,
        var url: String? = null
    )
}