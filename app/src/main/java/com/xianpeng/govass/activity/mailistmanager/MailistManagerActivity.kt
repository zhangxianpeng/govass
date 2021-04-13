package com.xianpeng.govass.activity.mailistmanager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.App
import com.xianpeng.govass.Constants
import com.xianpeng.govass.R
import com.xianpeng.govass.adapter.GroupManagerAdapter
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.ext.chooseFile
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.fragment.mailist.ChildRes
import com.xianpeng.govass.fragment.mailist.GroupRes
import com.xianpeng.govass.fragment.mailist.MailistViewModel
import com.xianpeng.govass.fragment.mailist.NewPlainMsgReqVo
import com.xuexiang.xui.widget.button.ButtonView
import com.xuexiang.xui.widget.edittext.ClearEditText
import com.zlylib.fileselectorlib.utils.Const
import kotlinx.android.synthetic.main.activity_mailist_manager.*
import kotlinx.android.synthetic.main.activity_mailist_manager.recycleview

@Suppress("DEPRECATION")
class MailistManagerActivity : BaseActivity<MailistViewModel>(), PopupWindow.OnDismissListener {
    //用户数据
    private var memberAdapter: GroupManagerAdapter? = null
    private var mData: MutableList<MemberItemBean> = ArrayList()
    private var isGetGoverMentUser = false
    override fun layoutId(): Int = R.layout.activity_mailist_manager

    //选中
    private var selectIdList: MutableList<Int> = ArrayList()
    private var sendMsgPop: PopupWindow? = null

    //弹框附件
    private var attachmentList: MutableList<String> = ArrayList()
    private var attachmentAdapter: BaseQuickAdapter<String, BaseViewHolder>? = null

    override fun initView(savedInstanceState: Bundle?) {
        initAdapter()
        titlebar.setLeftClickListener { finish() }
        val flagType = intent.getStringExtra("flagType")
        val itemPosition = intent.getIntExtra("itemPosition", -1)
        isGetGoverMentUser = intent.getBooleanExtra("isGetGoverMentUser", false)
        if (flagType == "plainMsg") {
            when (itemPosition) {
                1 -> {
                    titlebar.setTitle("选择分组发送消息")
                    getGroupList()
                    selectIdList.clear()
                    if (memberAdapter != null) {
                        memberAdapter!!.setOnItemChenedListener(object :
                            GroupManagerAdapter.OnItemCheckedListener {
                            override fun onItemNoChecked(view: View?, position: Int) {
                                if (selectIdList.size > 0) selectIdList.remove(mData[position].id)
                            }

                            override fun onItemChecked(view: View?, position: Int) {
                                selectIdList.add(mData[position].id)
                            }
                        })
                    }
                    btn_sure.setOnClickListener {
                        if (selectIdList.size > 0) {
                            Log.e("zhangxianpeng", selectIdList.toString())
                            showSendMsgDialog(selectIdList, 1)
                        }
                    }
                }
                2 -> {
                    titlebar.setTitle("选择成员发送消息")
                    if (isGetGoverMentUser) getAllGoverMentUser() else getAllEnterpriseUser()
                    selectIdList.clear()
                    if (memberAdapter != null) {
                        memberAdapter!!.setOnItemChenedListener(object :
                            GroupManagerAdapter.OnItemCheckedListener {
                            override fun onItemNoChecked(view: View?, position: Int) {
                                if (selectIdList.size > 0) selectIdList.remove(mData[position].id)
                            }

                            override fun onItemChecked(view: View?, position: Int) {
                                selectIdList.add(mData[position].id)
                            }
                        })
                    }
                    btn_sure.setOnClickListener {
                        if (selectIdList.size > 0) {
                            Log.e("zhangxianpeng", selectIdList.toString())
                            showSendMsgDialog(selectIdList, 2)
                        }
                    }
                }
            }
        } else {
            when (itemPosition) {
                0 -> {
                    titlebar.setTitle("从当前分组移除成员")
                }
                1 -> {
                    titlebar.setTitle("添加成员到当前分组")
                }
            }
        }
    }

    private fun initAdapter() {
        memberAdapter = GroupManagerAdapter(this, mData)
        recycleview.layoutManager = LinearLayoutManager(this)
        recycleview.adapter = memberAdapter
    }

    private fun transferGroupData(data: ArrayList<GroupRes.GroupDataList.GroupData>?): Collection<MemberItemBean> {
        val result: MutableList<MemberItemBean> = ArrayList()
        for (i in 0 until data!!.size) {
            val item = MemberItemBean()
            item.name = data[i].name
            item.id = data[i].id
            result.add(item)
        }
        return result
    }

    private fun transferMemberData(data: List<ChildRes.UserInfo>): Collection<MemberItemBean> {
        val result: MutableList<MemberItemBean> = ArrayList()
        for (i in data.indices) {
            val item = MemberItemBean()
            item.name = if (!TextUtils.isEmpty(data[i].enterpriseName)) data[i].realname + "-" + data[i].enterpriseName else data[i].realname
            item.id = data[i].id
            result.add(item)
        }
        return result
    }

    @SuppressLint("InflateParams")
    private fun showSendMsgDialog(idList: MutableList<Int>, receiveType: Int) {
        val contentView: View = LayoutInflater.from(this).inflate(R.layout.layout_bottom_dialog, null)
        initsendMsgPopView(contentView, receiveType, idList)
        val height = resources.getDimension(R.dimen.dp350).toInt()
        sendMsgPop = PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, height, true)
        sendMsgPop!!.animationStyle = R.style.ActionSheetDialogAnimation
        sendMsgPop!!.isOutsideTouchable = true
        backgroundAlpha(0.3f)
        sendMsgPop!!.setOnDismissListener(this)
        sendMsgPop!!.setBackgroundDrawable(BitmapDrawable())
        sendMsgPop!!.showAtLocation(rl_root, Gravity.BOTTOM, 0, 0)
    }

    private fun initsendMsgPopView(contentView: View, receiveType: Int, idList: MutableList<Int>) {
        contentView.findViewById<ImageView>(R.id.iv_close).setOnClickListener { sendMsgPop!!.dismiss() }
        contentView.findViewById<LinearLayout>(R.id.rl_add_attachment).setOnClickListener { chooseFile(this) }
        val attachment = contentView.findViewById<RecyclerView>(R.id.rv_attachment)
        initAttachmentAdapter(attachment)
        contentView.findViewById<ButtonView>(R.id.sendBtn).setOnClickListener {
            val msgReqVo = NewPlainMsgReqVo()
            msgReqVo.title = contentView.findViewById<ClearEditText>(R.id.et_title).text.toString()
            msgReqVo.content = contentView.findViewById<ClearEditText>(R.id.et_text).text.toString()
            msgReqVo.receiverType = receiveType
            if (receiveType == 1) msgReqVo.groupIdList = idList else msgReqVo.userIdList = idList
            if(attachmentList.isNotEmpty()) {
                showLoading("文件上传中...")
                mViewModel.uploadMultyFileAndSendMsg(attachmentList,msgReqVo)
            } else {
                showLoading("消息上传中...")
                mViewModel.sendMsg(msgReqVo)
            }
        }
    }

    private fun initAttachmentAdapter(recycleview:RecyclerView) {
        attachmentAdapter = object :
            BaseQuickAdapter<String, BaseViewHolder>(R.layout.adapter_attachment_item, attachmentList) {
            override fun convert(holder: BaseViewHolder, item: String) {
                holder.setText(R.id.tv_name, item)
            }
        }
        recycleview.layoutManager = LinearLayoutManager(App.instance)
        recycleview.adapter = attachmentAdapter
    }

    private fun backgroundAlpha(bgAlpha: Float) {
        val lp = this.window.attributes
        lp.alpha = bgAlpha
        this.window.attributes = lp
    }

    override fun onDismiss() {
        backgroundAlpha(1.0f)
        if (sendMsgPop != null) {
            sendMsgPop!!.dismiss()
        }
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

    /**
     * 获取分组列表
     */
    private fun getGroupList() {
        AndroidNetworking.get(Constants.GET_ALL_GROUP)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addQueryParameter("type", if (isGetGoverMentUser) "0" else "1")
            .build().getAsObject(GroupRes::class.java, object :
                ParsedRequestListener<GroupRes> {
                override fun onResponse(response: GroupRes?) {
                    dismissLoading()
                    if (response == null) {
                        toastError("获取分组列表失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg!!)
                        return
                    }
                    mData.clear()
                    mData.addAll(transferGroupData(response.data!!.list))
                    if (memberAdapter != null) memberAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError(anError!!.errorDetail)
                }
            })
    }

    /**
     * 获取全部政府人员
     */
    private fun getAllGoverMentUser() {
        AndroidNetworking.get(Constants.GET_ALL_GOVERMENT_USER)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(ChildRes::class.java, object :
                ParsedRequestListener<ChildRes> {
                override fun onResponse(response: ChildRes?) {
                    if (response == null) {
                        toastError("获取通讯录列表失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    mData.clear()
                    mData.addAll(transferMemberData(response.data!!))
                    if (memberAdapter != null) memberAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    /**
     * 获取全部企业用户
     */
    private fun getAllEnterpriseUser() {
        AndroidNetworking.get(Constants.GET_ALL_ENTERPRISE_USER)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(ChildRes::class.java, object :
                ParsedRequestListener<ChildRes> {
                override fun onResponse(response: ChildRes?) {
                    dismissLoading()
                    if (response == null) {
                        toastError("获取通讯录列表失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }

                    if (memberAdapter != null) memberAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError(anError!!.errorDetail)
                }
            })
    }

    /**
     * 根据分组id获取分组成员
     */
    private fun getGoverMentUserByGroupId(groupId: Int, groupPosition: Int) {
        AndroidNetworking.get(Constants.GET_ALL_GOVERMENT_USER)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addQueryParameter("groupId", groupId.toString())
            .build().getAsObject(ChildRes::class.java, object :
                ParsedRequestListener<ChildRes> {
                override fun onResponse(response: ChildRes?) {
                    dismissLoading()
                    if (response == null) {
                        toastError("获取通讯录列表失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }

                    if (memberAdapter != null) memberAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError(anError!!.errorDetail)
                }
            })
    }

    private fun getEnterpriseUserByGroupId(groupId: Int, groupPosition: Int) {
        AndroidNetworking.get(Constants.GET_ALL_ENTERPRISE_USER)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addQueryParameter("groupId", groupId.toString())
            .build().getAsObject(ChildRes::class.java, object :
                ParsedRequestListener<ChildRes> {
                override fun onResponse(response: ChildRes?) {
                    dismissLoading()
                    if (response == null) {
                        toastError("获取通讯录列表失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    if (memberAdapter != null) memberAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError(anError!!.errorDetail)
                }
            })
    }
}