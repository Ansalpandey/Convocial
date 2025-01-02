package com.app.convocial.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.convocial.R

@Composable
fun MoreOptionsButton(onDeleteClick: () -> Unit, onCancelClick: () -> Unit) {
  var expanded by remember { mutableStateOf(false) } // State to track menu visibility

  Box(
      modifier =
          Modifier.wrapContentSize(Alignment.TopEnd)
              .clip(RoundedCornerShape(120.dp)) // Align menu to the top end of the button
      ) {
        // Icon Button to trigger the menu
        IconButton(onClick = { expanded = true }) {
          Icon(
              painter = painterResource(id = R.drawable.more),
              modifier = Modifier.size(18.dp),
              contentDescription = "More Options",
              tint = Color.Gray,
          )
        }

        // Dropdown menu with Instagram-like styling
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier =
                Modifier.background(
                        MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 4.dp),
        ) {
          DropdownMenuItem(
              text = {
                Text("Delete", style = MaterialTheme.typography.bodyMedium, color = Color.Red)
              },
              onClick = {
                expanded = false
                onDeleteClick()
              },
              leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp),
                )
              },
          )

          HorizontalDivider(color = Color.LightGray)

          DropdownMenuItem(
              text = { Text("Cancel", style = MaterialTheme.typography.bodyMedium) },
              onClick = {
                expanded = false
                onCancelClick()
              },
              leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp),
                )
              },
          )
        }
      }
}
