package com.app.convocial.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.convocial.data.model.NotificationResponse
import com.app.convocial.utils.getRelativeTimeSpanString
import kotlinx.coroutines.delay

@Composable
fun NotificationItem(modifier: Modifier = Modifier, notificationResponse: NotificationResponse) {
  val timeAgo = remember {
    mutableStateOf(getRelativeTimeSpanString(notificationResponse.createdAt!!))
  }
  LaunchedEffect(notificationResponse.createdAt) {
    while (true) {
      timeAgo.value = getRelativeTimeSpanString(notificationResponse.createdAt!!)
      delay(60000) // Update every minute
    }
  }
  Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {

        AsyncImage(
          model = notificationResponse.details?.post?.profileImage,
          contentDescription = "profileImage",
          contentScale = ContentScale.Crop,
          modifier = Modifier.clip(CircleShape).size(34.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          notificationResponse.details?.post?.username!!,
          fontWeight = FontWeight.Bold,
          fontSize = MaterialTheme.typography.titleMedium.fontSize,
        )
        Text(text = notificationResponse.message!! + ".")
        Text(
          text = " ${timeAgo.value}",
          fontSize = MaterialTheme.typography.bodySmall.fontSize,
          fontWeight = FontWeight.Light,
          textAlign = TextAlign.End,
          modifier = Modifier.fillMaxWidth(),
        )
      }
    }
  }
}
