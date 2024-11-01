package com.app.convocial.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {
  private val splashShowFlow = MutableStateFlow(true)
  val isSplashShow = splashShowFlow.asStateFlow()

  init {
    viewModelScope.launch(Dispatchers.Main) {
      delay(1000)
      splashShowFlow.value = false
    }
  }
}
