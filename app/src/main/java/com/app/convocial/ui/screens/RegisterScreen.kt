package com.app.convocial.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.app.convocial.R
import com.app.convocial.data.model.UserRequest
import com.app.convocial.ui.navigation.Route
import com.app.convocial.ui.viewmodel.AuthViewModel
import com.app.convocial.utils.validateFields
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Composable
fun RegisterScreen(
  modifier: Modifier = Modifier,
  authViewModel: AuthViewModel,
  navController: NavController,
) {
  val userState by authViewModel.userStateHolder.collectAsStateWithLifecycle()
  val context = LocalContext.current
  // Form state variables
  var email by rememberSaveable { mutableStateOf("") }
  var password by rememberSaveable { mutableStateOf("") }
  var age by rememberSaveable { mutableStateOf("") }
  var name by rememberSaveable { mutableStateOf("") }
  var username by rememberSaveable { mutableStateOf("") }
  var bio by rememberSaveable { mutableStateOf("") }
  var passwordVisible by rememberSaveable { mutableStateOf(false) }
  var profileImageUri: Uri? by remember { mutableStateOf(null) }
  var profileImageBase64: String? by remember { mutableStateOf(null) }

  // Error state variables
  var emailError by rememberSaveable { mutableStateOf<String?>(null) }
  var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
  var ageError by rememberSaveable { mutableStateOf<String?>(null) }
  var nameError by rememberSaveable { mutableStateOf<String?>(null) }
  var usernameError by rememberSaveable { mutableStateOf<String?>(null) }

  val launcher =
    rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
      profileImageUri = uri
      uri?.let {
        context.contentResolver.openInputStream(it)?.use { inputStream ->
          profileImageBase64 = inputStream.toBase64()
        }
      }
    }
  // Observe registration status
  LaunchedEffect(userState.isRegistered) {
    if (userState.isRegistered) {
      // Navigate to LoginScreen only after successful registration
      navController.navigate(Route.LoginScreen)
    }
  }
  if (userState.isLoading) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      CircularProgressIndicator()
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize().imePadding().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      item {
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Welcome To Slidee 👋", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text(
          text = "Sign Up and enjoy our community",
          fontWeight = FontWeight.Light,
          fontSize = 18.sp,
          color = Color.Gray,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
          modifier =
            Modifier.size(100.dp).clip(CircleShape).clickable {
              launcher.launch(
                PickVisualMediaRequest(
                  mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                )
              )
            },
          contentAlignment = Alignment.Center,
        ) {
          if (profileImageUri == null) {
            Image(
              painter = painterResource(id = R.drawable.profile),
              contentDescription = "profile_image",
              modifier = Modifier.fillMaxSize(),
            )
          } else {
            AsyncImage(
              model = profileImageUri,
              contentDescription = "profile_image",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop,
            )
          }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = name,
          onValueChange = {
            name = it
            nameError = null
          },
          label = { Text("Name") },
          modifier = Modifier.fillMaxWidth(),
          leadingIcon = {
            Icon(
              painter = painterResource(id = R.drawable.profile_icon),
              contentDescription = "name_icon",
              modifier = Modifier.size(24.dp),
            )
          },
          singleLine = true,
          maxLines = 1,
          isError = nameError != null,
          keyboardActions = KeyboardActions(onDone = KeyboardActions.Default.onNext),
        )
        if (nameError != null) {
          Text(text = nameError ?: "", color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = email,
          onValueChange = {
            email = it.lowercase()
            emailError = null
          },
          label = { Text("Email") },
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
          isError = emailError != null,
          keyboardActions = KeyboardActions(onDone = KeyboardActions.Default.onNext),
        )
        if (emailError != null) {
          Text(text = emailError ?: "", color = Color.Red, fontSize = 12.sp)
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
            val image =
              if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff

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
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = username,
          onValueChange = {
            username = it.lowercase()
            usernameError = null
          },
          label = { Text("Username") },
          modifier = Modifier.fillMaxWidth(),
          leadingIcon = {
            Icon(
              painter = painterResource(id = R.drawable.username),
              contentDescription = "username_icon",
              modifier = Modifier.size(24.dp),
            )
          },
          singleLine = true,
          maxLines = 1,
          isError = usernameError != null,
          keyboardActions = KeyboardActions(onDone = KeyboardActions.Default.onNext),
        )
        if (usernameError != null) {
          Text(text = usernameError ?: "", color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = bio,
          onValueChange = { bio = it },
          label = { Text("Bio") },
          leadingIcon = {
            Icon(
              painter = painterResource(id = R.drawable.bio),
              contentDescription = "bio",
              modifier = Modifier.size(24.dp),
            )
          },
          placeholder = {
            Text(text = "Tell us something about yourself", fontSize = 12.sp, color = Color.Gray)
          },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          maxLines = 1,
          keyboardActions = KeyboardActions(onDone = KeyboardActions.Default.onNext),
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = age,
          onValueChange = {
            age = it
            ageError = null
          },
          label = { Text("Age") },
          leadingIcon = {
            Icon(
              painter = painterResource(id = R.drawable.age),
              contentDescription = "age_icon",
              modifier = Modifier.size(24.dp),
            )
          },
          modifier = Modifier.fillMaxWidth(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          maxLines = 1,
          isError = ageError != null,
          keyboardActions = KeyboardActions(onDone = KeyboardActions.Default.onDone),
        )
        if (ageError != null) {
          Text(text = ageError ?: "", color = Color.Red, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
          Text(text = "By continuing you agree to our")
        }
        Row {
          Text(text = "and ")
          Text(
            text = "Terms & Conditions & Privacy Policy",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
          )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
          modifier = Modifier.fillMaxWidth().height(50.dp),
          onClick = {
            val isValid =
              validateFields(
                name = name,
                email = email,
                password = password,
                age = age,
                username = username,
                setNameError = { nameError = it },
                setEmailError = { emailError = it },
                setPasswordError = { passwordError = it },
                setAgeError = { ageError = it },
                setUsernameError = { usernameError = it },
              )
            if (isValid) {
              val user =
                UserRequest(
                  name = name,
                  email = email,
                  age = age.toInt(),
                  username = username,
                  password = password,
                  bio = bio,
                  profileImageBase64 = profileImageBase64,
                )
              authViewModel.registerUser(user)
            }
          },
        ) {
          Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
          Text(text = "Already Have an Account? ")
          Text(
            text = "Sign In",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { navController.navigate(Route.LoginScreen) },
          )
        }

        if (userState.error?.isNotBlank() == true) {
          Spacer(modifier = Modifier.height(16.dp))
          Text(text = userState.error!!, color = Color.Red, textAlign = TextAlign.Center)
        }
      }
    }
  }
}

fun InputStream.toBase64(): String? {
  // Attempt to decode the stream into a Bitmap
  val bitmap = BitmapFactory.decodeStream(this)

  return if (bitmap != null) {
    // Create an output stream for the base64 conversion
    ByteArrayOutputStream().use { outputStream ->
      // Compress the bitmap to JPEG format with 50% quality
      val isCompressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

      if (isCompressed) {
        val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        Log.i("toBase64", "Bitmap successfully converted to Base64.")
        base64String
      } else {
        Log.e("toBase64", "Failed to compress bitmap.")
        null
      }
    }
  } else {
    Log.e("toBase64", "Failed to decode Bitmap from InputStream.")
    null
  }
}
