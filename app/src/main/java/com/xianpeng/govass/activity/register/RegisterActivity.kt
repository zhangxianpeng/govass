package com.xianpeng.govass.activity.register

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import com.xianpeng.govass.R
import com.xianpeng.govass.activity.login.LoginActivity
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.ext.checkBlank
import com.xianpeng.govass.ext.toastError
import kotlinx.android.synthetic.main.activity_register.*
import org.angmarch.views.NiceSpinner
import org.angmarch.views.OnSpinnerItemSelectedListener
import java.util.*

class RegisterActivity : BaseActivity<RegisterViewModel>() {
    private var registerUserType = 0; //注册的用户类型

    override fun layoutId(): Int = R.layout.activity_register

    override fun initView(savedInstanceState: Bundle?) {
        //注册
        registerBtn.setOnClickListener {
            val userName = userNameEt.checkBlank("用户名不能为空") ?: return@setOnClickListener
            val realName = realName.checkBlank("姓名不能为空") ?: return@setOnClickListener
            val phone = phoneEt.checkBlank("手机号不能为空") ?: return@setOnClickListener
            val email = emailEt.checkBlank("邮箱不能为空") ?: return@setOnClickListener
            val idcard = idcardEt.checkBlank("身份证不能为空") ?: return@setOnClickListener
            val pwd = pwdEt.checkBlank("密码不能为空") ?: return@setOnClickListener
            val repeatPwd = repeatpwdEt.checkBlank("确认密码不能为空") ?: return@setOnClickListener
            if(!pwd.equals(repeatPwd)){
                toastError("密码和确认密码必须一致")
                return@setOnClickListener
            }
            mViewModel.register(email, registerUserType, idcard, phone, pwd, realName, userName)
        }
        // 点击去注册
        toLoginTv.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        // 密码显示
        checkBox1.setOnCheckedChangeListener { _, isChecked ->
            pwdEt.transformationMethod = if (isChecked) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            pwdEt.setSelection(pwdEt.text.length)
        }
        checkBox2.setOnCheckedChangeListener { _, isChecked ->
            repeatpwdEt.transformationMethod = if (isChecked) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            repeatpwdEt.setSelection(repeatpwdEt.text.length)
        }
        val typeList: List<String> = LinkedList(listOf("企业法人", "其他员工"))
        stypeSpinner.attachDataSource(typeList)
        stypeSpinner.onSpinnerItemSelectedListener = object : OnSpinnerItemSelectedListener {
            override fun onItemSelected(
                parent: NiceSpinner?,
                view: View?,
                position: Int,
                id: Long
            ) {
                registerUserType = position
                Log.i("RegisterActivity","spinner result" + typeList[position])
            }
        }
    }
}