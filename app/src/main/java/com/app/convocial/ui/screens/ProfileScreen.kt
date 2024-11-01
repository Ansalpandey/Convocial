package com.app.convocial.ui.screens

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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.app.convocial.ui.navigation.Route
import com.app.convocial.ui.viewmodel.PostViewModel
import com.app.convocial.ui.viewmodel.ProfileViewModel
import com.app.convocial.utils.formatNumber
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
  modifier: Modifier = Modifier,
  profileViewModel: ProfileViewModel,
  postViewModel: PostViewModel,
  navController: NavController,
) {
  val profileState by profileViewModel.loggedInUserProfileState.collectAsStateWithLifecycle()
  val userPosts = postViewModel.userPosts.collectAsLazyPagingItems()
  LaunchedEffect(key1 = userPosts) {
    postViewModel.getUsersPostsById(profileState.data?.user?._id!!)
  }
  val pagerState = rememberPagerState()
  val coroutineScope = rememberCoroutineScope()
  val tabIcons =
    listOf(painterResource(id = R.drawable.post_stack), painterResource(id = R.drawable.courses))
  var coursesFetched by remember { mutableStateOf(false) }
  val refreshTrigger by remember { mutableStateOf(false) }
  val isProfileRefreshing = profileState is Resource.Loading

  val pullRefreshState =
    rememberPullRefreshState(
      refreshing = isProfileRefreshing,
      onRefresh = {
        profileViewModel.refreshProfile()
        postViewModel.getUsersPostsById(profileState.data?.user?._id ?: "")
      },
    )
  LaunchedEffect(refreshTrigger) { profileViewModel.refreshProfile() }

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
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
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
            },
            actions = {
              IconButton(onClick = { navController.navigate(Route.SettingsScreen) }) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "settings")
              }
            },
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
              modifier = Modifier.fillMaxWidth().padding(start = 30.dp),
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

            Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp)) {
              Text(
                text = state.data?.user?.bio ?: "",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
              )
            }
            Row(
              modifier = Modifier.fillMaxWidth().padding(start = 20.dp, top = 10.dp, end = 20.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Button(
                onClick = {
                  navController.navigate(
                    Route.EditProfileScreen(
                      username = state.data?.user?.username!!,
                      bio = state.data.user.bio!!,
                      profileImage = state.data.user.profileImage!!,
                      name = state.data.user.name!!,
                      location = state.data.user.location!!,
                      email = state.data.user.email!!,
                      age = state.data.user.age.toString(),
                    )
                  )
                },
                modifier = Modifier.weight(1f),
                colors =
                  ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
              ) {
                Text(
                  text = "Edit Profile",
                  fontWeight = FontWeight.Bold,
                  fontSize = MaterialTheme.typography.bodyLarge.fontSize,
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
                      items(userPosts.itemCount) { index ->
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
                          loadState.refresh is LoadState.Loading -> {
                            item {
                              Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center,
                              ) {
                                CircularProgressIndicator()
                              }
                            }
                          }

                          loadState.append is LoadState.Loading -> {
                            item {
                              Box(
                                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                                contentAlignment = Alignment.Center,
                              ) {
                                CircularProgressIndicator()
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
          PullRefreshIndicator(refreshing = isProfileRefreshing, state = pullRefreshState)
        }
      }
    }

    is Resource.Error -> Text(text = "Error: ${state.message}")
  }
}
