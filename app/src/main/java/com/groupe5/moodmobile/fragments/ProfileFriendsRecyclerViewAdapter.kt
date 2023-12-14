package com.groupe5.moodmobile.fragments

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.databinding.FragmentFriendItemBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.squareup.picasso.Picasso

class ProfileFriendsRecyclerViewAdapter(
    private val values: List<DtoInputFriend>
) : RecyclerView.Adapter<ProfileFriendsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentFriendItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        Picasso.with(holder.photo.context).load(R.drawable.logo).into(holder.photo)
        holder.name.text = item.name
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentFriendItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val photo = binding.ivProfileFriendUserImage
        val name: TextView = binding.tvProfileFriendUserUsername
    }

}