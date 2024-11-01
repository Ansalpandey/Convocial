package com.app.convocial.data.model

data class EditProfileRequest(
    val username: String,
    val name: String,
    val bio: String,
    val age: Int,
    val email: String,
    val location: String,
    val profileImage: String,
    val profileImageBase64: String?
)
