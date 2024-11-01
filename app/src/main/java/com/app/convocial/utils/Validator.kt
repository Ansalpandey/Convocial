package com.app.convocial.utils

fun validateFields(
    name: String,
    email: String,
    password: String,
    age: String,
    username: String,
    setNameError: (String?) -> Unit,
    setEmailError: (String?) -> Unit,
    setPasswordError: (String?) -> Unit,
    setAgeError: (String?) -> Unit,
    setUsernameError: (String?) -> Unit,
): Boolean {
  var isValid = true

  // Email regex pattern to check for a valid email format
  val emailPattern = Regex("^[A-Za-z0-9+_.-]+@(.+)$")

  if (name.isBlank()) {
    setNameError("Name is required")
    isValid = false
  }
  if (email.isBlank()) {
    setEmailError("Email is required")
    isValid = false
  } else if (!email.matches(emailPattern)) {
    setEmailError("Invalid email format")
    isValid = false
  }
  if (password.isBlank()) {
    setPasswordError("Password is required")
    isValid = false
  }
  if (age.isBlank()) {
    setAgeError("Age is required")
    isValid = false
  }
  if (username.isBlank()) {
    setUsernameError("Username is required")
    isValid = false
  }

  return isValid
}

fun validateLoginFields(
    emailOrUsername: String,
    password: String,
    setEmailOrUsernameError: (String?) -> Unit,
    setPasswordError: (String?) -> Unit,
): Boolean {
  var isValid = true

  val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()

  if (emailOrUsername.isBlank()) {
    setEmailOrUsernameError("Email or Username is required")
    isValid = false
  } else if (emailOrUsername.contains('@') && !emailRegex.matches(emailOrUsername)) {
    setEmailOrUsernameError("Invalid email format")
    isValid = false
  }

  if (password.isBlank()) {
    setPasswordError("Password is required")
    isValid = false
  }

  return isValid
}
