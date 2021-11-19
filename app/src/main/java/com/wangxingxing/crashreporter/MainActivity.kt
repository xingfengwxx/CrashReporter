package com.wangxingxing.crashreporter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.wangxingxing.crashreporter.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            btnJava.setOnClickListener {
                CrashReport.testJavaCrash()
            }

            btnNative.setOnClickListener {
                CrashReport.testNativeCrash()
            }
        }
    }

}