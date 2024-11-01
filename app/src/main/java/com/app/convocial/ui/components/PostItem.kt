package com.app.convocial.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertComment
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.app.convocial.R
import com.app.convocial.data.model.Post
import com.app.convocial.preferences.getLikeCount
import com.app.convocial.preferences.getLikeState
import com.app.convocial.preferences.saveLikeCount
import com.app.convocial.preferences.saveLikeState
import com.app.convocial.ui.navigation.Route
import com.app.convocial.ui.viewmodel.PostViewModel
import com.app.convocial.ui.viewmodel.ProfileViewModel
import com.app.convocial.utils.getRelativeTimeSpanString
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PostItem(
  modifier: Modifier = Modifier,
  post: Post?,
  navController: NavController,
  onClick: () -> Unit,
  profileViewModel: ProfileViewModel,
  postViewModel: PostViewModel,
) {
  val timeAgo = remember { mutableStateOf(getRelativeTimeSpanString(post?.createdAt!!)) }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val isLiked = remember { mutableStateOf(false) }
  val likeCount = remember { mutableIntStateOf(post?.likes ?: 0) }
  val newComment = rememberSaveable { mutableStateOf("") }
  val showComments = rememberSaveable { mutableStateOf(false) }
  val isCommentCreated = rememberSaveable { mutableStateOf(false) }
  // Fetch logged-in user ID
  val loggedInUserId = remember { profileViewModel.loggedInUserProfileState.value.data?.user?._id }

  LaunchedEffect(post?._id) {
    if (!isLiked.value) {
      isLiked.value = post?.likedBy?.any { it._id == loggedInUserId } == true
    } else {
      isLiked.value = getLikeState(context, post?._id!!)
    }
    // If no stored like state, fallback to checking the likedBy list
    if (!isLiked.value) {
      isLiked.value = post?.likedBy?.any { it._id == loggedInUserId } == true
    }
    likeCount.intValue =
      if (getLikeCount(context, post?._id!!) == 0) post.likes!! else getLikeCount(context, post._id)
  }

  LaunchedEffect(post?.createdAt) {
    while (true) {
      timeAgo.value = getRelativeTimeSpanString(post?.createdAt!!)
      delay(60000) // Update every minute
    }
  }

  val pagerState = rememberPagerState()

  Card(
    modifier = Modifier.fillMaxWidth().padding(10.dp),
    shape = RectangleShape,
    colors = CardDefaults.cardColors(Color.Transparent),
  ) {
    Column {
      Row {
        if (post?.createdBy?.profileImage.isNullOrEmpty()) {
          Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "profile_image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.padding(10.dp).clip(CircleShape).size(50.dp),
          )
        } else {
          AsyncImage(
            model = post?.createdBy?.profileImage,
            contentDescription = "profileImage",
            contentScale = ContentScale.Crop,
            modifier = Modifier.padding(10.dp).clip(CircleShape).size(50.dp),
          )
        }

        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            post?.createdBy?.name?.let {
              Text(
                text = it,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onClick.invoke() },
              )
            }
            Text(text = " ${timeAgo.value}", fontSize = 12.sp, fontWeight = FontWeight.Light)
          }
          Text(
            text = "@${post?.createdBy?.username!!}",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            color = Color.Gray,
            fontWeight = FontWeight.SemiBold,
          )
          Text(
            text = post.content!!,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Light,
          )
          Spacer(modifier = Modifier.height(10.dp))
          when {
            post.imageUrl?.isNotEmpty()!! -> {
              Box(modifier = Modifier.fillMaxWidth()) {
                HorizontalPager(
                  count = post.imageUrl.size,
                  state = pagerState,
                  modifier = Modifier.height(300.dp).padding(top = 5.dp, bottom = 10.dp),
                ) { page ->
                  Box(
                    modifier = Modifier.padding(horizontal = 4.dp).fillMaxWidth().height(300.dp)
                  ) {
                    AsyncImage(
                      model = post.imageUrl[page],
                      contentDescription = "post_image",
                      contentScale = ContentScale.Crop,
                      modifier =
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable {
                          navController.navigate(Route.ImageScreen(post.imageUrl, page))
                        },
                    )
                    Box(
                      modifier =
                        Modifier.fillMaxWidth()
                          .height(60.dp)
                          .align(Alignment.BottomCenter)
                          .clip(RoundedCornerShape(12.dp))
                          .background(
                            brush =
                              Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                              )
                          )
                    )
                  }
                }
                if (post.imageUrl.size > 1) {
                  HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    activeColor = MaterialTheme.colorScheme.primary,
                    inactiveColor = Color.LightGray,
                    indicatorWidth = 6.dp,
                    indicatorHeight = 6.dp,
                    spacing = 4.dp,
                  )
                }
              }
            }

            post.videoUrl?.isNotEmpty()!! -> {
              PostVideoPlayer(
                videoUrl = post.videoUrl,
                modifier = Modifier.fillMaxWidth().height(300.dp),
              )
            }

            post.imageUrl.isNotEmpty() && post.videoUrl.isNotEmpty() -> {
              Box(modifier = Modifier.fillMaxWidth()) {
                HorizontalPager(
                  count = post.imageUrl.size,
                  state = pagerState,
                  modifier = Modifier.height(300.dp).padding(top = 5.dp, bottom = 10.dp),
                ) { page ->
                  Box(
                    modifier =
                      Modifier.padding(horizontal = 4.dp) // Gap between images
                        .fillMaxWidth()
                        .height(300.dp)
                  ) {
                    AsyncImage(
                      model = post.imageUrl[page],
                      contentDescription = "post_image",
                      contentScale = ContentScale.Crop,
                      modifier =
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable {
                          navController.navigate(Route.ImageScreen(post.imageUrl, page))
                        },
                    )
                    Box(
                      modifier =
                        Modifier.fillMaxWidth()
                          .height(60.dp)
                          .align(Alignment.BottomCenter)
                          .clip(RoundedCornerShape(12.dp))
                          .background(
                            brush =
                              Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                              )
                          )
                    )
                  }
                  PostVideoPlayer(
                    videoUrl = post.videoUrl,
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                  )
                }
              }
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            IconButton(
              onClick = {
                if (isLiked.value) {
                  post._id?.let { postViewModel.unLikePost(it) }
                  isLiked.value = false
                  likeCount.intValue -= 1
                  scope.launch(Dispatchers.Main) {
                    post._id?.let { saveLikeState(context, it, false) }
                    post._id?.let { saveLikeCount(context, it, likeCount.intValue) }
                  }
                } else {
                  post._id?.let { postViewModel.likePost(it) }
                  isLiked.value = true
                  likeCount.intValue += 1
                  scope.launch(Dispatchers.Main) {
                    post._id?.let { saveLikeState(context, it, true) }
                    post._id?.let { saveLikeCount(context, it, likeCount.intValue) }
                  }
                }
              }
            ) {
              Icon(
                painter =
                  painterResource(
                    id = if (isLiked.value) R.drawable.like_filled else R.drawable.like
                  ),
                modifier = Modifier.size(18.dp),
                contentDescription = "like_btn",
                tint = if (isLiked.value) Color.Red else Color.Gray,
              )
            }
            Text(
              text = "${likeCount.intValue}",
              fontSize = MaterialTheme.typography.titleLarge.fontSize,
              color = Color.Gray,
            )
            // Comment Button
            IconButton(onClick = { showComments.value = true }) {
              Icon(
                painter = painterResource(id = R.drawable.comment),
                modifier = Modifier.size(18.dp),
                contentDescription = "comment_btn",
                tint = Color.Gray,
              )
            }
            Text(
              text = "${post.commentsCount}",
              fontSize = MaterialTheme.typography.titleLarge.fontSize,
              color = Color.Gray,
            )

            IconButton(onClick = { /*TODO: Share functionality*/ }) {
              Icon(
                painter = painterResource(id = R.drawable.share),
                modifier = Modifier.size(18.dp),
                contentDescription = "share_btn",
                tint = Color.Gray,
              )
            }

            MoreOptionsButton(onDeleteClick = {}, onCancelClick = { /*TODO*/ })

            if (showComments.value) {
              ModalBottomSheet(
                onDismissRequest = { showComments.value = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                modifier =
                  Modifier.fillMaxHeight()
                    .fillMaxWidth()
                    .padding(top = 200.dp)
                    .imePadding(), // Ensure padding is adjusted for the keyboard
              ) {
                // State to trigger comment fetching
                val commentsFetched = remember { mutableStateOf(false) }
                LaunchedEffect(showComments.value, isCommentCreated.value) {
                  if (showComments.value && (!commentsFetched.value || isCommentCreated.value)) {
                    postViewModel.getComments(post._id!!) // Fetch comments
                    commentsFetched.value = true // Mark as fetched
                  }
                }

                Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                  Text(
                    text = "Comments",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                  )
                  LazyColumn(
                    modifier =
                      Modifier.weight(1f) // Makes the LazyColumn take available space for scrolling
                        .fillMaxWidth(),
                    content = {
                      if (post.comments.isNullOrEmpty()) {
                        item {
                          Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                          ) {
                            Icon(
                              imageVector =
                                Icons.Default.ChatBubbleOutline, // Replace with desired icon
                              contentDescription = "No comments available",
                              modifier =
                                Modifier.fillMaxWidth()
                                  .size(144.dp)
                                  .fillMaxHeight()
                                  .padding(16.dp)
                                  .align(Alignment.CenterHorizontally),
                              tint = MaterialTheme.colorScheme.primary,
                            )

                            Text(
                              text = "No comments yet. Be the first to comment!",
                              style = MaterialTheme.typography.bodyLarge,
                              textAlign = TextAlign.Center,
                              fontWeight = FontWeight.Bold,
                              modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            )
                          }
                        }
                      }
                      items(post.comments ?: emptyList()) { comment ->
                        CommentItem(
                          comment = comment,
                          postViewModel = postViewModel,
                          modifier = Modifier.padding(8.dp),
                        )
                      }
                    },
                  )

                  Spacer(modifier = Modifier.height(8.dp))

                  Row(
                    modifier =
                      Modifier.fillMaxWidth()
                        .background(
                          MaterialTheme.colorScheme.surface,
                          shape = RoundedCornerShape(20.dp),
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Icon(
                      imageVector = Icons.AutoMirrored.Filled.InsertComment,
                      contentDescription = "User profile",
                      modifier = Modifier.size(32.dp),
                      tint = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // TextField for adding new comment
                    TextField(
                      value = newComment.value,
                      onValueChange = { newComment.value = it },
                      modifier = Modifier.fillMaxWidth().imeNestedScroll(),
                      placeholder = { Text(text = "Add your thoughts...") },
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
                            if (newComment.value.isNotBlank()) {
                              // Post the new comment using postViewModel
                              postViewModel.addComment(post._id!!, newComment.value)
                              isCommentCreated.value = true // Set the flag to true
                              newComment.value = "" // Reset input field after posting
                            }
                          }
                        ),
                      singleLine = true,
                      shape = RoundedCornerShape(20.dp),
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(start = 80.dp),
    ) {
      Text(
        text = if (post?.likes!! >= 1) "Liked by" else "",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(end = 2.dp),
      )
      AsyncImage(
        model = post.likedBy?.let {
          if (it.isNotEmpty()) it.first().profileImage else null
        },
        contentDescription = "profileImage",
        contentScale = ContentScale.Crop,
        modifier = Modifier.clip(CircleShape).size(20.dp),
      )

      Spacer(modifier = Modifier.width(2.dp))

      val likedByUsers = post.likedBy
      if (!likedByUsers.isNullOrEmpty()) {
        val firstUser = likedByUsers.firstOrNull()?.username ?: ""

        val likesCount = post.likes?.minus(1) ?: 0
        Text(
          text =
            when {
              likedByUsers.size == 1 -> firstUser
              likedByUsers.size > 1 -> firstUser
              else -> ""
            },
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(end = 2.dp).clickable {
            if (post.likedBy.first()._id == loggedInUserId){
              navController.navigate(Route.ProfileScreen)
            } else {
              navController.navigate(Route.UserProfileScreen(post.likedBy.first()._id))
            }
          },
        )
        Text(
          " and",
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(end = 2.dp),
        )
        Text(
          text = "$likesCount",
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(end = 2.dp),
        )

        Text(
          text = if (likesCount > 1) " others" else " other",
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(end = 2.dp),
        )
      }
    }
  }
  HorizontalDivider(color = Color.Gray, thickness = 0.25.dp)
}
