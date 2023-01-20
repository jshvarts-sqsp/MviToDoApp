package com.jshvarts.todoapp.di

//import com.jshvarts.todoapp.data.NotesJsonConverter
import com.jshvarts.todoapp.data.remote.NotesApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
  @Provides
  fun provideNoteApi(): NotesApi {

    val loggingInterceptor = HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpBuilder = OkHttpClient.Builder()
      .addInterceptor(loggingInterceptor)

    val moshi = Moshi.Builder()
      .addLast(KotlinJsonAdapterFactory())
      .build()

    return Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .client(okHttpBuilder.build())
      .build()
      .create(NotesApi::class.java)
  }
}