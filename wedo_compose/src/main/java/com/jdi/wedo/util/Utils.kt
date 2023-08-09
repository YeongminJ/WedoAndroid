package com.jdi.wedo.util

import kotlin.random.Random

object Utils {
    fun generateRandomAlphanumericString(length: Int): String {
        val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = Random.Default
        val sb = StringBuilder(length)
        for (i in 0 until length) {
            sb.append(characters[random.nextInt(characters.length)])
        }
        return sb.toString()
    }
}