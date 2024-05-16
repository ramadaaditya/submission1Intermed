package com.dicoding.storyapp.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.ui.MainViewModel
import com.dicoding.storyapp.R
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityHomeBinding
import com.dicoding.storyapp.ui.addStory.AddStoryActivity
import com.dicoding.storyapp.ui.welcome.WelcomeActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val adapter by lazy { HomeAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainHome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupViews()
        observeStories()

        supportActionBar?.show()
    }


    override fun onResume() {
        super.onResume()
        mainViewModel.getStories()
    }


    private fun setupViews() {
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter = this@HomeActivity.adapter
        }

        binding.buttonAddStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }


        binding.appBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.change_language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }

                R.id.button_logout -> {
                    mainViewModel.logout()
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                    true
                }

                else -> false
            }
        }
    }

    private fun handleStories(stories: ResultState<List<ListStoryItem>>) {
        when (stories) {
            is ResultState.Success -> showStories(stories.data)
            is ResultState.Error -> showError(stories.error)
            is ResultState.Loading -> showLoading(true)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showStories(storyList: List<ListStoryItem>) {
        showLoading(false)
        adapter.submitList(storyList)
    }

    private fun showError(errorMessage: String) {
        showLoading(false)
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }




    private fun observeStories() {
        mainViewModel.getStories().observe(this@HomeActivity) { stories ->
            handleStories(stories)
        }
    }
}