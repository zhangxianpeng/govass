package com.xianpeng.govass.fragment.working

class GlobalSearchBean(
    var list: List<GlobalSearchDta>? = null
) {
    data class GlobalSearchDta(
        var id: String? = null,
        var primaryId: Int = -1,
        var title: String? = null,
        var content: String? = null,
        var type: Int = -1
    )
}