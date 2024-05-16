package com.dicoding.storyapp.ui.addStory

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository
import java.io.File

class AddStoryViewModel(private val storyRepository: StoryRepository):ViewModel() {
    fun uploadStories(file: File, description: String) =
        storyRepository.uploadStories(file, description)
}