package com.app.convocial.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.app.convocial.R
import com.app.convocial.ui.components.AppBar
import com.app.convocial.ui.components.BottomBar
import com.app.convocial.ui.components.LottieAnimationComponent
import com.app.convocial.ui.components.PostItem
import com.app.convocial.ui.components.PostShimmerItem
import com.app.convocial.ui.navigation.Route
import com.app.convocial.ui.viewmodel.AuthViewModel
import com.app.convocial.ui.viewmodel.PostViewModel
import com.app.convocial.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
  modifier: Modifier = Modifier,
  authViewModel: AuthViewModel,
  profileViewModel: ProfileViewModel,
  postViewModel: PostViewModel,
  navController: NavController,
) {
  val userState by authViewModel.userStateHolder.collectAsStateWithLifecycle()
  val profileState by profileViewModel.loggedInUserProfileState.collectAsStateWithLifecycle()
  val posts = postViewModel.posts.collectAsLazyPagingItems()
  val listState = rememberLazyListState()

  // PullRefresh state tied to loading state of posts
  val isRefreshing = false
  val pullRefreshState =
    rememberPullRefreshState(
      refreshing = isRefreshing,
      onRefresh = { postViewModel.refreshPosts() },
    )

  // Create scroll behavior for the top bar
  val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

  LaunchedEffect(key1 = userState) { profileViewModel.fetchUserProfile() }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      // TopBar should only recompose when profileImage or name changes
      val profileImage by remember { derivedStateOf { profileState.data?.user?.profileImage } }
      val profileName by remember { derivedStateOf { profileState.data?.user?.name ?: "" } }
      AppBar(
        image = profileImage,
        name = profileName,
        navController = navController,
        scrollBehavior = scrollBehavior,
      )
    },
    bottomBar = { BottomBar(navController = navController) },
  ) { innerPadding ->
    if (userState.isLoading) {
      Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
      }
    } else {
      if (userState.isLoggedIn) {
        Box(modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState).padding(innerPadding)) {
          LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            if (posts.itemCount == 0 && posts.loadState.refresh is LoadState.NotLoading) {
              item {
                Column(
                  modifier = Modifier.fillMaxSize().padding(top = 100.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center,
                ) {
                  LottieAnimationComponent(
                    animation = R.raw.animation,
                    modifier = Modifier.size(500.dp),
                  )
                  OutlinedButton(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    onClick = { postViewModel.getPosts() },
                  ) {
                    Text(
                      text = "Reload Posts",
                      fontSize = MaterialTheme.typography.labelLarge.fontSize,
                      fontWeight = FontWeight.Medium,
                    )
                  }
                }
              }
            }

            if (posts.loadState.refresh is LoadState.Loading) {
              items(10) {
                PostShimmerItem(
                  isLoading = true,
                  contentAfterLoading = {
                    PostItem(
                      post = posts.peek(it),
                      navController = navController,
                      profileViewModel = profileViewModel,
                      postViewModel = postViewModel,
                      onClick = {
                        val loggedInUserId = profileState.data?.user?._id
                        if (posts.peek(it)?.createdBy?._id == loggedInUserId) {
                          navController.navigate(Route.ProfileScreen)
                        } else {
                          navController.navigate(
                            Route.UserProfileScreen(posts.peek(it)?.createdBy?._id!!)
                          )
                        }
                      },
                    )
                  },
                )
              }
            } else {
              items(posts.itemCount, key = { posts.peek(it)?._id ?: it }) { index ->
                posts[index]?.let { post ->
                  PostItem(
                    post = post,
                    navController = navController,
                    profileViewModel = profileViewModel,
                    postViewModel = postViewModel,
                    onClick = {
                      val loggedInUserId = profileState.data?.user?._id
                      if (post.createdBy?._id == loggedInUserId) {
                        navController.navigate(Route.ProfileScreen)
                      } else {
                        navController.navigate(Route.UserProfileScreen(post.createdBy?._id!!))
                      }
                    },
                  )
                }
              }
            }
            posts.apply {
              when {
                loadState.append is LoadState.Loading -> {
                  item {
                    Box(
                      modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 10.dp),
                      contentAlignment = Alignment.Center,
                    ) {
                      CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                      )
                    }
                  }
                }
              }
            }
          }

          PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
          )
        }
      } else {
        LaunchedEffect(Unit) { navController.navigate(Route.LoginScreen) }
      }
    }
  }
}
