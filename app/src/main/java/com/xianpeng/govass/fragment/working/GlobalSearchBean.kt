package com.xianpeng.govass.fragment.working

data class GlobalSearchBean(
    var code: Int = -1,
    var msg: String? = null,
    var data: GlobalSearchBeanDetail? = null
) {
    data class GlobalSearchBeanDetail(
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
}