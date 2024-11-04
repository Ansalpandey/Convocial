package com.app.convocial.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.app.convocial.R
import com.app.convocial.common.Resource
import com.app.convocial.data.model.SearchUserResponse
import com.app.convocial.ui.components.BottomBar
import com.app.convocial.ui.navigation.Route
import com.app.convocial.ui.theme.inverseSurfaceLightMediumContrast
import com.app.convocial.ui.theme.onBackgroundLightHighContrast
import com.app.convocial.ui.theme.onBackgroundLightMediumContrast
import com.app.convocial.ui.theme.onSurfaceLightMediumContrast
import com.app.convocial.ui.theme.onSurfaceVariantLightHighContrast
import com.app.convocial.ui.viewmodel.ProfileViewModel
import com.app.convocial.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
  modifier: Modifier = Modifier,
  searchViewModel: SearchViewModel,
  navController: NavController,
  profileViewModel: ProfileViewModel,
) {
  var query by remember { mutableStateOf("") }
  var active by remember { mutableStateOf(false) }

  // Collect search state and search history from ViewModel
  val searchState by searchViewModel.searchResults.collectAsStateWithLifecycle()
  val searchHistory by searchViewModel.searchHistory.collectAsStateWithLifecycle()
  val profileState by profileViewModel.loggedInUserProfileState.collectAsStateWithLifecycle()

  fun onUserClick(user: SearchUserResponse) {
    if (user._id == profileState.data?.user?._id) {
      searchViewModel.addUserToHistory(user)
      navController.navigate(Route.ProfileScreen)
    } else {
      searchViewModel.addUserToHistory(user)
      navController.navigate(Route.UserProfileScreen(user._id!!))
    }
  }

  // Handle history item click
  fun onHistoryItemClick(historyUser: SearchUserResponse) {
    // Directly navigate to the user's profile without updating the query
    onUserClick(historyUser)
  }

  // Handle clear all history
  fun onClearAllHistory() {
    searchViewModel.clearSearchHistory()
  }

  // Fetch search results continuously when the query changes
  LaunchedEffect(query) {
    delay(1000)
    if (query.isNotEmpty()) {
      val existingUser = searchHistory.find { it.name == query }
      if (existingUser != null) {
        // If user is already in history, navigate directly
        onUserClick(existingUser)
      } else {
        // If not in history, perform the search
        searchViewModel.searchUsers(query)
      }
    }
  }
  Scaffold(bottomBar = { BottomBar(navController = navController) }) { paddingValues ->
    Column(modifier = modifier.fillMaxSize().padding(paddingValues)) {
      val searchBarModifier =
        if (active) {
          Modifier.fillMaxWidth().padding(top = 20.dp).height(700.dp)
        } else {
          Modifier.fillMaxWidth().padding(top = 20.dp, start = 10.dp, end = 10.dp)
        }

      DockedSearchBar(
        modifier = searchBarModifier,
        shape = RoundedCornerShape(20.dp),
        query = query,
        colors =
          SearchBarDefaults.colors(
            containerColor = if (active) Color.Transparent else MaterialTheme.colorScheme.onSecondary,
            dividerColor = Color.Transparent,
          ),
        onQueryChange = { query = it },
        onSearch = {
          active = false
          if (query.isNotEmpty()) {
            val existingUser = searchHistory.find { it.name == query }
            if (existingUser != null) {
              onUserClick(existingUser)
            } else {
              searchViewModel.searchUsers(query)
            }
          }
        },
        active = active,
        onActiveChange = { active = it },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "search") },
        placeholder = { Text(text = "Search Users or Courses") },
        trailingIcon = {
          if (active) {
            Icon(
              modifier =
                Modifier.clickable {
                  if (query.isNotEmpty()) {
                    query = ""
                    searchViewModel.searchUsers(query)
                  } else {
                    active = false
                  }
                },
              imageVector = Icons.Default.Close,
              contentDescription = "close",
            )
          }
        },
      ) {
        if (query.isEmpty()) {
          // Display search history
          if (searchHistory.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxSize().background(Color.Transparent)) {
              Text(
                text = "Clear All",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { onClearAllHistory() },
                color = MaterialTheme.colorScheme.primary,
              )
              Text(
                text = "Search History",
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
              )
              searchHistory.forEach { historyUser ->
                Row(
                  modifier =
                    Modifier.fillMaxWidth().padding(start = 10.dp, bottom = 10.dp).clickable {
                      onHistoryItemClick(historyUser)
                    },
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  AsyncImage(
                    model =
                      historyUser.profileImage.takeIf { !it.isNullOrEmpty() } ?: R.drawable.profile,
                    contentDescription = "user_profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(50.dp).clip(CircleShape),
                    placeholder = painterResource(id = R.drawable.profile),
                  )
                  Column {
                    Text(
                      text = "@${historyUser.username!!}",
                      modifier = Modifier.padding(start = 10.dp),
                      fontSize = MaterialTheme.typography.titleSmall.fontSize,
                      color = Color.Gray,
                      fontWeight = FontWeight.Normal,
                    )
                    Text(text = historyUser.name!!, modifier = Modifier.padding(start = 10.dp))
                  }
                }
              }
            }
          }
        } else {
          // Display search results
          when (val resource = searchState) {
            is Resource.Loading -> {
              Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
              }
            }

            is Resource.Success -> {
              Column(modifier = Modifier.fillMaxSize().background(Color.Transparent)) {
                if (resource.data?.users.isNullOrEmpty()) {
                  Text(
                    text = "No results found",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Center,
                  )
                } else {
                  resource.data?.users?.forEach { user ->
                    Row(
                      modifier =
                        Modifier.fillMaxWidth()
                          .padding(start = 20.dp, bottom = 10.dp, top = 10.dp)
                          .clickable { onUserClick(user) },
                      verticalAlignment = Alignment.CenterVertically,
                    ) {
                      AsyncImage(
                        model =
                          user.profileImage.takeIf { !it.isNullOrEmpty() } ?: R.drawable.profile,
                        contentDescription = "user_profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(50.dp).clip(CircleShape),
                        placeholder = painterResource(id = R.drawable.profile),
                      )
                      Column {
                        Text(
                          text = "@${user.username!!}",
                          modifier = Modifier.padding(start = 10.dp),
                          fontSize = MaterialTheme.typography.titleSmall.fontSize,
                          color = Color.Gray,
                          fontWeight = FontWeight.Normal,
                        )
                        Text(text = user.name!!, modifier = Modifier.padding(start = 10.dp))
                      }
                    }
                  }
                }
              }
            }

            is Resource.Error -> {
              Text(
                text = "Error: ${resource.message}",
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center,
              )
            }
          }
        }
      }
    }
  }
}
