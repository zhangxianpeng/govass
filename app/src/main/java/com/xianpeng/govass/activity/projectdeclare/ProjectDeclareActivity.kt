package com.xianpeng.govass.activity.projectdeclare

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xianpeng.govass.App
import com.xianpeng.govass.R
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.ext.checkBlank
import com.xuexiang.xui.widget.actionbar.TitleBar
import com.zlylib.fileselectorlib.FileSelector
import com.zlylib.fileselectorlib.utils.Const
import kotlinx.android.synthetic.main.activity_project_declare.*
import kotlinx.android.synthetic.main.layout_attachment.*
import kotlinx.android.synthetic.main.layout_refresh_recycleview.*
import kotlinx.android.synthetic.main.titlebar_layout.*

class ProjectDeclareActivity : BaseActivity<ProjectDeclareViewModel>() {
    private var attachmentList: MutableList<String> = ArrayList()
    private var attachmentAdapter: BaseQuickAdapter<String, BaseViewHolder>? = null

    override fun layoutId(): Int = R.layout.activity_project_declare

    override fun initView(savedInstanceState: Bundle?) {
        titlebar.setTitle("新建申报")
        titlebar.setLeftClickListener { finish() }
        titlebar.addAction(object : TitleBar.Action {
            override fun leftPadding(): Int = 0
            override fun performAction(view: View?) {
                doProjectDeclare()
            }

            override fun rightPadding(): Int = 0
            override fun getText(): String = ""
            override fun getDrawable(): Int = R.drawable.ic_baseline_send_24
        })
        ll_choose_attachment.setOnClickListener { chooseFile() }
        initAttachmentAdapter()
    }

    private fun chooseFile() {
        FileSelector.from(this)
            .setMaxCount(5) //设置最大选择数
            .setFileTypes(
                "png",
                "doc",
                "docx",
                "xls",
                "apk",
                "mp3",
                "gif",
                "txt",
                "mp4",
                "zip",
                "pdf"
            ) //设置文件类型
            .setSortType(FileSelector.BY_NAME_ASC) //设置名字排序
            .requestCode(1001) //设置返回码
            .start()
    }

    private fun initAttachmentAdapter() {
        attachmentAdapter = object :
            BaseQuickAdapter<String, BaseViewHolder>(
                R.layout.adapter_attachment_item,
                attachmentList
            ) {
            override fun convert(holder: BaseViewHolder, item: String) {
                holder.setText(R.id.tv_name, item)
            }
        }
        recycleview!!.layoutManager = LinearLayoutManager(App.instance)
        recycleview!!.adapter = attachmentAdapter
    }

    private fun doProjectDeclare() {
        val policyName = et_policy_name.checkBlank("政策名称不能为空") ?: return
        val projectName = et_prorject_name.checkBlank("项目名称不能为空") ?: return
        val projectAmmount = et_lastyear_ammount.checkBlank("上年产值不能为空") ?: return
        val projectPayAmmount = et_lastyear_pay_ammount.checkBlank("上年纳税额不能为空") ?: return
        val projectAddress = et_prorject_address.checkBlank("项目地址不能为空") ?: return
        val projectContract = et_prorject_contract.checkBlank("联系人不能为空") ?: return
        val projectPhone = et_prorject_phone.checkBlank("联系方式不能为空") ?: return
        mViewModel.saveProjectDeclare(
            policyName,
            projectName,
            projectAmmount,
            projectPayAmmount,
            projectAddress,
            projectContract,
            projectPhone,
            attachmentList
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 1001) {
                val essFileList =
                    data.getStringArrayListExtra(Const.EXTRA_RESULT_SELECTION) ?: return
                attachmentList.clear()
                attachmentList.addAll(essFileList)
                if (attachmentAdapter != null) attachmentAdapter!!.notifyDataSetChanged()
            }
        }
    }
}