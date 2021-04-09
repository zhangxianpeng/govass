package com.xianpeng.govass.fragment.mailist

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.tencent.mmkv.MMKV
import com.wyt.searchbox.SearchFragment
import com.wyt.searchbox.custom.IOnSearchClickListener
import com.xianpeng.govass.Constants
import com.xianpeng.govass.R
import com.xianpeng.govass.activity.mailistmanager.MailistManagerActivity
import com.xianpeng.govass.base.BaseFragment
import com.xianpeng.govass.ext.showMessage
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.visible
import com.xuexiang.xui.widget.button.ButtonView
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet.BottomListSheetBuilder
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.xuexiang.xui.widget.edittext.ClearEditText
import kotlinx.android.synthetic.main.fragment_mailist.*
import kotlinx.android.synthetic.main.tab_title_layout.*


/**
 * @description: 通讯录
 * @author: zhangxianpeng
 * @time:2021/04/08
 */
class MailistFragment : BaseFragment<MailistViewModel>(), PopupWindow.OnDismissListener,
    IOnSearchClickListener {
    private val searchFragment by lazy { SearchFragment.newInstance() }
    private var groupArray: MutableList<GroupRes.GroupDataList.GroupData> = ArrayList()
    private var childArray: MutableList<ArrayList<ChildRes.UserInfo>> = ArrayList()
    private var userAdapter: ExpandableAdapter? = null

    private var isGetGoverMentUser = true
    private var sendMsgPop: PopupWindow? = null
    override fun layoutId(): Int = R.layout.fragment_mailist

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initView(savedInstanceState: Bundle?) {
        leftTabTv.setText("政府用户")
        rightTabTv.setText("企业用户")
        historyIv.visible(true)
        historyIv.setImageDrawable(resources.getDrawable(R.drawable.ic_search_white_24dp))
        historyIv.setOnClickListener {
            searchFragment.showFragment(activity?.supportFragmentManager, SearchFragment.TAG)
        }
        searchFragment.setOnSearchClickListener(this)
        //政府用户点击
        ll_left.setOnClickListener {
            collapseGroup()
            isGetGoverMentUser = true
            title.text = "政府用户"
            showLoading()
            getGroupList()
            leftTabTv.setTypeface(null, Typeface.BOLD)
            left_indicator.visible(true)
            rightTabTv.setTypeface(null, Typeface.NORMAL)
            right_indicator.visible(false)
        }
        //企业用户点击
        ll_right.setOnClickListener {
            collapseGroup()
            isGetGoverMentUser = false
            title.text = "企业用户"
            showLoading()
            getGroupList()
            leftTabTv.setTypeface(null, Typeface.NORMAL)
            left_indicator.visible(false)
            rightTabTv.setTypeface(null, Typeface.BOLD)
            right_indicator.visible(true)
        }
        tv_send_msg.setOnClickListener {
            showSendMsgDialog(isGetGoverMentUser)
        }
        addIv.setOnClickListener {
            showCenterDialog(true,-1)
        }
        initUserAdapter()
        showLoading()
        getGroupList()
    }

    private fun collapseGroup() {
        if (userAdapter!!.groupCount != 0) {
            val count: Int = userAdapter!!.groupCount
            for (i in 0 until count) {
                user_listView.collapseGroup(i)
            }
        }
    }

    /**
     * 初始化用户列表适配器
     */
    private fun initUserAdapter() {
        userAdapter = ExpandableAdapter(requireActivity(), groupArray, childArray)
        user_listView.setAdapter(userAdapter)
        user_listView.setOnGroupClickListener { parent, view, groupPosition, id ->
            if (user_listView.isGroupExpanded(groupPosition)) {
                user_listView.collapseGroup(groupPosition)
            } else {
                user_listView.expandGroup(groupPosition, true)
                if (groupPosition == 0) {  //获取全部联系人
                    if (isGetGoverMentUser) getAllGoverMentUser() else getAllEnterpriseUser()
                } else {  //根据分组id获取联系人
                    if (isGetGoverMentUser) getGoverMentUserByGroupId(
                        groupArray[groupPosition].id,
                        groupPosition
                    ) else getEnterpriseUserByGroupId(
                        groupArray[groupPosition].id, groupPosition
                    )
                }
            }
            true
        }

        userAdapter!!.setOnElementClickListener(object : OnElementClickListener {
            override fun onElementClick(id: Int, name: String, elementId: Int) {
                when (elementId) {
                    R.id.tv_msg -> showGroupManageDialog(isGetGoverMentUser, name, id)
                    R.id.tv_delete -> {
                        showMessage("确定将此用户从当前分组中移除吗？", positiveAction = {
                            deleteUserFromCurrentGroup(id)
                        }, negativeButtonText = "取消")
                    }
                }
            }
        })

        user_listView.setOnChildClickListener { _, _, p2, p3, _ ->
            val contract = childArray[p2][p3].mobile
            val contractName = childArray[p2][p3].realname
            showMessage("拨打：$contractName-$contract", positiveAction = {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$contract"))
                startActivity(dialIntent)
            }, negativeButtonText = "取消")
            true
        }
    }

    private fun showCenterDialog(isInsert: Boolean,groupId: Int) {
        MaterialDialog.Builder(requireActivity())
            .customView(R.layout.dialog_custom, true)
            .title(if(isInsert)"新增分组" else "修改分组信息")
            .positiveText("确定")
            .onPositive { dialog, which ->
                if(isInsert) { //新增
                    var newGroup = AddGroupReqVo()
                    newGroup.type = if (isGetGoverMentUser) 0 else 1
                    newGroup.name = dialog.findViewById<ClearEditText>(R.id.group_name).text.toString()
                    newGroup.remark = dialog.findViewById<ClearEditText>(R.id.group_remark).text.toString()
                    showLoading()
                    mViewModel.addGroup(newGroup)
                    // TODO: 2021/4/10  不能保证数据刷新
                    getGroupList()
                } else { //修改
                    var updateGroup = AddGroupReqVo()
                    updateGroup.type = if (isGetGoverMentUser) 0 else 1
                    updateGroup.name = dialog.findViewById<ClearEditText>(R.id.group_name).text.toString()
                    updateGroup.remark = dialog.findViewById<ClearEditText>(R.id.group_remark).text.toString()
                    updateGroup.id = groupId
                    showLoading()
                    mViewModel.updateGroup(updateGroup)
                    getGroupList()
                }
            }
            .negativeText("取消")
            .show()
    }
    
    private fun showSendMsgDialog(isGetGoverMentUser: Boolean) {
        BottomListSheetBuilder(activity)
            .addItem("全员消息")
            .addItem("分组消息")
            .addItem("成员消息")
            .setIsCenter(true)
            .setOnSheetItemClickListener { dialog: BottomSheet, itemView: View?, position: Int, tag: String? ->
                dialog.dismiss()
                when (position) {
                    0 ->  showSendAllMsgPop()
                    1,2 -> startActivity(Intent(requireActivity(), MailistManagerActivity::class.java).putExtra("flagType", "plainMsg").putExtra("itemPosition", position).putExtra("isGetGoverMentUser", isGetGoverMentUser))
                }
            }.build().show()
    }
   
    private fun showGroupManageDialog(isGetGoverMentUser: Boolean,groupName: String, groupId: Int) {
            BottomListSheetBuilder(activity)
                .setTitle(groupName)
                .addItem("移除成员")
                .addItem("新增成员")
                .addItem("修改分组信息")
                .addItem("删除此分组")
                .setIsCenter(true)
                .setOnSheetItemClickListener { dialog: BottomSheet, itemView: View?, position: Int, tag: String? ->
                    dialog.dismiss()
                    when(position) {
                        0,1->startActivity(Intent(requireActivity(), MailistManagerActivity::class.java).putExtra("itemPosition", position).putExtra("isGetGoverMentUser", isGetGoverMentUser))
                        2-> showCenterDialog(false,groupId)
                        3-> showMessage("确定删除此分组吗", positiveAction = {
                            showLoading()
                            mViewModel.deleteGroup(groupId)
                            getGroupList()
                            }, negativeButtonText = "取消")

                    }
                }.build().show()
    }

    private fun showSendAllMsgPop() {
        val contentView: View =
            LayoutInflater.from(activity).inflate(R.layout.layout_bottom_dialog, null)
        initsendMsgPopView(contentView)
        val height = resources.getDimension(R.dimen.dp350).toInt()
        sendMsgPop = PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, height, true)
        sendMsgPop!!.animationStyle = R.style.ActionSheetDialogAnimation
        sendMsgPop!!.isOutsideTouchable = true
        backgroundAlpha(0.3f)
        sendMsgPop!!.setOnDismissListener(this)
        sendMsgPop!!.setBackgroundDrawable(BitmapDrawable())
        sendMsgPop!!.showAtLocation(rootView, Gravity.BOTTOM, 0, 0)
    }

    private fun initsendMsgPopView(contentView: View) {
        contentView.findViewById<ImageView>(R.id.iv_close)
            .setOnClickListener { sendMsgPop!!.dismiss() }
        contentView.findViewById<ButtonView>(R.id.sendBtn)
            .setOnClickListener {
                var allUserMsgReqVo = NewPlainMsgReqVo()
                allUserMsgReqVo.title = contentView.findViewById<ClearEditText>(R.id.et_title).text.toString()
                allUserMsgReqVo.content = contentView.findViewById<ClearEditText>(R.id.et_text).text.toString()
                allUserMsgReqVo.receiverType = 0
                allUserMsgReqVo.userType = if (isGetGoverMentUser) 0 else 1
                mViewModel.sendAllUserMsg(allUserMsgReqVo)
            }
    }

    private fun backgroundAlpha(bgAlpha: Float) {
        val lp = requireActivity().window.attributes
        lp.alpha = bgAlpha
        requireActivity().window.attributes = lp
    }

    override fun onDismiss() {
        backgroundAlpha(1.0f);
        if (sendMsgPop != null) {
            sendMsgPop!!.dismiss()
        }
    }

    //----------------接口 start ------------------//
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
                    groupArray.clear()
                    val titleGroup = GroupRes.GroupDataList.GroupData()
                    titleGroup.name = "全部联系人"
                    groupArray.add(titleGroup)
                    groupArray.addAll(response.data?.list!!)
                    for (i in groupArray.indices) {
                        val tempArray: ArrayList<ChildRes.UserInfo> = ArrayList()
                        childArray.add(tempArray)
                    }
                    if (userAdapter != null) userAdapter!!.notifyDataSetChanged()
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
                    val childModels = childArray[0]
                    childModels.clear()
                    childModels.addAll(response.data!!)
                    if (userAdapter != null) userAdapter!!.notifyDataSetChanged()
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
                    val childModels = childArray[0]
                    childModels.clear()
                    childModels.addAll(response.data!!)
                    if (userAdapter != null) userAdapter!!.notifyDataSetChanged()
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
                    val childModels = childArray[groupPosition]
                    childModels.clear()
                    childModels.addAll(response.data!!)
                    if (userAdapter != null) userAdapter!!.notifyDataSetChanged()
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
                    val childModels = childArray[groupPosition]
                    childModels.clear()
                    childModels.addAll(response.data!!)
                    if (userAdapter != null) userAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError(anError!!.errorDetail)
                }
            })
    }

    /**
     * 从分组中删除
     * @param id 分组id
     */
    private fun deleteUserFromCurrentGroup(groupId: Int) {

    }

    override fun OnSearchClick(keyword: String?) {
        // TODO: 2021/4/10 搜索用户
    }
    
}