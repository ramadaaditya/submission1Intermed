package com.dicoding.storyapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.UserModel
import com.dicoding.storyapp.preference.SettingPreferences
import kotlinx.coroutines.launch

class MainViewModel(private val pref : SettingPreferences) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return pref.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}