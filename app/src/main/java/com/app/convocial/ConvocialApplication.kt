package com.app.convocial

import android.app.Application
import com.app.convocial.utils.TokenRefreshManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ConvocialApplication : Application() {

  @Inject lateinit var tokenRefreshManager: TokenRefreshManager

  override fun onCreate() {
    super.onCreate()
    if (::tokenRefreshManager.isInitialized) {
      tokenRefreshManager.startTokenRefresh()
    }
  }
}
