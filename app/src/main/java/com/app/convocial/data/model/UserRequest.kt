package com.app.convocial.data.model

data class UserRequest(
    val age: Int? = null,
    val bio: String? = null,
    val email: String? = null,
    val name: String? = null,
    val password: String? = null,
    val profileImage: String? = null,
    val profileImageBase64: String? = null,
    val username: String? = null,
)
