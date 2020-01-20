package com.atdev.kotlincorotinues

import android.util.Log

object Utils {

    fun LogThread(methodName: String) {

        Log.d("ThreadsT", "debug: ${methodName} + ${Thread.currentThread().name}")
        println("debugT: ${methodName} + ${Thread.currentThread().name}")
    }
}