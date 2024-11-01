package com.app.convocial.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.app.convocial.data.model.NotificationResponse
import com.app.convocial.ui.components.CustomBottomBar
import com.app.convocial.ui.components.NotificationItem
import com.app.convocial.ui.viewmodel.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
  modifier: Modifier = Modifier,
  navController: NavController,
  notificationViewModel: NotificationViewModel = hiltViewModel(),
) {
  // Use a state list to manage the combined notification state
  val notificationList = remember { mutableStateListOf<NotificationResponse>() }

  // Collect notifications from the ViewModel
  val notifications = notificationViewModel.notifications.collectAsStateWithLifecycle()

  // When the composable is first loaded, fetch the notifications from the ViewModel
  LaunchedEffect(Unit) {
    notificationViewModel.getNotifications("671b630c73f92f038a929a0d") // Use the provided userId
  }

  // Update notificationList when notifications change
  LaunchedEffect(notifications.value) {
    notificationList.clear() // Clear the previous notifications
    notificationList.addAll(notifications.value) // Add new notifications
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
              Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "back")
            }
            Text(text = "Notifications", fontSize = 28.sp, modifier = Modifier.padding(10.dp))
          }
        }
      )
    },
    bottomBar = { CustomBottomBar(navController = navController) },
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier.padding(innerPadding),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.Start,
    ) {
      // Display each notification using NotificationItem
      items(notificationList) { notification -> // Use notificationList here
        NotificationItem(notificationResponse = notification)
      }
    }
  }
}
