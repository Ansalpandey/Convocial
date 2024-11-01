package com.app.convocial.data.model

data class NotificationResponse(
    val __v: Int?,
    val _id: String?,
    val createdAt: String?,
    val link: String?,
    val message: String?,
    val read: Boolean?,
    val updatedAt: String?,
    val user: String?,
    val type: String?,
    val details: Details?
)

data class Details(
    val post: PostDetails?
)

data class PostDetails(
    val id: String?,
    val title: String?,
    val name: String?,
    val username: String?,
    val profileImage: String?
)


