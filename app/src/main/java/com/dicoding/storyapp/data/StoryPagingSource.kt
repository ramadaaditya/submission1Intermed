package com.dicoding.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyapp.data.remote.api.ApiService
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.response.MessageResponse
import com.google.gson.Gson
import retrofit2.HttpException

class StoryPagingSource(private val apiService: ApiService) : PagingSource<Int, ListStoryItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getStories(page, params.loadSize)
            val stories = response.listStory

            LoadResult.Page(
                data = stories,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (stories.isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            val errorBody = exception.response()?.errorBody()?.string()
            val message = Gson().fromJson(
                errorBody,
                MessageResponse::class.java
            ).message ?: "Error"
            LoadResult.Error(Exception(message))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}