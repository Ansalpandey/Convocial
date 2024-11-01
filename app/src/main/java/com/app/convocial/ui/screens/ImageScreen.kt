package com.app.convocial.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageScreen(images: List<String>, initialPage: Int = 0, onClose: () -> Unit) {
  val pagerState = rememberPagerState(initialPage)
  var scale by remember { mutableFloatStateOf(1f) }
  val scaleAnim = remember { Animatable(1f) }
  val coroutineScope = rememberCoroutineScope()

  // Update the scale animation based on the current scale
  LaunchedEffect(scale) { scaleAnim.snapTo(scale) }

  Box(modifier = Modifier.fillMaxSize()) {
    HorizontalPager(count = images.size, state = pagerState, modifier = Modifier.fillMaxSize()) {
      page ->
      // Box to apply the scale transformations
      Box(
        modifier =
          Modifier.fillMaxSize()
            .pointerInput(Unit) {
              detectTransformGestures { _, _, zoom, _ ->
                // Update scale while ensuring that it is within limits
                scale *= zoom
                scale = scale.coerceIn(1f, 5f)
              }
            }
            .graphicsLayer(scaleX = scaleAnim.value, scaleY = scaleAnim.value)
      ) {
        // Display the image
        AsyncImage(
          model = images[page],
          contentDescription = "image_screen_image",
          contentScale = ContentScale.Fit,
          modifier = Modifier.fillMaxSize(),
        )
      }

      // Reset scale when the page changes
      LaunchedEffect(pagerState.currentPage) {
        scale = 1f
        scaleAnim.snapTo(scale)
      }
    }

    // Navigation Buttons
    IconButton(
      onClick = {
        coroutineScope.launch {
          if (pagerState.currentPage > 0) {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
          }
        }
      },
      modifier =
        Modifier.align(Alignment.CenterStart)
          .padding(16.dp)
          .background(Color.Gray.copy(alpha = 0.7f), CircleShape),
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = "previous_image",
        tint = Color.White,
      )
    }

    IconButton(
      onClick = {
        coroutineScope.launch {
          if (pagerState.currentPage < images.size - 1) {
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
          }
        }
      },
      modifier =
        Modifier.align(Alignment.CenterEnd)
          .padding(16.dp)
          .background(Color.Gray.copy(alpha = 0.7f), CircleShape),
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        contentDescription = "next_image",
        tint = Color.White,
      )
    }
  }
  // Close button
  IconButton(
    onClick = onClose,
    modifier =
      Modifier.padding(start = 20.dp, top = 30.dp)
        .size(50.dp)
        .background(Color.Gray.copy(alpha = 0.7f), CircleShape),
  ) {
    Icon(
      imageVector = Icons.Default.Close,
      contentDescription = "close_button",
      tint = Color.White,
      modifier = Modifier.size(20.dp),
    )
  }
}
