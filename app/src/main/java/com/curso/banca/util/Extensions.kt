package com.curso.banca.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toCurrencyMXN(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
    return format.format(this / 100.0)
}

fun String.toCentsOrNull(): Long? {
    val clean = replace("$", "")
        .replace(",", "")
        .replace("MXN", "")
        .trim()
    val amount = clean.toDoubleOrNull() ?: return null
    return (amount * 100).toLong()
}

fun Long.toDisplayDate(): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX"))
    return format.format(Date(this))
}

fun Long.toLongDate(): String {
    val format = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "MX"))
    return format.format(Date(this))
}

fun String.maskAccount(): String = if (length >= 4) "****${takeLast(4)}" else this
