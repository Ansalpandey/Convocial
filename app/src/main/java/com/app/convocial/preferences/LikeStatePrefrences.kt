package com.app.convocial.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.likeDataStore: DataStore<Preferences> by preferencesDataStore(name = "likes")

suspend fun saveLikeState(context: Context, postId: String, isLiked: Boolean) {
  val key = booleanPreferencesKey(postId)
  context.likeDataStore.edit { preferences -> preferences[key] = isLiked }
}

suspend fun getLikeState(context: Context, postId: String): Boolean {
  val key = booleanPreferencesKey(postId)
  val preferences =
    context.likeDataStore.data
      .map { it[key] ?: false } // Default to false if the value is not found
      .first()
  return preferences
}

suspend fun saveLikeCount(context: Context, postId: String, count: Int) {
  if (count < 0) return // Prevent saving negative counts
  val key = intPreferencesKey("${postId}_count") // Create a key for the like count
  context.likeDataStore.edit { preferences -> preferences[key] = count }
}

// Add this function to get the like count
suspend fun getLikeCount(context: Context, postId: String): Int {
  val key = intPreferencesKey("${postId}_count")
  val preferences =
    context.likeDataStore.data
      .map { it[key] ?: 0 } // Default to 0 if the value is not found
      .first()
  return preferences
}

suspend fun saveCommentLikeState(context: Context, commentId: String, isLiked: Boolean) {
  val key = booleanPreferencesKey(commentId)
  context.likeDataStore.edit { preferences -> preferences[key] = isLiked }
}

suspend fun getCommentLikeState(context: Context, commentId: String): Boolean {
  val key = booleanPreferencesKey(commentId)
  val preferences =
    context.likeDataStore.data
      .map { it[key] ?: false } // Default to false if the value is not found
      .first()
  return preferences
}

suspend fun saveCommentLikeCount(context: Context, commentId: String, count: Int){
  if (count < 0) return // Prevent saving negative counts
  val key = intPreferencesKey("${commentId}_count") // Create a key for the like count
  context.likeDataStore.edit { preferences -> preferences[key] = count }
}

suspend fun getCommentLikeCount(context: Context, commentId: String): Int {
  val key = intPreferencesKey("${commentId}_count")
  val preferences =
    context.likeDataStore.data
      .map { it[key] ?: 0 } // Default to 0 if the value is not found
      .first()
  return preferences
}
