package com.xianpeng.govass.activity.feedback

import com.xianpeng.govass.bean.Attachment

data class FeedBackInfo(
    var msg: String? = null,
    var code: Int = -1,
    var data: FeedBackDetail? = null
) {
    data class FeedBackDetail(
        var title: String? = null,
        var id: Int = -1,
        var userId: Int = -1,
        var status: Int = -1,
        var content: String? = null,
        var answer: String? = null,
        var username: String? = null,
        var enterpriseName: String? = null,
        var attachmentList: List<Attachment>? = null
    )
}