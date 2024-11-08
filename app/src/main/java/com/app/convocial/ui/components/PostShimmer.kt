package com.app.convocial.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun PostShimmerItem(
  isLoading: Boolean,
  contentAfterLoading: @Composable () -> Unit,
  modifier: Modifier = Modifier,
) {
  if (isLoading) {
    Row(modifier = modifier.padding(16.dp)) {
      // Profile image shimmer
      Box(modifier = Modifier.size(50.dp).clip(CircleShape).shimmerEffect())

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier) {
        // Name and handle shimmer
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier =
              Modifier.fillMaxWidth(0.8f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
          )
          Spacer(modifier = Modifier.width(8.dp))
          Box(
            modifier =
              Modifier.fillMaxWidth(1f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
          modifier =
            Modifier.fillMaxWidth(0.5f).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Content shimmer
        Box(
          modifier =
            Modifier.fillMaxWidth()
              .height(140.dp)
              .clip(RoundedCornerShape(4.dp))
              .shimmerEffect()
        )
      }
    }
  } else {
    contentAfterLoading()
  }
}

fun Modifier.shimmerEffect(): Modifier = composed {
  var size by remember { mutableStateOf(IntSize.Zero) }
  val transition = rememberInfiniteTransition()
  val startOffsetX by
    transition.animateFloat(
      initialValue = -2 * size.width.toFloat(),
      targetValue = 2 * size.width.toFloat(),
      animationSpec = infiniteRepeatable(animation = tween(1000)),
    )

  background(
      brush =
        Brush.linearGradient(
          colors = listOf(Color(0xFF4F4E4E), Color(0xFF3B3838), Color(0xFF232323)),
          start = Offset(startOffsetX, 0f),
          end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat()),
        )
    )
    .onGloballyPositioned { size = it.size }
}
