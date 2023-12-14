package com.groupe5.moodmobile.fragments

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.databinding.ProfilePublicationItemBinding
import com.groupe5.moodmobile.dtos.Publication.DtoInputPublication
import com.squareup.picasso.Picasso

class ProfilePublicationsRecyclerViewAdapter(
    private val values: List<DtoInputPublication>
) : RecyclerView.Adapter<ProfilePublicationsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ProfilePublicationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        Picasso.with(holder.content.context).load(R.drawable.logo).into(holder.content)
        //Picasso.with(holder.content.context).load(item.content).into(holder.content)
        holder.content.contentDescription = item.content
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ProfilePublicationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val content = binding.imProfilePublicationItemContent
    }

}