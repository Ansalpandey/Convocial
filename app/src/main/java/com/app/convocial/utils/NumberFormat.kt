package com.app.convocial.utils

import java.util.Locale
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

fun formatNumber(number: Long): String {
  if (number < 1000) return number.toString()
  val suffixes = arrayOf("", "K", "M", "B") // Suffixes for 1K, 1M, 1B, etc.
  val exp = floor(log10(number.toDouble())).toInt() / 3
  val formattedNumber = number / 10.0.pow((exp * 3).toDouble())

  return if (formattedNumber < 10) {
    // For numbers like 1.2K, 3.5M, etc.
    String.format(Locale.ROOT, "%.1f%s", formattedNumber, suffixes[exp])
  } else {
    // For numbers like 12K, 10M, 100B, etc.
    String.format(Locale.ROOT, "%.0f%s", formattedNumber, suffixes[exp])
  }
}
