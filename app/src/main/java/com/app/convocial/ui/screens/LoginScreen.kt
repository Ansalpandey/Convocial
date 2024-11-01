package com.app.convocial.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.app.convocial.R
import com.app.convocial.data.model.UserRequest
import com.app.convocial.ui.navigation.Route
import com.app.convocial.ui.viewmodel.AuthViewModel
import com.app.convocial.utils.validateLoginFields

@Composable
fun LoginScreen(
  modifier: Modifier = Modifier,
  authViewModel: AuthViewModel,
  navController: NavController,
) {
  val userState by authViewModel.userStateHolder.collectAsStateWithLifecycle()
  var emailOrUsername by rememberSaveable { mutableStateOf("") }
  var password by rememberSaveable { mutableStateOf("") }
  var passwordVisible by rememberSaveable { mutableStateOf(false) }
  var emailOrUsernameError by remember { mutableStateOf<String?>(null) }
  var passwordError by remember { mutableStateOf<String?>(null) }
  var showErrorDialog by remember { mutableStateOf(false) }
  var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

  // Observe registration status
  LaunchedEffect(userState.isLoggedIn) {
    if (userState.isLoggedIn) {
      // Navigate to LoginScreen only after successful registration
      navController.navigate(Route.HomeScreen)
    }
  }
  // Display loading spinner if needed
  if (userState.isLoading) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      CircularProgressIndicator()
    }
  }

  // Handle error message
  SideEffect {
    if (userState.error?.isNotBlank() == true) {
      errorMessage = userState.error
      showErrorDialog = true
    }
  }

  Column(
    modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Top,
  ) {
    Image(
      painter = painterResource(id = R.drawable.app_icon),
      contentDescription = "app_icon",
      modifier = Modifier.height(200.dp),
    )
    Text(text = "Welcome to Slidee 👋", fontSize = 32.sp, fontWeight = FontWeight.Bold)
    Text(
      text = "Enter your Email & Password to Sign in",
      fontWeight = FontWeight.Light,
      fontSize = 18.sp,
      color = Color.Gray,
    )
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
      value = emailOrUsername,
      onValueChange = {
        emailOrUsername = it
        emailOrUsernameError = null // Reset error when the user types
      },
      label = { Text("Email or Username", color = Color.LightGray) },
      modifier = Modifier.fillMaxWidth(),
      leadingIcon = {
        Icon(
          painter = painterResource(id = R.drawable.email_icon),
          contentDescription = "email_icon",
          modifier = Modifier.size(24.dp),
        )
      },
      singleLine = true,
      maxLines = 1,
      isError = emailOrUsernameError != null,
      keyboardActions = KeyboardActions(onDone = KeyboardActions.Default.onDone),
    )
    if (emailOrUsernameError != null) {
      Text(
        text = emailOrUsernameError ?: "",
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.align(Alignment.Start),
      )
    }
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
      value = password,
      onValueChange = {
        password = it
        passwordError = null
      },
      label = { Text("Password") },
      modifier = Modifier.fillMaxWidth(),
      visualTransformation =
        if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
      leadingIcon = {
        Icon(
          painter = painterResource(id = R.drawable.password_icon),
          contentDescription = "password_icon",
          modifier = Modifier.size(28.dp),
        )
      },
      trailingIcon = {
        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff

        IconButton(onClick = { passwordVisible = !passwordVisible }) {
          Icon(imageVector = image, contentDescription = "Toggle password visibility")
        }
      },
      singleLine = true,
      maxLines = 1,
      isError = passwordError != null,
      keyboardActions = KeyboardActions(onDone = KeyboardActions.Default.onNext),
    )
    if (passwordError != null) {
      Text(text = passwordError ?: "", color = Color.Red, fontSize = 12.sp)
    }
    Spacer(modifier = Modifier.height(16.dp))
    Button(
      onClick = {
        val isValid =
          validateLoginFields(
            emailOrUsername,
            password,
            setEmailOrUsernameError = { emailOrUsernameError = it },
            setPasswordError = { passwordError = it },
          )

        if (isValid) {
          val user =
            UserRequest(
              email = if (emailOrUsername.contains('@')) emailOrUsername else null,
              username = if (!emailOrUsername.contains('@')) emailOrUsername else null,
              password = password,
            )
          authViewModel.loginUser(user)
        }
      },
      modifier = Modifier.fillMaxWidth().height(50.dp),
    ) {
      Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "Forgot Password?", color = Color.LightGray)

    Spacer(modifier = Modifier.height(30.dp))
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
    ) {
      Text(text = "Don't have an account?", fontWeight = FontWeight.Light, fontSize = 18.sp)
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "Sign Up",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.clickable { navController.navigate(Route.RegisterScreen) },
      )
    }
  }

  if (errorMessage != null) {
    LaunchedEffect(Unit) {
      authViewModel.resetUserState() // Reset user state
    }
    AlertDialog(
      onDismissRequest = { errorMessage = null },
      title = { Text(text = "Error") },
      text = { Text(errorMessage ?: "Invalid credentials. Please try again.") },
      confirmButton = { Button(onClick = { errorMessage = null }) { Text("OK") } },
    )
  }
}
