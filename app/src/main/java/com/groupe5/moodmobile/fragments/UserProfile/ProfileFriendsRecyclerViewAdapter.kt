package com.groupe5.moodmobile.fragments.UserProfile

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.databinding.FragmentFriendItemBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.repositories.IImageRepository
import com.groupe5.moodmobile.services.ImageService
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFriendsRecyclerViewAdapter(
    private val context: Context,
    private val values: List<DtoInputFriend>
) : RecyclerView.Adapter<ProfileFriendsRecyclerViewAdapter.ViewHolder>() {
    lateinit var jwtToken: String
    private lateinit var imageRepository: IImageRepository
    private lateinit var imageService: ImageService
    lateinit var prefs: SharedPreferences
    private var deleteClickListener: OnDeleteClickListener? = null
    private var friendClickListener: OnFriendClickListener? = null
    interface OnDeleteClickListener {
        fun onDeleteClick(friend: DtoInputFriend)
    }
    interface OnFriendClickListener {
        fun onFriendClick(friend: DtoInputFriend)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        prefs = context.getSharedPreferences("mood", Context.MODE_PRIVATE)
        jwtToken = prefs.getString("jwtToken", "") ?: ""
        imageRepository = RetrofitFactory.create(jwtToken, IImageRepository::class.java)
        imageService = ImageService(context, imageRepository)
        return ViewHolder(
            FragmentFriendItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        deleteClickListener = listener
    }
    fun setOnFriendClickListener(listener: OnFriendClickListener) {
        friendClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        CoroutineScope(Dispatchers.Main).launch {
            val image = imageService.getImageById(item.idImage)
            if (image.startsWith("@drawable/")) {
                val resourceId = context.resources.getIdentifier(
                    image.substringAfterLast('/'),
                    "drawable",
                    context.packageName
                )
                Picasso.with(holder.photo.context).load(resourceId).into(holder.photo)
            } else {
                Picasso.with(holder.photo.context).load(image).into(holder.photo)
            }
        }
        holder.name.text = item.name
        holder.button.setOnClickListener {
            deleteClickListener?.onDeleteClick(item)
        }
        holder.name.setOnClickListener {
            friendClickListener?.onFriendClick(item)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentFriendItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val photo = binding.ivProfileFriendUserImage
        val name: TextView = binding.tvProfileFriendUserUsername
        val button = binding.btnProfileFriendDeleteFriend
    }
}
