package com.xianpeng.govass.activity.common

data class SystemNoticeBase(
    var code: Int = -1,
    var msg: String = "",
    var data: SystemNoticeList? = null
) {
    data class SystemNoticeList(
        var list: List<SystemNoticeItem>? = null
    ) {
        data class SystemNoticeItem(
            var id: Int = -1,
            var title: String = "",
            var  imageUrl: String = "",
            var content: String = "",
            var contentType: Int = -1,
            var createTime: String = ""
        )
    }
}