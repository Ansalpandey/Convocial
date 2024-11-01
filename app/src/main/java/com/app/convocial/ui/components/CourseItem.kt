package com.app.convocial.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.convocial.data.model.Course

@Composable
fun CourseItem(modifier: Modifier = Modifier, course: Course?) {
  Column(modifier = Modifier
    .fillMaxWidth()
    .height(350.dp)
    .padding(5.dp)) {
    Card(modifier = Modifier
      .height(350.dp)) {
      AsyncImage(
        model = course?.thumbnail,
        contentDescription = "course thumbnail",
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp),
        contentScale = ContentScale.FillBounds,
      )
      Column(modifier = Modifier.padding(16.dp)) {
        Text(text = course?.name!!, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(text = "Rs. ${course.fee}", fontSize = 16.sp, fontWeight = FontWeight.Light)

        Text(text = "Instructor: ${course.madeBy?.name}")
        Text(text = "Rating: ${course.rating}")
      }
    }
  }
}
