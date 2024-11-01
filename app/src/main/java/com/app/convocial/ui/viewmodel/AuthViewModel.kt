package com.app.convocial.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.convocial.common.Resource
import com.app.convocial.data.implementation.UserRepositoryImplementation
import com.app.convocial.data.model.UserRequest
import com.app.convocial.data.model.UserResponse
import com.app.convocial.ui.stateholder.UserStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepository: UserRepositoryImplementation) :
  ViewModel() {

  private val _userStateHolder = MutableStateFlow(UserStateHolder())
  val userStateHolder: StateFlow<UserStateHolder> = _userStateHolder.asStateFlow()

  init {
    viewModelScope.launch {
      userRepository.userStateHolder.collect { state -> _userStateHolder.value = state }
    }
  }

  fun registerUser(user: UserRequest) {
    viewModelScope.launch {
      userRepository.registerUser(user).collect { resource ->
        handleRegisterResource(resource)
        if (resource is Resource.Success) {
          // Update state on success and reset flag after some delay
          _userStateHolder.value = _userStateHolder.value.copy(isRegistered = true)
        }
      }
    }
  }

  fun loginUser(user: UserRequest) {
    viewModelScope.launch {
      userRepository.loginUser(user).collect { resource ->
        handleLoginResource(resource)
        if (resource is Resource.Success) {
          _userStateHolder.value = _userStateHolder.value.copy(isLoggedIn = true)
        }
      }
    }
  }

  private fun handleRegisterResource(resource: Resource<UserResponse>) {
    when (resource) {
      is Resource.Loading -> {
        _userStateHolder.value = _userStateHolder.value.copy(isLoading = true)
      }
      is Resource.Success -> {
        _userStateHolder.value =
          _userStateHolder.value.copy(
            isLoading = false,
            data = flowOf(resource.data!!),
            isLoggedIn = false,
            isRegistered = true, // Set registration status here
          )
      }
      is Resource.Error -> {
        _userStateHolder.value =
          _userStateHolder.value.copy(
            isLoading = false,
            error = resource.message,
            isRegistered = false, // Reset registration status on error
          )
      }
    }
  }

  private fun handleLoginResource(resource: Resource<UserResponse>) {
    when (resource) {
      is Resource.Loading -> {
        _userStateHolder.value = _userStateHolder.value.copy(isLoading = true)
      }
      is Resource.Success -> {
        _userStateHolder.value =
          _userStateHolder.value.copy(
            isLoading = false,
            data = flowOf(resource.data!!),
            isLoggedIn = true,
            isRegistered = false, // Set registration status here
          )
      }
      is Resource.Error -> {
        _userStateHolder.value =
          _userStateHolder.value.copy(
            isLoading = false,
            error = resource.message,
            isRegistered = false, // Reset registration status on error
          )
      }
    }
  }

  fun resetUserState() {
    _userStateHolder.value = UserStateHolder()
  }

  fun logoutUser() {
    viewModelScope.launch {
      userRepository.logoutUser()
      _userStateHolder.value = UserStateHolder() // Reset the state holder after logout
    }
  }
}
