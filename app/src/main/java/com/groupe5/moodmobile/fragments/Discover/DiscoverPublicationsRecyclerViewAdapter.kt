package com.groupe5.moodmobile.fragments.Discover

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.groupe5.moodmobile.placeholder.PlaceholderContent.PlaceholderItem
import com.groupe5.moodmobile.databinding.FragmentDiscoverPublicationsItemBinding
class DiscoverPublicationsRecyclerViewAdapter(
    private val values: List<PlaceholderItem>
) : RecyclerView.Adapter<DiscoverPublicationsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentDiscoverPublicationsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentDiscoverPublicationsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image = binding.imDiscoverPublicationItemContent
    }

}