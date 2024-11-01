package com.app.convocial.ui.components

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.app.convocial.R

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(videoUrl: String, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val activity = context as Activity

  var player: ExoPlayer? = remember {
    ExoPlayer.Builder(context)
      .setRenderersFactory(DefaultRenderersFactory(context).setEnableDecoderFallback(true))
      .build()
      .apply {
        val mediaItem = MediaItem.fromUri(videoUrl)
        setMediaItem(mediaItem)
        prepare()
      }
  }

  val playWhenReady by rememberSaveable { mutableStateOf(true) }
  var playbackPosition by rememberSaveable { mutableLongStateOf(0L) }
  var isLandscape by rememberSaveable { mutableStateOf(false) }

  val playerView = remember { PlayerView(context).apply { useController = true } }

  LaunchedEffect(player) {
    player?.apply {
      this.playWhenReady = playWhenReady
      seekTo(playbackPosition)
      videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
    }
  }

  DisposableEffect(player) {
    onDispose {
      playbackPosition = player?.currentPosition ?: 0L
      player?.release()
    }
  }

  AndroidView(
    modifier = modifier.fillMaxWidth().padding(8.dp).clip(RoundedCornerShape(16.dp)),
    factory = {
      playerView.apply {
        // Add the custom rotation button overlay
        addView(
          createRotationButton(context) {
            isLandscape = !isLandscape
            activity.requestedOrientation =
              if (isLandscape) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
              } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
              }
          }
        )
      }
    },
    update = { it.player = player },
  )
}

fun createRotationButton(context: Context, onRotate: () -> Unit): View {
  return ImageButton(context).apply {
    setImageResource(R.drawable.rotate)
    setBackgroundColor(android.graphics.Color.TRANSPARENT)
    setColorFilter(android.graphics.Color.WHITE)
    scaleType = ImageView.ScaleType.CENTER_INSIDE
    setOnClickListener { onRotate() }

    val size = context.resources.getDimensionPixelSize(R.dimen.exo_icon_size)
    layoutParams =
      FrameLayout.LayoutParams(size, size).apply {
        gravity = Gravity.END or Gravity.TOP
        marginEnd = context.resources.getDimensionPixelSize(R.dimen.exo_margin)
        topMargin = context.resources.getDimensionPixelSize(R.dimen.exo_margin)
      }
  }
}

@OptIn(UnstableApi::class)
@Composable
fun PostVideoPlayer(videoUrl: String, modifier: Modifier = Modifier) {
  // Obtain the current context for use in the ExoPlayer setup
  val context = LocalContext.current

  // State to control whether the video is playing or not
  var isPlaying by remember { mutableStateOf(false) }

  // State to hold the thumbnail bitmap extracted from the video
  var thumbnailBitmap by remember { mutableStateOf<Bitmap?>(null) }

  // Initialize the ExoPlayer
  val player = remember {
    ExoPlayer.Builder(context)
      // Set up the RenderersFactory with fallback enabled to handle various formats
      .setRenderersFactory(DefaultRenderersFactory(context).setEnableDecoderFallback(true))
      .build()
      .apply {
        // Create a MediaItem from the video URL
        val mediaItem = MediaItem.fromUri(videoUrl)
        // Set the media item for playback
        setMediaItem(mediaItem)
        // Prepare the player to start playback
        prepare()
      }
  }

  // Launch a coroutine to extract a thumbnail from the video asynchronously
  LaunchedEffect(videoUrl) {
    val retriever = MediaMetadataRetriever()
    // Set the data source to the video URL
    retriever.setDataSource(videoUrl, HashMap())
    // Extract a frame at time 1 microsecond (nearest frame to the start)
    thumbnailBitmap = retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
    // Release resources used by MediaMetadataRetriever
    retriever.release()
  }

  // Observe the lifecycle of the current context to handle playback
  val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
  DisposableEffect(player) {
    val lifecycleObserver =
      object : DefaultLifecycleObserver {
        override fun onPause(owner: LifecycleOwner) {
          // Pause the player when the app goes to the background
          if (isPlaying) {
            player.pause()
            isPlaying = false
          }
        }

        override fun onResume(owner: LifecycleOwner) {
          // Resume playback if it was previously playing
          if (isPlaying) {
            player.playWhenReady = true
          }
        }
      }

    lifecycleOwner.value.lifecycle.addObserver(lifecycleObserver)

    onDispose {
      player.release() // Release player resources
      lifecycleOwner.value.lifecycle.removeObserver(lifecycleObserver)
    }
  }

  // Layout for the video player, initially showing a placeholder or thumbnail
  Box(
    modifier =
      modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(16.dp)) // Clip corners with a rounded shape
        .background(Color.Black) // Set background color to black
  ) {
    if (!isPlaying) {
      // Display the extracted thumbnail if available and video is not playing
      thumbnailBitmap?.let {
        Image(
          bitmap = it.asImageBitmap(), // Convert Bitmap to ImageBitmap
          contentDescription = "Video Thumbnail", // Accessibility description
          modifier = Modifier.matchParentSize(), // Fill the Box
          contentScale = ContentScale.Crop, // Crop the image to fill the space
        )
      }

      // Play button overlay to start video playback
      IconButton(
        onClick = {
          isPlaying = true // Set isPlaying to true to start playback
          player.playWhenReady = true // Start playback
        },
        modifier =
          Modifier.align(Alignment.Center) // Center the button within the Box
            .size(64.dp) // Set the size of the button
            .background(
              color = Color.Black.copy(alpha = 0.5f),
              shape = CircleShape,
            ), // Button background
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow, // Play arrow icon
          contentDescription = "Play button", // Accessibility description
          tint = Color.White, // Icon color
          modifier = Modifier.size(32.dp), // Size of the icon
        )
      }
    } else {
      // Display the ExoPlayer view when the video is playing
      AndroidView(
        factory = {
          PlayerView(context).apply {
            this.player = player // Set the ExoPlayer instance
            useController = true // Show the media controls
            player.playWhenReady = true // Start playback automatically
          }
        },
        modifier = Modifier.matchParentSize(), // Fill the Box
      )
    }
  }
}

@Composable
fun VideoPreview(uri: Uri?, onRemove: () -> Unit) {
  val context = LocalContext.current
  var thumbnail by remember { mutableStateOf<Bitmap?>(null) }

  LaunchedEffect(uri) {
    uri?.let {
      val mediaMetadataRetriever = MediaMetadataRetriever()
      try {
        mediaMetadataRetriever.setDataSource(context, it)
        val bitmap = mediaMetadataRetriever.getFrameAtTime(0)
        thumbnail = bitmap
      } catch (e: Exception) {
        // Handle exceptions
      } finally {
        mediaMetadataRetriever.release()
      }
    }
  }

  Box(
    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)),
    contentAlignment = Alignment.Center,
  ) {
    if (thumbnail != null) {
      Image(
        bitmap = thumbnail!!.asImageBitmap(),
        contentDescription = "Video Thumbnail",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
      )
    } else {
      // Placeholder
      Box(
        modifier = Modifier.fillMaxSize().background(Color.Gray),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Default.VideoLibrary,
          contentDescription = "Video Placeholder",
          tint = Color.White,
          modifier = Modifier.size(48.dp),
        )
      }
    }
    IconButton(
      onClick = onRemove,
      modifier =
        Modifier.align(Alignment.Center)
          .padding(16.dp)
          .size(50.dp)
          .background(Color.DarkGray.copy(alpha = 0.7f), CircleShape),
    ) {
      Icon(
        imageVector = Icons.Default.Close,
        contentDescription = "close_button",
        tint = Color.White,
        modifier = Modifier.size(20.dp),
      )
    }
  }
}
