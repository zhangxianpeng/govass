package com.xianpeng.govass.activity.common

import android.content.Context
import android.os.Looper
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.bumptech.glide.Glide
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.xianpeng.govass.Constants.Companion.FILE_SERVER
import com.xianpeng.govass.R
import com.xianpeng.govass.ext.visible
import com.xianpeng.govass.tencentx5.FileReaderView
import com.xianpeng.govass.util.CommonUtil
import com.xuexiang.xui.widget.imageview.photoview.PhotoView
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import java.io.File

class CommonViewModel : BaseViewModel() {
    private val TAG = "downloadViewModel"
    fun downLoadFileAndOpen(
        context: Context,
        pdfView: PDFView,
        wordView: FileReaderView,
        photoView: PhotoView,
        fileName: String,
        filePath: String
    ) {
        AndroidNetworking.download(filePath, CommonUtil.getRootDirPath(context), fileName)
            .setPriority(Priority.HIGH)
            .setTag(this)
            .build()
            .setAnalyticsListener { timeTakenInMillis, bytesSent, bytesReceived, isFromCache ->
                Log.d(
                    TAG,
                    " timeTakenInMillis : $timeTakenInMillis"
                )
                Log.d(
                    TAG,
                    " bytesSent : $bytesSent"
                )
                Log.d(
                    TAG,
                    " bytesReceived : $bytesReceived"
                )
                Log.d(
                    TAG,
                    " isFromCache : $isFromCache"
                )
            }
            .setDownloadProgressListener { bytesDownloaded, totalBytes ->
                Log.d(
                    TAG,
                    "bytesDownloaded : $bytesDownloaded totalBytes : $totalBytes"
                )
                Log.d(
                    TAG,
                    "setDownloadProgressListener isMainThread : " + (Looper.myLooper() == Looper.getMainLooper()).toString()
                )
            }
            .startDownload(object : DownloadListener {
                override fun onDownloadComplete() {
                    if (fileName.endsWith("PNG") ||
                        fileName.endsWith("JPG") ||
                        fileName.endsWith("JEPG") ||
                        fileName.endsWith("png") ||
                        fileName.endsWith("jpg") ||
                        fileName.endsWith("jepg")
                    ) {
                        pdfView.visible(false)
                        wordView.visible(false)
                        photoView.visible(true)
                        Glide.with(context).load(filePath)
                            .placeholder(R.drawable.default_img)
                            .error(R.drawable.default_img).into(photoView)
                    } else if (fileName.endsWith("pdf")) {
                        pdfView.visible(true)
                        wordView.visible(false)
                        photoView.visible(false)
                        val realFile = File(CommonUtil.getRootDirPath(context), fileName)
                        pdfView.fromFile(realFile)
                            .defaultPage(0)
                            .enableAnnotationRendering(true)
                            .scrollHandle(DefaultScrollHandle(context))
                            .spacing(10) // in dp
                            .load()
                    } else {
                        pdfView.visible(false)
                        wordView.visible(true)
                        photoView.visible(false)
                        wordView.show(CommonUtil.getRootDirPath(context) + "/" + fileName)
                    }
                }

                override fun onError(error: ANError) {
                    if (error.errorCode != 0) {
                        // received ANError from server
                        // error.getErrorCode() - the ANError code from server
                        // error.getErrorBody() - the ANError body from server
                        // error.getErrorDetail() - just an ANError detail
                        Log.d(
                            TAG,
                            "onError errorCode : " + error.errorCode
                        )
                        Log.d(
                            TAG,
                            "onError errorBody : " + error.errorBody
                        )
                        Log.d(
                            TAG,
                            "onError errorDetail : " + error.errorDetail
                        )
                    } else {
                        // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                        Log.d(
                            TAG,
                            "onError errorDetail : " + error.errorDetail
                        )
                    }
                }
            })
    }
}