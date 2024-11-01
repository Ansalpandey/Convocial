package com.app.convocial.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.convocial.R
import com.app.convocial.data.model.Comment
import com.app.convocial.preferences.getCommentLikeState
import com.app.convocial.preferences.saveLikeCount
import com.app.convocial.preferences.saveLikeState
import com.app.convocial.ui.viewmodel.PostViewModel
import com.app.convocial.utils.getRelativeTimeSpanString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CommentItem(modifier: Modifier = Modifier, comment: Comment, postViewModel: PostViewModel) {
  // State management for relative time, like status, like count, reply visibility, and reply text
  val timeAgo = remember { mutableStateOf(getRelativeTimeSpanString(comment.createdAt!!)) }
  val isLiked = remember { mutableStateOf(false) }
  val likeCount = remember { mutableIntStateOf(comment.likes ?: 0) }
  val showReplyField = rememberSaveable { mutableStateOf(false) }
  val replyText = rememberSaveable { mutableStateOf("") }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  LaunchedEffect(comment._id) {
    // Fetch the like state from local storage
    isLiked.value = getCommentLikeState(context, comment._id!!)
    likeCount.intValue = comment.likes!!
  }

  // LaunchedEffect to update the relative time every minute
  LaunchedEffect(comment.createdAt) {
    while (true) {
      timeAgo.value = getRelativeTimeSpanString(comment.createdAt!!)
      delay(60000) // Update every minute
    }
  }

  // Main card containing the comment and the reply functionality
  Card(
    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    shape = MaterialTheme.shapes.medium,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(8.dp) // Inner padding
    ) {
      // Row for the profile image and comment content
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top, // Align items at the top
      ) {
        // Profile image
        if (comment.createdBy?.profileImage.isNullOrEmpty()) {
          Image(
            painter = painterResource(id = R.drawable.profile), // Default profile image
            contentDescription = "profile_image",
            contentScale = ContentScale.Crop,
            modifier =
              Modifier.size(40.dp) // Adjusted size for a cleaner look
                .clip(CircleShape),
          )
        } else {
          AsyncImage(
            model = comment.createdBy?.profileImage,
            contentDescription = "profile_image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(40.dp).clip(CircleShape),
          )
        }

        Spacer(modifier = Modifier.width(8.dp)) // Space between profile image and text

        Column(
          modifier = Modifier.weight(1f) // Use weight to push timeAgo to the end
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, // Space between name and timeAgo
          ) {
            Column {
              Text(
                text = comment.createdBy?.name ?: "Unknown",
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
              )
              Text(
                text = "@${comment.createdBy?.username ?: "user"}",
                color = Color.Gray,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.SemiBold,
              )
            }
            Text(text = timeAgo.value, fontSize = 12.sp, fontWeight = FontWeight.Light)
          }

          Text(
            text = comment.content ?: "",
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(top = 4.dp), // Add space above comment content
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Row for like and reply actions
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = {
            if (isLiked.value) {
              //              unlikePost(post._id!!)
              //              postViewModel.unLikePost(post._id)
              isLiked.value = false
              likeCount.intValue -= 1
              scope.launch {
                saveLikeState(context, comment._id!!, false)
                saveLikeCount(context, comment._id, likeCount.intValue) // Save the updated count
              }
            } else {
              //              likePost(post._id!!)
              //              postViewModel.likePost(post._id)
              isLiked.value = true
              likeCount.intValue += 1
              scope.launch {
                saveLikeState(context, comment._id!!, true)
                saveLikeCount(context, comment._id, likeCount.intValue) // Save the updated count
              }
            }
          }
        ) {
          Icon(
            painter =
              painterResource(id = if (isLiked.value) R.drawable.like_filled else R.drawable.like),
            modifier = Modifier.size(14.dp),
            contentDescription = "like_btn",
            tint = if (isLiked.value) Color.Red else Color.Gray,
          )
        }
        Text(
          text = "${likeCount.intValue}",
          fontSize = MaterialTheme.typography.titleMedium.fontSize,
          color = Color.Gray,
        )

        // "Reply" button that toggles the visibility of the reply text field
        Text(
          text = "Reply",
          fontSize = MaterialTheme.typography.bodyMedium.fontSize,
          fontWeight = FontWeight.Light,
          color = MaterialTheme.colorScheme.primary,
          modifier =
            Modifier.padding(start = 12.dp).clickable {
              showReplyField.value = !showReplyField.value
            },
        )
      }

      // Conditionally show the reply TextField if showReplyField is true
      if (showReplyField.value) {
        Spacer(modifier = Modifier.height(8.dp))

        // Row containing the profile icon and the reply TextField
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier =
            Modifier.fillMaxWidth()
              .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp))
              .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
          if (comment.createdBy?.profileImage.isNullOrEmpty()) {
            Image(
              painter = painterResource(id = R.drawable.profile), // Default profile image
              contentDescription = "profile_image",
              contentScale = ContentScale.Crop,
              modifier = Modifier.size(32.dp).clip(CircleShape), // Adjusted size for a cleaner look
            )
          } else {

            AsyncImage(
              model = comment.createdBy?.profileImage,
              contentDescription = "profile_image",
              contentScale = ContentScale.Crop,
              modifier = Modifier.size(32.dp).clip(CircleShape),
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          // TextField for adding the reply
          TextField(
            value = replyText.value,
            onValueChange = { replyText.value = it },
            modifier = Modifier.fillMaxWidth().imePadding(), // Adjusts for the keyboard
            placeholder = { Text(text = "Add your reply...") },
            colors =
              TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
              ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            keyboardActions =
              KeyboardActions(
                onSend = {
                  if (replyText.value.isNotBlank()) {
                    // Call ViewModel to handle posting the reply
                    //                  postViewModel.addReply(comment.id, replyText.value)
                    replyText.value = "" // Reset reply field after sending
                    showReplyField.value = false // Hide reply field after posting
                  }
                }
              ),
            singleLine = true,
            shape = RoundedCornerShape(20.dp), // Rounded edges for the TextField
          )
        }
      }
    }
  }
}
