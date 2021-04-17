package com.xianpeng.govass.activity.projectdeclare

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.xianpeng.govass.App
import com.xianpeng.govass.R
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.bean.MSGTYPE
import com.xianpeng.govass.bean.Msg
import com.xianpeng.govass.ext.checkBlank
import com.xianpeng.govass.ext.chooseFile
import com.xianpeng.govass.fragment.policy.PolicyItem
import com.xianpeng.govass.fragment.working.GlobalSearchBean
import com.xuexiang.xui.widget.actionbar.TitleBar
import com.zlylib.fileselectorlib.FileSelector
import com.zlylib.fileselectorlib.utils.Const
import kotlinx.android.synthetic.main.activity_project_declare.*
import kotlinx.android.synthetic.main.layout_attachment.*
import kotlinx.android.synthetic.main.layout_refresh_recycleview.*
import kotlinx.android.synthetic.main.titlebar_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ProjectDeclareActivity : BaseActivity<ProjectDeclareViewModel>(),TextWatcher, AdapterView.OnItemClickListener {
    private var attachmentList: MutableList<String> = ArrayList()
    private var attachmentAdapter: BaseQuickAdapter<String, BaseViewHolder>? = null
    private var searchPolicyList: MutableList<PolicyItem> = ArrayList()
    private var editSpinnerList : List<String>? = null

    //选中的政策的id
    private var selectedPolicyId:Int = -1
    override fun layoutId(): Int = R.layout.activity_project_declare

    override fun initView(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        titlebar.setTitle("新建申报")
        et_policy_name.addTextChangedListener(this)
        et_policy_name.setOnItemClickListener(this)
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
        ll_choose_attachment.setOnClickListener { chooseFile(this) }
        initAttachmentAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun initAttachmentAdapter() {
        attachmentAdapter = object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.adapter_attachment_item, attachmentList) {
            override fun convert(holder: BaseViewHolder, item: String) {
                holder.setText(R.id.tv_name, item)
            }
        }
        recycleview!!.layoutManager = LinearLayoutManager(App.instance)
        recycleview!!.adapter = attachmentAdapter
    }

    private fun doProjectDeclare() {
//        val policyName = et_policy_name.checkBlank("政策名称不能为空") ?: return
        val projectName = et_prorject_name.checkBlank("项目名称不能为空") ?: return
        val projectAmmount = et_lastyear_ammount.checkBlank("上年产值不能为空") ?: return
        val projectPayAmmount = et_lastyear_pay_ammount.checkBlank("上年纳税额不能为空") ?: return
        val projectAddress = et_prorject_address.checkBlank("项目地址不能为空") ?: return
        val projectContract = et_prorject_contract.checkBlank("联系人不能为空") ?: return
        val projectPhone = et_prorject_phone.checkBlank("联系方式不能为空") ?: return
        val newProjectDeclareReqVo = NewProjectDeclareReqVo()
        newProjectDeclareReqVo.policyId = selectedPolicyId
        newProjectDeclareReqVo.address = projectAddress
        newProjectDeclareReqVo.contact = projectPhone
        newProjectDeclareReqVo.linkman = projectContract
        newProjectDeclareReqVo.taxOfLastYear = projectPayAmmount
        newProjectDeclareReqVo.outputOfLastYear = projectAmmount
        newProjectDeclareReqVo.name = projectName
        mViewModel.saveProjectDeclare(newProjectDeclareReqVo, attachmentList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 1001) {
                val essFileList = data.getStringArrayListExtra(Const.EXTRA_RESULT_SELECTION) ?: return
                attachmentList.clear()
                attachmentList.addAll(essFileList)
                if (attachmentAdapter != null) attachmentAdapter!!.notifyDataSetChanged()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: Msg?) {
        dismissLoading()
        if (event?.msg == MSGTYPE.GET_POLICY_BY_SEARCH_SUCCESS.name) {
            searchPolicyList = event.searchPolicyDta!!.toMutableList()
            editSpinnerList = transforData(event.searchPolicyDta!!)
            et_policy_name.setItems(editSpinnerList)
        }else if(event?.msg == MSGTYPE.POST_PROJECT_DECLARE_SUCCESS.name) {
            finish()
        }
    }

    private fun transforData(originData: List<PolicyItem>):List<String> {
        var resultData : MutableList<String> =  ArrayList()
        for(i in originData.indices) {
            resultData.add(originData[i].title)
        }
        return resultData
    }

    override fun afterTextChanged(p0: Editable?) {
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        showLoading()
        mViewModel.getPolicy(p0.toString().trim())
    }

    /**
     * editSpinner点击事件
     */
    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if(editSpinnerList!=null) {
            val selectedStr = editSpinnerList!![p2]
            for(i in 0 until searchPolicyList.size) {
                if(selectedStr == searchPolicyList[i].title) {
                    selectedPolicyId = searchPolicyList[i].id
                }
            }
        }
    }
}