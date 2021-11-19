package com.wangxingxing.crashreporter

import android.app.Application

/**
 * author : 王星星
 * date : 2021/11/19 16:59
 * email : 1099420259@qq.com
 * description :
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        CrashReport.init(applicationContext)
    }
}