package com.dicoding.storyapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.repository.StoryRepository

class HomeViewModel (repository: StoryRepository): ViewModel(){
    val storyList: LiveData<PagingData<ListStoryItem>> =
        repository.getStories().cachedIn(viewModelScope)
}