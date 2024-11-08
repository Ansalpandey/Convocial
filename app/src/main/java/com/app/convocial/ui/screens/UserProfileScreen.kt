package com.app.convocial.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.app.convocial.R
import com.app.convocial.common.Resource
import com.app.convocial.ui.components.CourseItem
import com.app.convocial.ui.components.PostItem
import com.app.convocial.ui.components.PostShimmerItem
import com.app.convocial.ui.navigation.Route
import com.app.convocial.ui.viewmodel.PostViewModel
import com.app.convocial.ui.viewmodel.ProfileViewModel
import com.app.convocial.utils.formatNumber
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun UserProfileScreen(
  modifier: Modifier = Modifier,
  profileViewModel: ProfileViewModel,
  userId: String,
  postViewModel: PostViewModel,
  navController: NavController,
) {
  val profileState by profileViewModel.userProfileState.collectAsStateWithLifecycle()
  val userPosts = postViewModel.userPosts.collectAsLazyPagingItems()
  val isFollowing by profileViewModel.isFollowing.collectAsStateWithLifecycle()
  val pagerState = rememberPagerState()
  val coroutineScope = rememberCoroutineScope()
  val tabIcons =
    listOf(painterResource(id = R.drawable.post_stack), painterResource(id = R.drawable.courses))
  var coursesFetched by remember { mutableStateOf(false) }
  var isRefreshing by remember { mutableStateOf(false) }
  LaunchedEffect(key1 = userId) {
    profileViewModel.fetchUserProfileById(userId)
    profileViewModel.checkIfFollowing(userId)
    postViewModel.getUsersPostsById(userId)
  }

  val pullRefreshState =
    rememberPullRefreshState(
      refreshing = isRefreshing,
      onRefresh = {
        isRefreshing = true
        profileViewModel.fetchUserProfileById(userId)
        postViewModel.getUsersPostsById(userId)
        isRefreshing = false
      },
    )

  when (val state = profileState) {
    is Resource.Loading -> {
      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
      }
    }

    is Resource.Success -> {
      Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
          TopAppBar(
            title = {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
              ) {
                IconButton(onClick = { navController.popBackStack() }) {
                  Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "back")
                }
                Text(
                  text = "@${state.data?.user?.username ?: ""}",
                  textAlign = TextAlign.Center,
                  modifier = Modifier.fillMaxWidth(),
                  fontSize = MaterialTheme.typography.titleLarge.fontSize,
                )
              }
            }
          )
        },
      ) { paddingValues ->
        LazyColumn(
          modifier = Modifier.padding(paddingValues).fillMaxSize().pullRefresh(pullRefreshState)
        ) {
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              AsyncImage(
                model = state.data?.user?.profileImage,
                contentDescription = "profileImage",
                modifier = Modifier.clip(CircleShape).size(80.dp),
                placeholder = painterResource(id = R.drawable.profile),
                error = painterResource(id = R.drawable.profile),
                contentScale = ContentScale.Crop,
              )
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
              ) {
                Text(
                  text = formatNumber(state.data?.postCount?.toLong() ?: 0),
                  fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.padding(end = 5.dp),
                )
                Text(
                  text = "Posts",
                  fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                  fontWeight = FontWeight.ExtraBold,
                )
              }
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                  Modifier.clickable {
                    navController.navigate(
                      Route.FollowingScreen(
                        userId = state.data?.user?._id!!,
                        followingCount = state.data.user.followingCount!!,
                      )
                    )
                  },
              ) {
                Text(
                  text = formatNumber(state.data?.user?.followingCount?.toLong() ?: 0),
                  fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.padding(end = 5.dp),
                )
                Text(
                  text = "Following",
                  fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                  fontWeight = FontWeight.ExtraBold,
                )
              }
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                  Modifier.clickable {
                    navController.navigate(
                      Route.FollowersScreen(
                        userId = state.data?.user?._id!!,
                        followersCount = state.data.user.followersCount!!,
                      )
                    )
                  },
              ) {
                Text(
                  text = formatNumber(state.data?.user?.followersCount?.toLong() ?: 0),
                  fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.padding(end = 5.dp),
                )
                Text(
                  text = "Followers",
                  fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                  fontWeight = FontWeight.ExtraBold,
                )
              }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth().padding(start = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Start,
            ) {
              Text(
                text = (state.data?.user?.name + " "),
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontWeight = FontWeight.Bold,
              )
              if (
                profileState.data?.user?.location != null &&
                  profileState.data!!.user?.location != ""
              ) {
                Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = "location",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(24.dp),
                )
                Text(
                  text = profileState.data?.user?.location!!,
                  fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                  fontWeight = FontWeight.Bold,
                  color = Color.LightGray,
                  modifier = Modifier.padding(end = 5.dp),
                )
              }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(modifier = Modifier.fillMaxWidth().padding(start = 10.dp)) {
              Text(
                text = state.data?.user?.bio ?: "",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
              )
            }
            Row(
              modifier = Modifier.fillMaxWidth().padding(start = 10.dp, top = 10.dp, end = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              FilledIconToggleButton(
                modifier = Modifier.weight(1f),
                checked = isFollowing,
                onCheckedChange = { profileViewModel.toggleFollowUser(userId) },
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  if (isFollowing) {
                    Text(
                      text = "Following",
                      fontWeight = FontWeight.Bold,
                      fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    )
                  } else {
                    Text(
                      text = "Follow",
                      fontWeight = FontWeight.Bold,
                      fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    )
                  }
                }
              }
              IconButton(onClick = { /*TODO*/ }) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.Send,
                  contentDescription = "message",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(32.dp).align(Alignment.CenterVertically),
                )
              }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column {
              TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth(),
              ) {
                tabIcons.forEachIndexed { index, icon ->
                  Tab(
                    icon = {
                      Icon(
                        painter = icon,
                        contentDescription = "icons",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp),
                      )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                      coroutineScope.launch {
                        pagerState.scrollToPage(index)
                        if (index == 1 && !coursesFetched) {
                          coursesFetched = true
                        }
                      }
                    },
                  )
                }
              }
            }
            HorizontalPager(
              count = tabIcons.size,
              state = pagerState,
              modifier = Modifier.fillMaxWidth().height(700.dp), // Set a fixed height
            ) { page ->
              when (page) {
                0 -> {
                  if (userPosts.itemCount == 0) {
                    Icon(
                      painter = painterResource(id = R.drawable.post_stack),
                      contentDescription = "posts_not_found",
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.fillMaxSize().padding(60.dp).alpha(0.6f),
                    )
                  } else {
                    LazyColumn(
                      modifier = Modifier.fillMaxSize(),
                      horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                      if (userPosts.loadState.refresh is LoadState.Loading) {
                        items(10) {
                          PostShimmerItem(
                            isLoading = true,
                            contentAfterLoading = {
                              PostItem(
                                post = userPosts.peek(it),
                                navController = navController,
                                profileViewModel = profileViewModel,
                                postViewModel = postViewModel,
                                onClick = {
                                  val loggedInUserId = profileState.data?.user?._id
                                  if (userPosts.peek(it)?.createdBy?._id == loggedInUserId) {
                                    navController.navigate(Route.ProfileScreen)
                                  } else {
                                    navController.navigate(
                                      Route.UserProfileScreen(userPosts.peek(it)?.createdBy?._id!!)
                                    )
                                  }
                                },
                              )
                            },
                          )
                        }
                      }
                      items(userPosts.itemCount, key = { userPosts.peek(it)?._id ?: it }) { index ->
                        PostItem(
                          post = userPosts[index],
                          navController = navController,
                          profileViewModel = profileViewModel,
                          postViewModel = postViewModel,
                          onClick = {
                            /*TODO*/
                          },
                        )
                      }
                      userPosts.apply {
                        when {
                          loadState.append is LoadState.Loading -> {
                            item {
                              Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                              ) {
                                Column(
                                  modifier = Modifier.fillMaxWidth(),
                                  horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                  PullRefreshIndicator(refreshing = true, state = pullRefreshState)
                                }
                              }
                            }
                          }
                          loadState.append is LoadState.Error -> {
                            item {
                              Box(
                                modifier = Modifier.fillParentMaxSize().padding(30.dp),
                                contentAlignment = Alignment.Center,
                              ) {
                                Column(
                                  horizontalAlignment = Alignment.CenterHorizontally,
                                  modifier = Modifier.fillMaxSize(),
                                  verticalArrangement = Arrangement.SpaceEvenly,
                                ) {
                                  Image(
                                    painter = painterResource(id = R.drawable.page_not_found),
                                    contentDescription = "posts_not_found",
                                  )
                                  Text(
                                    text = "Error 404! Posts not found",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                  )
                                  OutlinedButton(onClick = { postViewModel.getPosts() }) {
                                    Text(
                                      text = "Reload Posts",
                                      fontSize = MaterialTheme.typography.labelLarge.fontSize,
                                      fontWeight = FontWeight.Medium,
                                    )
                                  }
                                }
                              }
                            }
                          }
                          loadState.refresh is LoadState.Error -> {
                            item {
                              Box(
                                modifier = Modifier.fillParentMaxSize().padding(30.dp),
                                contentAlignment = Alignment.Center,
                              ) {
                                Column(
                                  horizontalAlignment = Alignment.CenterHorizontally,
                                  modifier = Modifier.fillMaxSize(),
                                  verticalArrangement = Arrangement.SpaceEvenly,
                                ) {
                                  Image(
                                    painter = painterResource(id = R.drawable.page_not_found),
                                    contentDescription = "posts_not_found",
                                  )
                                  Text(
                                    text = "Error 404! Posts not found",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                  )
                                  OutlinedButton(onClick = { postViewModel.getPosts() }) {
                                    Text(
                                      text = "Reload Posts",
                                      fontSize = MaterialTheme.typography.labelLarge.fontSize,
                                      fontWeight = FontWeight.Medium,
                                    )
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
                1 -> {
                  val courses = state.data?.user?.courses
                  if (courses.isNullOrEmpty()) {
                    Icon(
                      painter = painterResource(id = R.drawable.courses),
                      contentDescription = "courses_not_found",
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.fillMaxSize().padding(60.dp).alpha(0.6f),
                    )
                  } else {
                    LazyColumn(
                      modifier = Modifier.fillMaxSize(),
                      horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                      items(courses) { course ->
                        CourseItem(course = course!!, modifier = Modifier.fillMaxWidth())
                      }
                    }
                  }
                }
              }
            }
          }
        }
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          PullRefreshIndicator(refreshing = true, state = pullRefreshState)
        }
      }
    }
    is Resource.Error -> Text(text = "Error: ${state.message}")
  }
}
