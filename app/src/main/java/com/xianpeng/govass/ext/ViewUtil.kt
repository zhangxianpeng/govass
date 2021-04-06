package com.xianpeng.govass.ext

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Outline
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.xianpeng.govass.Constants.Companion.FILE_SERVER
import com.xianpeng.govass.R
import de.hdodenhof.circleimageview.CircleImageView
import org.apache.commons.lang3.StringEscapeUtils

/**
 *
 * View 相关的工具类
 *
 * Author: nanchen
 * Email: liushilin.nanchen@bytedance.com
 * Date: 2019-10-18 11:47
 */
fun View?.hideSoftInput() {
    this?.apply {
        if (windowToken != null) {
            (appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(
                windowToken,
                0
            )
        }
    }
}

fun View?.showSoftInput() {
    this?.postDelayed({
        requestFocus()
        if (windowToken != null) {
            (appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(
                this,
                0
            )
        }
    }, 100)
}

fun View?.visible(visible: Boolean) {
    this?.apply {
        visibility = if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}

fun View?.isVisible(): Boolean {
    return this?.run {
        visibility == View.VISIBLE
    } ?: false
}

@SuppressLint("ObsoleteSdkInt")
fun <T : View> T.whenWidthReady(
    block: T.(layoutChanged: Boolean) -> Unit,
    widthPx: Int = 0,
    heightPx: Int = 0
): T {
    if (widthPx > 0 && heightPx > 0) {
        block(false)
        return this
    }
    if (this.layoutParams.width > 0 && this.layoutParams.height > 0) {
        block(false)
        return this
    }
    if (width > 0 && height > 0) {
        block(false)
        return this
    }
    fun addLayoutListener() {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (width <= 0 || height <= 0) {
                    return
                }
                block(true)
            }
        })
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !isAttachedToWindow) {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(v: View?) {
            }

            override fun onViewAttachedToWindow(v: View?) {
                removeOnAttachStateChangeListener(this)
                if (width > 0 && height > 0) {
                    block(true)
                    return
                }
                addLayoutListener()
            }

        })
    } else {
        addLayoutListener()
    }
    return this
}

fun <T : View> T.addOnPreDrawListener(block: T.() -> Unit) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)
            block()
            return true
        }
    })
}

/**
 * 强制裁剪一个View为圆角
 */
fun View?.clipViewCornerRadius(radius: Float) {
    this?.run {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                if (view == null || outline == null) {
                    return
                }
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }

        }
        clipToOutline = true
    }
}

fun View.toBitmap(
    isDisplayed: Boolean = true, widthPx: Int = width, heightPx: Int = height,
    bitmapConfig: Bitmap.Config = Bitmap.Config.RGB_565
): Bitmap {
    if (!isDisplayed) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(heightPx, View.MeasureSpec.EXACTLY)
        measure(widthSpec, heightSpec)
        layout(0, 0, measuredWidth, measuredHeight)
    }
    val bitmap = Bitmap.createBitmap(width, height, bitmapConfig)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.WHITE)
    draw(canvas)
    return bitmap
}

/**
 * 判空
 */
fun TextView.checkBlank(message: String): String? {
    val text = this.text.toString()
    if (text.isBlank()) {
        toastError(message)
        return null
    }
    return text
}

fun <T> checkNull(t: T): Boolean? {
    return t == null
}

fun WebView.loadRichText(str: String) {
    loadDataWithBaseURL(
        null,
        StringEscapeUtils.unescapeHtml4(str),
        "text/html",
        "UTF-8",
        null
    )
}

fun glidePicToImg(str: String, iv: ImageView) {
    Glide.with(appContext).load(FILE_SERVER + str).placeholder(R.mipmap.ic_launcher)
        .error(R.mipmap.ic_launcher)
        .into(iv)
}

fun glidePicToCircleImg(str: String, circleImageView: CircleImageView) {
    Glide.with(appContext).load(FILE_SERVER + str).placeholder(R.drawable.default_tx_img)
        .error(R.drawable.default_tx_img)
        .into(circleImageView)
}