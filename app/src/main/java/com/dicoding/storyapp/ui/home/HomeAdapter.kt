package com.dicoding.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.ItemStoryBinding
import com.dicoding.storyapp.ui.detail.DetailActivity

class HomeAdapter : ListAdapter<ListStoryItem, HomeAdapter.MyViewHolder>(DIFF_CALLBACK) {
    inner class MyViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: ListStoryItem?) {
            with(binding) {
                val args = Bundle()
                args.putString(NAME_KEY, user?.name)
                tvItemName.text = user?.name
                tvDescription.text = user?.description
                Glide.with(root).load(user?.photoUrl).into(binding.ivItemPhoto)
                root.setOnClickListener {
                    val intentDetail = Intent(it.context, DetailActivity::class.java)
                    intentDetail.putExtra(DetailActivity.NAME_KEY, user?.name)
                    intentDetail.putExtra(DetailActivity.DESCRIPTION_KEY, user?.description)
                    intentDetail.putExtra(DetailActivity.PICTURE_KEY, user?.photoUrl)
                    binding.root.context.startActivity(intentDetail)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    companion object {
        const val NAME_KEY = "username"

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean =
                oldItem == newItem
        }
    }
}
