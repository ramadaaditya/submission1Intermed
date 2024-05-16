package com.dicoding.storyapp.ui.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = intent.getStringExtra(NAME_KEY)
        val description = intent.getStringExtra(DESCRIPTION_KEY)
        val picture = intent.getStringExtra(PICTURE_KEY)

        name?.let { binding.tvDetailName.text = it }
        description?.let { binding.tvDetailDescription.text = it }
        picture?.let { Glide.with(this).load(it).into(binding.ivDetailPhoto) }
    }

    companion object {
        const val NAME_KEY = "name"
        const val DESCRIPTION_KEY = "description"
        const val PICTURE_KEY = "picture"

    }
}