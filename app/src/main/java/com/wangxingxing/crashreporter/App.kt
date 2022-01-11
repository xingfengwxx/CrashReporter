package com.wangxingxing.crashreporter

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.util.Log
import kotlin.system.exitProcess

/**
 * author : 王星星
 * date : 2021/11/19 16:59
 * email : 1099420259@qq.com
 * description :
 */
class App : Application() {

    private val TAG = "App"

    override fun onCreate() {
        super.onCreate()

//        CrashReport.init(applicationContext)

        openCrashProtected()
    }

    private fun openCrashProtected() {
        Log.d(TAG, "openCrashProtected")
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                    Log.d(TAG, "main looper execute loop")
                } catch (e: Throwable) {
                    Log.e(TAG, "catch exception: " + e.toString())
                    //主线程出现异常，关闭栈顶activity
//                    ActivityStack.Instance().curr()?.finish()
                }
            }
        }

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            //捕获到异常，只打印日志，不杀进程
            Log.e(TAG, "${Thread.currentThread().name} 捕获到异常：$e")

            //dump各个线程
//            dumpAllThreadsInfo()

            //执行完方法后，线程无法继续存活。
            //如果是子线程，app不会crash。如果是主线程，就退出进程。
            if (Looper.getMainLooper() == Looper.myLooper()) {
                Process.killProcess(Process.myPid())
                exitProcess(10)
            }
        }
    }
}