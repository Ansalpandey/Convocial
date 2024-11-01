package com.app.convocial.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.convocial.ui.screens.ChatScreen
import com.app.convocial.ui.screens.CreatePostScreen
import com.app.convocial.ui.screens.EditProfileScreen
import com.app.convocial.ui.screens.ExploreScreen
import com.app.convocial.ui.screens.FollowersScreen
import com.app.convocial.ui.screens.FollowingScreen
import com.app.convocial.ui.screens.HomeScreen
import com.app.convocial.ui.screens.ImageScreen
import com.app.convocial.ui.screens.LoginScreen
import com.app.convocial.ui.screens.NotificationScreen
import com.app.convocial.ui.screens.ProfileScreen
import com.app.convocial.ui.screens.RegisterScreen
import com.app.convocial.ui.screens.SettingsScreen
import com.app.convocial.ui.screens.UserProfileScreen
import com.app.convocial.ui.viewmodel.AuthViewModel
import com.app.convocial.ui.viewmodel.CourseViewModel
import com.app.convocial.ui.viewmodel.PostViewModel
import com.app.convocial.ui.viewmodel.ProfileViewModel
import com.app.convocial.ui.viewmodel.SearchViewModel

@Composable
fun NavigationSetup(
  modifier: Modifier = Modifier,
  authViewModel: AuthViewModel,
  profileViewModel: ProfileViewModel,
  postViewModel: PostViewModel,
  navController: NavHostController,
  courseViewModel: CourseViewModel,
  searchViewModel: SearchViewModel,
) {
  val userState by authViewModel.userStateHolder.collectAsState()
  val startDestination = if (userState.isLoggedIn) Route.HomeScreen else Route.LoginScreen

  NavHost(navController = navController, startDestination = startDestination) {
    composable<Route.LoginScreen> {
      LoginScreen(authViewModel = authViewModel, modifier = modifier, navController = navController)
    }

    composable<Route.RegisterScreen> {
      RegisterScreen(
        authViewModel = authViewModel,
        modifier = modifier,
        navController = navController,
      )
    }

    composable<Route.HomeScreen> {
      HomeScreen(
        authViewModel = authViewModel,
        modifier = modifier,
        navController = navController,
        profileViewModel = profileViewModel,
        postViewModel = postViewModel,
      )
    }

    composable<Route.CreatePostScreen> {
      CreatePostScreen(
        postViewModel = postViewModel,
        modifier = modifier,
        navController = navController,
      )
    }

    composable<Route.ProfileScreen> {
      ProfileScreen(
        modifier = modifier,
        profileViewModel = profileViewModel,
        navController = navController,
        postViewModel = postViewModel,
      )
    }

    composable<Route.UserProfileScreen> { backStackEntry ->
      UserProfileScreen(
        modifier = modifier,
        profileViewModel = profileViewModel,
        userId = backStackEntry.arguments?.getString("userId") ?: "",
        navController = navController,
        postViewModel = postViewModel,
      )
    }

    composable<Route.SettingsScreen> {
      SettingsScreen(modifier = modifier, navController = navController)
    }

    composable<Route.EditProfileScreen> { backStackEntry ->
      EditProfileScreen(
        modifier = modifier,
        navController = navController,
        profileViewModel = profileViewModel,
        username = backStackEntry.arguments?.getString("username") ?: "",
        name = backStackEntry.arguments?.getString("name") ?: "",
        bio = backStackEntry.arguments?.getString("bio") ?: "",
        profileImage = backStackEntry.arguments?.getString("profileImage") ?: "",
        location = backStackEntry.arguments?.getString("location") ?: "",
        email = backStackEntry.arguments?.getString("email") ?: "",
        age = backStackEntry.arguments?.getString("age") ?: "",
      )
    }

    composable<Route.ChatScreen> { ChatScreen(modifier = modifier, navController = navController) }

    composable<Route.ExploreScreen> {
      ExploreScreen(
        modifier = modifier,
        navController = navController,
        searchViewModel = searchViewModel,
        profileViewModel = profileViewModel,
      )
    }

    composable<Route.NotificationScreen> {
      NotificationScreen(modifier = modifier, navController = navController)
    }

    composable<Route.ImageScreen> {
      val images = it.arguments?.getStringArray("images")?.toList() ?: emptyList()
      val initialPage = it.arguments?.getInt("initialPage") ?: 0
      ImageScreen(images = images, initialPage = initialPage) { navController.popBackStack() }
    }

    composable<Route.FollowersScreen> { backStackEntry ->
      val userId = backStackEntry.arguments?.getString("userId") ?: ""
      val followersCount = backStackEntry.arguments?.getInt("followersCount") ?: 0
      FollowersScreen(
        userId = userId,
        modifier = modifier,
        profileViewModel = profileViewModel,
        navController = navController,
        followersCount = followersCount,
      )
    }

    composable<Route.FollowingScreen> { backStackEntry ->
      val userId = backStackEntry.arguments?.getString("userId") ?: ""
      val followingCount = backStackEntry.arguments?.getInt("followingCount") ?: 0
      FollowingScreen(
        userId = userId,
        modifier = modifier,
        profileViewModel = profileViewModel,
        navController = navController,
        followingCount = followingCount,
      )
    }
  }
}
