package com.xianpeng.govass.fragment.mailist

/**
 * @ClassName: 通讯录分组实体类
 * @Author: xianpeng
 * @Date: 2020/7/11 17:55
 */
class GroupRes(var msg: String? = null, var code: Int = -1, var data: GroupDataList? = null) {
    data class GroupDataList(var list: ArrayList<GroupData>? = null) {
        data class GroupData(
            var name: String? = null,
            var id: Int = -1,
            var type: Int = -1,
            var remark: String? = null
        )
    }
}