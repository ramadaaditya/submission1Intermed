package com.dicoding.storyapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.data.remote.response.MessageResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: StoryRepository) : ViewModel() {
    private val _responseResult = MutableLiveData<ResultState<MessageResponse>>()
    val responseResultState: LiveData<ResultState<MessageResponse>> get() = _responseResult

    fun registerUser(name: String, email: String, password: String) {
        _responseResult.value = ResultState.Loading

        viewModelScope.launch {
            val result = repository.register(name, email, password)
            _responseResult.value = result
        }
    }
}

