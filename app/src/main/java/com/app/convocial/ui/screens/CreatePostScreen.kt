package com.app.convocial.ui.screens

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.app.convocial.R
import com.app.convocial.data.model.PostRequest
import com.app.convocial.ui.components.VideoPreview
import com.app.convocial.ui.viewmodel.PostViewModel
import java.io.ByteArrayOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
  modifier: Modifier = Modifier,
  postViewModel: PostViewModel,
  navController: NavController,
) {
  val context = LocalContext.current
  var content by rememberSaveable { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(false) }
  var postImageUris by rememberSaveable { mutableStateOf(emptyList<Uri>()) }
  var postImageBase64s by rememberSaveable { mutableStateOf(emptyList<String>()) }
  var videoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
  var videoBase64 by rememberSaveable { mutableStateOf<String?>(null) }

  // Keyboard controller
  val keyboardController = LocalSoftwareKeyboardController.current

  // Launcher for picking images
  val imageLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
      uris.let { selectedUris ->
        postImageUris = postImageUris + selectedUris
        selectedUris.forEach { uri ->
          context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.toBase64(context.contentResolver, uri)?.let { base64String ->
              postImageBase64s = postImageBase64s + base64String
            } ?: Log.e("ImageLauncher", "Failed to convert image to Base64.")
          }
        }
      }
    }


  // Launcher for picking a video
  val videoLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
      uri?.let {
        videoUri = it
        context.contentResolver.openInputStream(it)?.use { inputStream ->
          videoBase64 = inputStream.toBase64(
            context.contentResolver,
            it
          )
        }
      }
    }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(text = "Create Post") },
        navigationIcon = {
          IconButton(onClick = { navController.popBackStack() }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Back")
          }
        },
        actions = {
          Button(
            onClick = {
              keyboardController?.hide()
              val postRequest =
                PostRequest(
                  content = content,
                  imageUrlBase64 = postImageBase64s,
                  videoUrlBase64 = videoBase64,
                  imageUrl = "",
                  videoUrl = "",
                )
              postViewModel.createPost(postRequest)
              isLoading = true
              postViewModel.getPosts()
              navController.popBackStack()
            },
            enabled = content.isNotEmpty() || postImageUris.isNotEmpty() || videoUri != null,
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
          ) {
            Text("Post")
          }
        },
      )
    },
  ) { paddingValues ->
    Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp).imePadding()) {
      TextField(
        value = content,
        onValueChange = { content = it },
        modifier = Modifier.fillMaxWidth().weight(1f),
        placeholder = { Text("What's happening?") },
        textStyle = MaterialTheme.typography.bodyLarge,
        colors =
          TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
          ),
      )

      // Display selected images
      if (postImageUris.isNotEmpty()) {
        LazyRow(
          modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          items(postImageUris) { uri ->
            Box(
              modifier =
                Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)).background(Color.Gray),
              contentAlignment = Alignment.Center,
            ) {
              AsyncImage(
                model = uri,
                contentDescription = "selected_image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
              )

              IconButton(
                onClick = {
                  val indexToRemove = postImageUris.indexOf(uri)
                  postImageUris = postImageUris.toMutableList().apply { removeAt(indexToRemove) }
                  postImageBase64s =
                    postImageBase64s.toMutableList().apply { removeAt(indexToRemove) }
                },
                modifier =
                  Modifier.align(Alignment.Center)
                    .size(50.dp)
                    .background(Color.Gray.copy(alpha = 0.7f), CircleShape),
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "remove_image",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp),
                )
              }
            }
          }

          if (postImageUris.size < 5 && postImageUris.isNotEmpty()) {
            item {
              Box(
                modifier =
                  Modifier.size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                    .clickable {
                      imageLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                      )
                    },
                contentAlignment = Alignment.Center,
              ) {
                Icon(
                  painter = painterResource(id = R.drawable.image),
                  contentDescription = "add_more_images",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(48.dp),
                )
              }
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(10.dp))
      // Display selected video
      if (videoUri != null) {
        Box(
          modifier = Modifier.width(100.dp).height(100.dp).clip(RoundedCornerShape(12.dp)),
          contentAlignment = Alignment.Center,
        ) {
          VideoPreview(
            uri = videoUri,
            onRemove = {
              videoUri = null
              videoBase64 = null
            },
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        // Image picker
        IconButton(
          modifier =
            Modifier.size(50.dp)
              .background(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                RoundedCornerShape(12.dp),
              ),
          onClick = {
            imageLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          },
          enabled = videoUri == null, // Disable if a video is selected
        ) {
          Icon(
            painter = painterResource(id = R.drawable.image),
            contentDescription = "pick_image",
            tint = MaterialTheme.colorScheme.primary,
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        // Video picker
        IconButton(
          modifier =
            Modifier.size(50.dp)
              .background(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                RoundedCornerShape(12.dp),
              ),
          onClick = {
            videoLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
            )
          },
          enabled = postImageUris.isEmpty() && videoUri == null, // Disable if images are selected
        ) {
          Icon(
            imageVector = Icons.Default.VideoLibrary,
            contentDescription = "pick_video",
            tint = MaterialTheme.colorScheme.primary,
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Handle Loading State
      if (isLoading) {
        CircularProgressIndicator()
      }
    }
  }
}

fun InputStream.toBase64(contentResolver: ContentResolver, uri: Uri): String? {
  return try {
    // Check the MIME type of the content
    val mimeType = contentResolver.getType(uri)

    if (mimeType?.startsWith("image") == true) {
      // If it's an image, decode and compress it
      val bitmap = BitmapFactory.decodeStream(this)
      if (bitmap != null) {
        ByteArrayOutputStream().use { outputStream ->
          val isCompressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
          Log.i("toBase64", "Bitmap compressed: $isCompressed")

          if (isCompressed) {
            val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
            Log.i("toBase64", "Image successfully converted to Base64.")
            base64String
          } else {
            Log.e("toBase64", "Failed to compress image.")
            null
          }
        }
      } else {
        Log.e("toBase64", "Failed to decode image from InputStream.")
        null
      }
    } else {
      // If it's not an image, treat it as a binary file (e.g., video)
      val byteArray = this.readBytes()
      val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
      Log.i("toBase64", "Video or binary file successfully converted to Base64.")
      base64String
    }
  } catch (e: Exception) {
    Log.e("toBase64", "Failed to convert file to Base64: ${e.message}")
    null
  }
}

