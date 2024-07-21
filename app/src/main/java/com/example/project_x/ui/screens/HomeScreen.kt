package com.example.project_x.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.project_x.ui.components.CustomAppBar
import com.example.project_x.ui.components.CustomBottomBar
import com.example.project_x.ui.components.PostItem
import com.example.project_x.ui.navigation.LoginScreen
import com.example.project_x.ui.viewmodel.AuthViewModel
import com.example.project_x.ui.viewmodel.PostViewModel
import com.example.project_x.ui.viewmodel.ProfileViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    postViewModel: PostViewModel,
    navController: NavController,
) {
  val userState by authViewModel.userStateHolder.collectAsState()
  val profileState by profileViewModel.userProfileState.collectAsState()
  val posts = postViewModel.posts.collectAsLazyPagingItems()
  var isProfileFetched by remember { mutableStateOf(false) }
  val lifecycleOwner = LocalLifecycleOwner.current

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        if (userState.isLoggedIn) {
          CustomAppBar(
              image = profileState.data?.user?.profileImage,
              name = profileState.data?.user?.name,
              navController = navController)
        }
      },
      floatingActionButton = {
        FloatingActionButton(
            shape = RoundedCornerShape(20.dp),
            content = { Icon(imageVector = Icons.Default.Add, contentDescription = "create_post") },
            onClick = {
              navController.navigate(
                  "create_post_screen") // Ensure this route matches your nav graph
              navController.currentBackStackEntry
                  ?.savedStateHandle
                  ?.getLiveData<Boolean>("refreshProfile")
                  ?.observe(lifecycleOwner) { shouldRefresh ->
                    if (shouldRefresh) {
                      profileViewModel.refreshProfile()
                      postViewModel.getPosts() // Refetch posts if needed
                    }
                  }
            },
        )
      },
      bottomBar = {
        if (userState.isLoggedIn) {
          CustomBottomBar(authViewModel = authViewModel)
        }
      },
  ) { innerPadding ->
    if (userState.isLoading) {
      Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
      }
    } else {
      if (userState.isLoggedIn) {
        LaunchedEffect(key1 = isProfileFetched) {
          if (!isProfileFetched) {
            profileViewModel.fetchUserProfile()
            postViewModel.getPosts()
            isProfileFetched = true
          }
        }
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          if (posts.itemCount == 0 && posts.loadState.refresh is LoadState.NotLoading) {
            item {
              Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No posts found")
              }
            }
          }
          items(posts.itemCount) { index -> posts[index]?.let { post -> PostItem(post = post) } }
          posts.apply {
            when {
              loadState.refresh is LoadState.Loading -> {
                item {
                  Box(
                      modifier = Modifier.fillParentMaxSize(),
                      contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                      }
                }
              }
              loadState.append is LoadState.Loading -> {
                item {
                  Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                  }
                }
              }
              loadState.append is LoadState.Error -> {
                item {
                  Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(text = "Error loading more posts")
                  }
                }
              }
              loadState.refresh is LoadState.Error -> {
                item {
                  Box(
                      modifier = Modifier.fillParentMaxSize(),
                      contentAlignment = Alignment.Center) {
                        Text(text = "Error refreshing posts")
                      }
                }
              }
            }
          }
        }
      } else {
        navController.navigate(LoginScreen)
      }
    }
  }
}
