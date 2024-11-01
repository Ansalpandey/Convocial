package com.app.convocial.ui.stateholder

import com.app.convocial.data.model.UserResponse
import kotlinx.coroutines.flow.Flow

data class UserStateHolder(
    val isLoading: Boolean = false,
    val data: Flow<UserResponse>? = null,
    val error: String? = "",
    var isLoggedIn: Boolean = false,
    val isRegistered: Boolean = false,
)
