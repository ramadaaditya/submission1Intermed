package com.dicoding.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private var _loginResult = MutableLiveData<ResultState<LoginResponse>>()
    val loginResult: LiveData<ResultState<LoginResponse>> get() = _loginResult

    fun loginUser(email: String, password: String) {
        _loginResult.value = ResultState.Loading

        viewModelScope.launch {
            val result = storyRepository.loginUser(email, password)
            _loginResult.value = result
        }
    }
}
