package com.dicoding.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyapp.preference.SettingPreferences
import com.dicoding.storyapp.data.UserModel
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.data.StoryPagingSource
import com.dicoding.storyapp.data.remote.api.ApiConfig
import com.dicoding.storyapp.data.remote.api.ApiService
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.remote.response.MessageResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val pref: SettingPreferences
) {
    private suspend fun saveSession(user: UserModel) {
        pref.saveSession(user)
    }

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): ResultState<MessageResponse> {
        ResultState.Loading
        return try {
            val response = apiService.registerUser(name, email, password)

            if (response.error == true) {
                ResultState.Error(response.message ?: "Unknown error")
            } else {
                ResultState.Success(response)
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, MessageResponse::class.java)
            val errorMessage = errorBody.message
            ResultState.Error(errorMessage.toString())
        }
    }

    suspend fun loginUser(email: String, password: String): ResultState<LoginResponse> {
        return try {
            val response = apiService.loginUser(email, password)
            val session = UserModel(
                name = response.loginResult.name,
                email = email,
                token = response.loginResult.token,
                isLogin = true
            )
            saveSession(session)
            ApiConfig.getApiService(session.token)
            ResultState.Success(response)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, MessageResponse::class.java)
            val errorMessage = errorBody.message ?: "Unknown error"
            ResultState.Error(errorMessage)
        }
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(apiService) }
        ).liveData
    }

    suspend fun getStoriesWithLocation() = apiService.getStoriesWithLocation()

    fun uploadStories(
        imageFile: File,
        description: String
    ): LiveData<ResultState<MessageResponse>> = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.postNewStory(multipartBody, requestBody)
            if (successResponse.error == true) {
                emit(ResultState.Error(successResponse.message ?: "Unknown error"))
            } else {
                emit(ResultState.Success(successResponse))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, MessageResponse::class.java)
            val errorMessage = errorBody.message ?: "Unknown error"
            emit(ResultState.Error(errorMessage))
        }
    }

    companion object {
        fun getInstance(
            apiService: ApiService,
            userPreference: SettingPreferences
        ): StoryRepository = StoryRepository(apiService, userPreference)
    }
}