package com.xianpeng.govass.activity.richtext

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xianpeng.govass.R

/**
 * 展示富文本
 * 把str传进来即可
 */
class RichTextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rich_text)
    }
}