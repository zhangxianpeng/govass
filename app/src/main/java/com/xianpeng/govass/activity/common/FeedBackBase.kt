package com.xianpeng.govass.activity.common

data class FeedBackBase(
    var code: Int = -1,
    var msg: String = "",
    var data: FeedBackList? = null
) {
    data class FeedBackList(
        var list: List<FeedBackItem>? = null
    ) {
        data class FeedBackItem(
            var id: Int = -1,
            var status: Int = -1,
            var title: String = "",
            var content: String = "",
            var answer: String = "",
            var answerTime: String = "",
            var createTime: String = "",
            var username: String = ""
        )
    }
}