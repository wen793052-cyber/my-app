package com.example.data.sync

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

data class UserProfileDto(
    val username: String = "",
    val password: String = "",
    val nickname: String = "",
    val avatarUrl: String = "",
    val bio: String = "用文字记录温暖的生活片段~",
    val registeredAt: Long = System.currentTimeMillis()
)

data class FriendRequestDto(
    val requestId: String = "",
    val fromUsername: String = "",
    val fromNickname: String = "",
    val fromAvatarUrl: String = "",
    val toUsername: String = "",
    val message: String = "想和你一起互相鼓励记日记~",
    val status: String = "PENDING", // PENDING, ACCEPTED, REJECTED
    val timestamp: Long = System.currentTimeMillis()
)

data class CloudFriendDto(
    val friendUsername: String = "",
    val nickname: String = "",
    val avatarUrl: String = "",
    val bio: String = "",
    val addedAt: Long = System.currentTimeMillis()
)

data class AppVersionDto(
    val versionCode: Int = 1,
    val versionName: String = "1.0.0",
    val updateNotes: String = "全新版本！支持账号注册登录、云端加好友与日记同步。",
    val downloadUrl: String = "https://ais-pre-iz7jylpf7me2rsamwkmtbd-641962143261.asia-northeast1.run.app",
    val forceUpdate: Boolean = false
)

data class CloudDiaryDto(
    val cloudId: String = "",
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val formattedDate: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val mood: String = "😊 充实",
    val weather: String = "☀️ 晴朗",
    val imageUris: String = "",
    val tags: String = "",
    val location: String = "",
    val privacyLevel: String = "PRIVATE"
)

interface CloudApi {
    // User Profile APIs
    @GET("users/{username}.json")
    suspend fun getUserProfile(@Path("username") username: String): UserProfileDto?

    @PUT("users/{username}.json")
    suspend fun saveUserProfile(
        @Path("username") username: String,
        @Body profile: UserProfileDto
    ): UserProfileDto

    // Search Users
    @GET("users.json")
    suspend fun getAllUsers(): Map<String, UserProfileDto>?

    // Friend Request APIs
    @PUT("requests/{toUsername}/{requestId}.json")
    suspend fun sendFriendRequest(
        @Path("toUsername") toUsername: String,
        @Path("requestId") requestId: String,
        @Body request: FriendRequestDto
    ): FriendRequestDto

    @GET("requests/{toUsername}.json")
    suspend fun getIncomingRequests(@Path("toUsername") toUsername: String): Map<String, FriendRequestDto>?

    @DELETE("requests/{toUsername}/{requestId}.json")
    suspend fun deleteFriendRequest(
        @Path("toUsername") toUsername: String,
        @Path("requestId") requestId: String
    )

    // Friends Relationship APIs
    @PUT("friends/{username}/{friendUsername}.json")
    suspend fun addFriendRelation(
        @Path("username") username: String,
        @Path("friendUsername") friendUsername: String,
        @Body friend: CloudFriendDto
    ): CloudFriendDto

    @GET("friends/{username}.json")
    suspend fun getFriendsList(@Path("username") username: String): Map<String, CloudFriendDto>?

    @DELETE("friends/{username}/{friendUsername}.json")
    suspend fun removeFriendRelation(
        @Path("username") username: String,
        @Path("friendUsername") friendUsername: String
    )

    // Diary Cloud Backup & Restore APIs
    @PUT("diaries/{username}/{cloudId}.json")
    suspend fun saveUserDiary(
        @Path("username") username: String,
        @Path("cloudId") cloudId: String,
        @Body diary: CloudDiaryDto
    ): CloudDiaryDto

    @GET("diaries/{username}.json")
    suspend fun getUserDiaries(
        @Path("username") username: String
    ): Map<String, CloudDiaryDto>?

    @DELETE("diaries/{username}/{cloudId}.json")
    suspend fun deleteUserDiary(
        @Path("username") username: String,
        @Path("cloudId") cloudId: String
    )

    // App Version Check API
    @GET("app_version.json")
    suspend fun getLatestAppVersion(): AppVersionDto?
}

object CloudNetworkClient {
    // Using a shared, free Firebase Realtime Database cloud REST endpoint for seamless out-of-the-box sync
    private const val BASE_URL = "https://warmjournal-app-default-rtdb.asia-southeast1.firebasedatabase.app/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val api: CloudApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(CloudApi::class.java)
    }
}
