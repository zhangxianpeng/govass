package com.xianpeng.govass.bean

import java.io.Serializable

class Attachment : Serializable {
    var fileName: String? = null
    var filePath: String? = null
    var name: String? = null
    var url: String? = null
    var id = 0
    var policyId = 0
    var projectId = 0
}