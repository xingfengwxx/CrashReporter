package com.wangxingxing.crashreporter

import android.content.Context
import java.io.File

/**
 * author : 王星星
 * date : 2021/11/19 16:57
 * email : 1099420259@qq.com
 * description :
 */
object CrashReport {

    fun init(context: Context) {
        JavaCrashHandler.init(context)

        val file = File(context.filesDir, "native_crash")
        if (!file.exists()) {
            file.mkdirs()
        }
        initNativeCrash(file.absolutePath)
    }

    private external fun initNativeCrash(path: String)

    external fun testNativeCrash()

    fun testJavaCrash() {
        val i = 1 / 0
    }

    init {
        System.loadLibrary("crashreporter")
    }
}