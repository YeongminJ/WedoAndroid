package com.hostd.wedo.util

import android.util.Log

object Log {
    val DEFAULT_TAG = "JDI"

    fun v(tag: String, message: String) {
        Log.v(tag, message)
    }
    fun v(message: String) {
        Log.v(DEFAULT_TAG, message)
    }
    fun d(tag: String, message: String) {
        Log.d(tag, message)
    }
    fun d(message: String) {
        Log.d(DEFAULT_TAG, message)
    }
    fun i(tag: String, message: String) {
        Log.i(tag, message)
    }
    fun i(message: String) {
        Log.i(DEFAULT_TAG, message)
    }
    fun w(tag: String, message: String) {
        Log.w(tag, message)
    }
    fun w(message: String) {
        Log.w(DEFAULT_TAG, message)
    }
    fun e(tag: String, message: String) {
        Log.e(tag, message)
    }
    fun e(message: String) {
        Log.e(DEFAULT_TAG, message)
    }
}