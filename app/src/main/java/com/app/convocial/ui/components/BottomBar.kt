package com.app.convocial.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.convocial.ui.navigation.Route

@Composable
fun BottomBar(modifier: Modifier = Modifier, navController: NavController) {
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination

  var selectedItem by remember { mutableIntStateOf(0) }
  val barItems = remember {
    listOf(
      BottomNavItem(
        unselectedIcon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
        route = Route.HomeScreen,
      ),
      BottomNavItem(
        unselectedIcon = Icons.Outlined.Explore,
        selectedIcon = Icons.Filled.Explore,
        route = Route.ExploreScreen,
      ),
      BottomNavItem(
        unselectedIcon = Icons.Default.AddCircleOutline,
        selectedIcon = Icons.Filled.AddCircle,
        route = Route.CreatePostScreen,
      ),
      BottomNavItem(
        unselectedIcon = Icons.Outlined.Notifications,
        selectedIcon = Icons.Filled.Notifications,
        route = Route.NotificationScreen,
      ),
      BottomNavItem(
        unselectedIcon = Icons.AutoMirrored.Outlined.Chat,
        selectedIcon = Icons.Filled.ChatBubble,
        route = Route.ChatScreen,
      ),
    )
  }
  NavigationBar(modifier = modifier) {
    barItems.forEachIndexed { index, barItem ->
      val selected =
        currentDestination?.hierarchy?.any { it.route == barItem.route::class.qualifiedName } ==
          true

      val animatedIconSize by
        animateDpAsState(
          targetValue = if (selected) 24.dp else 20.dp,
          animationSpec = tween(durationMillis = 50),
          label = "",
        )

      val animatedIconColor by
        animateColorAsState(
          targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
          animationSpec = tween(durationMillis = 50),
          label = "",
        )

      NavigationBarItem(
        selected = selected,
        interactionSource = MutableInteractionSource(),
        onClick = {
          selectedItem = index
          navController.navigate(barItem.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
          }
        },
        icon = {
          Icon(
            imageVector = if (selected) barItem.selectedIcon else barItem.unselectedIcon,
            contentDescription = "",
            modifier = Modifier.size(animatedIconSize),
            tint = animatedIconColor,
          )
        },
      )
    }
  }
}

data class BottomNavItem(
  val unselectedIcon: ImageVector,
  val route: Route,
  val selectedIcon: ImageVector,
  val badgeCount: Int? = null,
)
