package com.app.convocial.di

import android.content.Context
import com.app.convocial.data.api.ApiService
import com.app.convocial.data.api.AuthInterceptor
import com.app.convocial.data.api.AuthenticatedApiService
import com.app.convocial.data.datasource.CourseDataSource
import com.app.convocial.data.datasource.NotificationDataSource
import com.app.convocial.data.datasource.PostDataSource
import com.app.convocial.data.datasource.UserDataSource
import com.app.convocial.data.implementation.CourseRepositoryImplementation
import com.app.convocial.data.implementation.NotificationRepositoryImplementation
import com.app.convocial.data.implementation.PostRepositoryImplementation
import com.app.convocial.data.implementation.UserRepositoryImplementation
import com.app.convocial.data.pagination.PostPagingSource
import com.app.convocial.data.pagination.UserPostsPagingSource
import com.app.convocial.data.repository.CourseRepository
import com.app.convocial.data.repository.NotificationRepository
import com.app.convocial.data.repository.PostRepository
import com.app.convocial.data.repository.UserRepository
import com.app.convocial.utils.TokenManager
import com.app.convocial.utils.TokenRefreshManager
import com.google.gson.Gson
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

  @Singleton
  @Provides
  fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
    return TokenManager(context)
  }

  @Singleton
  @Provides
  fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
  }

  @Singleton
  @Provides
  fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(authInterceptor)
      .connectTimeout(30, TimeUnit.SECONDS) // Set connection timeout
      .readTimeout(30, TimeUnit.SECONDS) // Set read timeout
      .writeTimeout(30, TimeUnit.SECONDS) // Set write timeout
      .build()
  }

  @Provides
  @Singleton
  fun provideTokenRefreshManager(
    tokenManager: TokenManager,
    userDataSource: Lazy<UserDataSource>,
  ): TokenRefreshManager {
    return TokenRefreshManager(tokenManager, userDataSource)
  }

  @Singleton
  @Provides
  fun provideRetrofitBuilder(): Retrofit.Builder {
    return Retrofit.Builder()
      .baseUrl("https://slidee-vercel-test-iota.vercel.app/api/v1/")
      .addConverterFactory(GsonConverterFactory.create())
  }

  @Singleton
  @Provides
  fun provideApiService(retrofitBuilder: Retrofit.Builder): ApiService {
    return retrofitBuilder.build().create(ApiService::class.java)
  }

  @Singleton
  @Provides
  fun provideAuthenticatedApiService(
    retrofitBuilder: Retrofit.Builder,
    okHttpClient: OkHttpClient,
  ): AuthenticatedApiService {
    return retrofitBuilder.client(okHttpClient).build().create(AuthenticatedApiService::class.java)
  }

  @Singleton
  @Provides
  fun provideAuthInterceptor(
    tokenManager: TokenManager,
    userDataSource: Lazy<UserDataSource>,
  ): AuthInterceptor {
    return AuthInterceptor(tokenManager, userDataSource)
  }

  @Singleton
  @Provides
  fun provideUserDataSource(
    apiService: ApiService,
    authenticatedApiService: AuthenticatedApiService,
  ): UserDataSource {
    return UserDataSource(apiService, authenticatedApiService)
  }

  @Singleton
  @Provides
  fun provideCourseDataSource(authenticatedApiService: AuthenticatedApiService): CourseDataSource {
    return CourseDataSource(authenticatedApiService)
  }

  @Singleton
  @Provides
  fun providePostDataSource(authenticatedApiService: AuthenticatedApiService): PostDataSource {
    return PostDataSource(authenticatedApiService)
  }

  @Singleton
  @Provides
  fun providePostPagingSource(postRepository: PostRepository): PostPagingSource {
    return PostPagingSource(postRepository)
  }

  @Singleton
  @Provides
  fun provideUserPostPagingSource(
    postRepository: PostRepository,
    id: String,
  ): UserPostsPagingSource {
    return UserPostsPagingSource(postRepository, id)
  }

  @Singleton
  @Provides
  fun provideUserRepository(
    userRepositoryImplementation: UserRepositoryImplementation
  ): UserRepository {
    return userRepositoryImplementation
  }

  @Singleton
  @Provides
  fun providePostRepository(
    postRepositoryImplementation: PostRepositoryImplementation
  ): PostRepository {
    return postRepositoryImplementation
  }

  @Singleton
  @Provides
  fun provideCourseRepository(
    courseRepositoryImplementation: CourseRepositoryImplementation
  ): CourseRepository {
    return courseRepositoryImplementation
  }

  @Singleton
  @Provides
  fun provideGson(): Gson {
    return Gson()
  }

  @Singleton
  @Provides
  fun provideNotificationDataSource(apiService: AuthenticatedApiService): NotificationDataSource {
    return NotificationDataSource(apiService)
  }

  @Singleton
  @Provides
  fun provideNotificationRepository(
    notificationRepositoryImplementation: NotificationRepositoryImplementation
  ): NotificationRepository {
    return notificationRepositoryImplementation
  }

  @Singleton
  @Provides
  fun provideDataStore(
    @ApplicationContext context: Context
  ): androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> {
    return androidx.datastore.preferences
      .preferencesDataStore(name = "user_preferences")
      .getValue(context, String::javaClass)
  }
}
