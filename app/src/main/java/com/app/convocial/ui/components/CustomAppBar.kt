package com.app.convocial.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.app.convocial.R
import com.app.convocial.ui.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAppBar(
  modifier: Modifier = Modifier,
  image: String?,
  name: String?,
  navController: NavController,
  scrollBehavior: TopAppBarScrollBehavior,
) {

  TopAppBar(
    modifier = modifier.fillMaxWidth(),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Welcome, ",
          fontSize = MaterialTheme.typography.titleLarge.fontSize,
          fontWeight = FontWeight.Light,
        )
        Text(
          text = "$name 👋",
          fontSize = MaterialTheme.typography.titleLarge.fontSize,
          fontWeight = FontWeight.Bold,
        )
      }
    },
    actions = {
      val profileImageModifier =
        Modifier.padding(end = 10.dp).clip(CircleShape).size(50.dp).clickable {
          navController.navigate(Route.ProfileScreen)
        }

      if (image.isNullOrEmpty()) {
        Image(
          painter = painterResource(id = R.drawable.profile),
          contentDescription = "profile_image",
          contentScale = ContentScale.Crop,
          modifier = profileImageModifier,
        )
      } else {
        AsyncImage(
          model = image,
          contentDescription = "profileImage",
          contentScale = ContentScale.Crop,
          modifier = profileImageModifier,
        )
      }
    },
    scrollBehavior = scrollBehavior,
  )
}
