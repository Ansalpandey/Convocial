package com.app.convocial.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.app.convocial.ui.components.FollowingItem
import com.app.convocial.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowingScreen(
  modifier: Modifier = Modifier,
  userId: String,
  profileViewModel: ProfileViewModel,
  navController: NavController,
  followingCount: Int,
) {
  LaunchedEffect(true) { profileViewModel.getFollowings(userId) }
  val followingState by profileViewModel.followings.collectAsState()
  val followings = followingState.data?.following ?: emptyList()
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Row (verticalAlignment = Alignment.CenterVertically){
            Icon(
              imageVector = Icons.Default.ArrowBackIosNew,
              contentDescription = "back_button",
              modifier = Modifier.clickable { navController.popBackStack() },
            )
            when (followingCount) {
              0 -> {
                Text(
                  text = "No Followings 😥",
                  fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                  modifier = Modifier.fillMaxWidth(),
                  textAlign = TextAlign.Center,
                )
              }

              1 -> {
                Text(
                  text = "$followingCount Following",
                  fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                  modifier = Modifier.fillMaxWidth(),
                  textAlign = TextAlign.Center,
                )
              }

              else -> {
                Text(
                  text = "$followingCount Following",
                  fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                  modifier = Modifier.fillMaxWidth(),
                  textAlign = TextAlign.Center,
                )
              }
            }
          }
        }
      )
    },
  ) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(it)) {
      items(followings) { follower -> FollowingItem(follower = follower!!) }
    }
  }
}
