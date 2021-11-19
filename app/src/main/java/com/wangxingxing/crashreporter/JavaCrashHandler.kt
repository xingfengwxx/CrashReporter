package com.wangxingxing.crashreporter

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : 王星星
 * date : 2021/11/19 16:42
 * email : 1099420259@qq.com
 * description :
 */
object JavaCrashHandler : Thread.UncaughtExceptionHandler {

    private lateinit var defaultUncaughtExceptionHandler: Thread.UncaughtExceptionHandler
    private lateinit var mContext: Context

    //private val i = 5 / 0

    /**
     * 发生 Java 层 Crash 会进入此方法
     */
    override fun uncaughtException(t: Thread, e: Throwable) {
        val dir = File(mContext.filesDir, "crash_info")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        // 将异常信息打印到日志文件
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
        val time = sdf.format(Date())
        val file = File(dir, "$time.log")
        try {
            val pw = PrintWriter(FileWriter(file))
            pw.println("thread: " + t.name)
            e.printStackTrace(pw)
            pw.flush()
            pw.close()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            defaultUncaughtExceptionHandler.uncaughtException(t, e)
        }
    }

    fun init(context: Context) {
        mContext = context
        // 原来的默认异常处理器还是保留下来
        Thread.getDefaultUncaughtExceptionHandler().also { defaultUncaughtExceptionHandler = it}
        // 设置当前线程的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }
}