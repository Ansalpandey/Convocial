package com.app.convocial.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LottieAnimationComponent(modifier: Modifier = Modifier, animation: Int) {
  val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(animation))
  LottieAnimation(
    composition = composition,
    iterations = LottieConstants.IterateForever,
    modifier = Modifier.padding(16.dp),
  )
}
