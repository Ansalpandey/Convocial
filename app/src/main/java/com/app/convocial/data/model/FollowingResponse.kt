package com.app.convocial.data.model

data class FollowingResponse(
    val currentPage: Int?, // 1
    val following: List<Follower?>?,
    val message: String?, // User followings retrieved successfully
    val totalPages: Int?, // 1
)
