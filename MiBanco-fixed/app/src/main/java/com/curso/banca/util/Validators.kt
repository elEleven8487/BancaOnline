package com.curso.banca.util

import android.util.Patterns
import java.util.Calendar

object Validators {
    fun isValidEmail(value: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches()

    fun isValidPassword(value: String): Boolean = value.length >= 8

    fun isValidPhone(value: String): Boolean =
        value.filter { it.isDigit() }.length >= 10

    fun isAdult(birthMillis: Long): Boolean {
        val birth = Calendar.getInstance().apply { timeInMillis = birthMillis }
        val limit = Calendar.getInstance().apply { add(Calendar.YEAR, -18) }
        return !birth.after(limit)
    }
}
