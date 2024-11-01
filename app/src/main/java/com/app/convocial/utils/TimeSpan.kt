package com.app.convocial.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

fun getRelativeTimeSpanString(dateString: String): String {
  val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
  sdf.timeZone = TimeZone.getTimeZone("UTC") // Set the timezone to UTC
  val date = sdf.parse(dateString)
  date?.let {
    // Convert UTC time to local time
    val localSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
    localSdf.timeZone = TimeZone.getDefault()
    val localDateString = localSdf.format(date)
    val localDate = localSdf.parse(localDateString)

    localDate?.let {
      val now = Date()
      val diff = now.time - localDate.time

      val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
      val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
      val hours = TimeUnit.MILLISECONDS.toHours(diff)
      val days = TimeUnit.MILLISECONDS.toDays(diff)

      return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "$minutes Min${if (minutes > 1) "s" else ""} ago"
        hours < 24 -> "$hours Hr${if (hours > 1) "s" else ""} ago"
        days < 7 -> "$days Day${if (days > 1) "s" else ""} ago"
        else -> {
          val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
          sdf.format(localDate)
        }
      }
    }
  }
  return "Unknown time"
}
