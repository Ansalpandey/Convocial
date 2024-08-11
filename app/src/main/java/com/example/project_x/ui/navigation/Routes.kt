package com.example.project_x.ui.navigation

import kotlinx.serialization.Serializable

sealed class Route {

  @Serializable data object LoginScreen : Route()

  @Serializable data object RegisterScreen : Route()

  @Serializable data object HomeScreen : Route()

  @Serializable data object CreatePostScreen : Route()

  @Serializable data object ProfileScreen : Route()

  @Serializable data class UserProfileScreen(val userId: String) : Route()

  @Serializable data object SettingsScreen : Route()

  @Serializable
  data class EditProfileScreen(
    val username: String,
    val name: String,
    val bio: String,
    val profileImage: String,
    val location: String,
    val email: String,
    val age: String,
  ) : Route()

  @Serializable data object ChatScreen : Route()

  @Serializable data object ExploreScreen : Route()

  @Serializable data object NotificationScreen : Route()

  @Serializable
  data class ImageScreen(val images: List<String>, val initialPage: Int = 0) : Route()
}
