package com.xianpeng.govass.fragment.mailist

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.xianpeng.govass.Constants.Companion.FILE_SERVER
import com.xianpeng.govass.R
import com.xianpeng.govass.ext.glidePicToCircleImg
import com.xianpeng.govass.ext.visible
import de.hdodenhof.circleimageview.CircleImageView

class ExpandableAdapter(
    context: Context,
    groupData: List<GroupRes.GroupDataList.GroupData>?,
    childData: List<List<ChildRes.UserInfo>>?
) : BaseExpandableListAdapter() {
    private var mContext: Context = context
    private var mGroupArray: List<GroupRes.GroupDataList.GroupData>? = groupData
    private var mChildArray: List<List<ChildRes.UserInfo>>? = childData
    private var onElementClickListener: OnElementClickListener? = null

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return mChildArray!![groupPosition][childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var convertView = convertView
        val groupHolder: ViewHolderGroup
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                R.layout.adapter_group_item, parent, false
            )
            groupHolder = ViewHolderGroup()
            groupHolder.tv_group_name =
                convertView!!.findViewById<View>(R.id.tv_group_name) as TextView?
            groupHolder.tv_manager = convertView.findViewById<View>(R.id.tv_msg) as TextView?
            groupHolder.img_indicator = convertView.findViewById<View>(R.id.iv_left) as ImageView?
            convertView.tag = groupHolder
        } else {
            groupHolder = convertView.tag as ViewHolderGroup
        }
        groupHolder.tv_group_name?.text = mGroupArray!![groupPosition].name
        groupHolder.tv_manager?.visible(groupPosition != 0)
        groupHolder.tv_manager?.setOnClickListener {
            if (onElementClickListener != null) {
                onElementClickListener!!.onElementClick(
                    mGroupArray!![groupPosition].id,
                    mGroupArray!![groupPosition].name!!,
                    R.id.tv_msg
                );
            }
        }
        groupHolder.img_indicator!!.setImageResource(if (isExpanded) R.drawable.down else R.drawable.right)
        return convertView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var convertView = convertView
        val itemHolder: ViewHolderItem
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                R.layout.adapter_child_item, parent, false
            )
            itemHolder = ViewHolderItem()
            itemHolder.img_head = convertView!!.findViewById<View>(R.id.iv_head) as CircleImageView?
            itemHolder.tv_username = convertView.findViewById<View>(R.id.tv_name) as TextView?
            itemHolder.tv_delete = convertView.findViewById<View>(R.id.tv_delete) as TextView?
            convertView.tag = itemHolder
        } else {
            itemHolder = convertView.tag as ViewHolderItem
        }

        glidePicToCircleImg(mChildArray!![groupPosition][childPosition].headUrl, itemHolder.img_head!!)

        val userName = mChildArray!![groupPosition][childPosition].realname
        val enterpriseName = mChildArray!![groupPosition][childPosition].enterpriseName
        val realName = if (TextUtils.isEmpty(enterpriseName)) userName else "$userName-$enterpriseName"
        itemHolder.tv_username!!.text = realName
        itemHolder.tv_delete!!.setOnClickListener {
            if (onElementClickListener != null) {
                onElementClickListener!!.onElementClick(
                    mChildArray!![groupPosition][childPosition].userId,
                    realName,
                    R.id.tv_delete
                );
            }
        }
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return mChildArray!![groupPosition].size
    }

    override fun getGroup(groupPosition: Int): Any {
        return mGroupArray!![groupPosition]
    }

    override fun getGroupCount(): Int {
        return mGroupArray!!.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    private class ViewHolderGroup {
        var tv_group_name: TextView? = null
        var tv_manager: TextView? = null
        var img_indicator: ImageView? = null
    }

    private class ViewHolderItem {
        var img_head: CircleImageView? = null
        var tv_username: TextView? = null
        var tv_delete: TextView? = null
    }

    fun setOnElementClickListener(onElementClickListener: OnElementClickListener) {
        this.onElementClickListener = onElementClickListener
    }
}

interface OnElementClickListener {
    fun onElementClick(id: Int, name: String, elementId: Int)
}
