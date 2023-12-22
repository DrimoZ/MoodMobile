package com.groupe5.moodmobile.fragments.More.Notifications

import android.content.Context
import android.content.SharedPreferences
import android.opengl.Visibility
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible

import com.groupe5.moodmobile.placeholder.PlaceholderContent.PlaceholderItem
import com.groupe5.moodmobile.databinding.FriendRequestsItemBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriendRequest
import com.groupe5.moodmobile.fragments.Discover.Users.DiscoverUsersRecyclerViewAdapter
import com.groupe5.moodmobile.repositories.IImageRepository
import com.groupe5.moodmobile.services.ImageService
import com.groupe5.moodmobile.services.UserService
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendRequestsRecyclerViewAdapter(
    private val context: Context,
    private val values: List<DtoInputFriendRequest>
) : RecyclerView.Adapter<FriendRequestsRecyclerViewAdapter.ViewHolder>() {
    lateinit var jwtToken: String
    private lateinit var imageRepository: IImageRepository
    private lateinit var imageService: ImageService
    private lateinit var userService: UserService
    lateinit var prefs: SharedPreferences
    private var acceptClickListener: FriendRequestsRecyclerViewAdapter.OnAcceptClickListener? = null
    private var rejectClickListener: FriendRequestsRecyclerViewAdapter.OnRejectClickListener? = null
    private var userClickListener: FriendRequestsRecyclerViewAdapter.OnUserClickListener? = null

    interface OnAcceptClickListener {
        fun onAcceptClick(user: DtoInputFriendRequest)
    }
    interface OnRejectClickListener {
        fun onRejectClick(user: DtoInputFriendRequest)
    }
    interface OnUserClickListener {
        fun onUserClick(user: DtoInputFriendRequest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        prefs = context.getSharedPreferences("mood", Context.MODE_PRIVATE)
        jwtToken = prefs.getString("jwtToken", "") ?: ""
        imageRepository = RetrofitFactory.create(jwtToken, IImageRepository::class.java)
        imageService = ImageService(context, imageRepository)
        userService = UserService(context)
        return ViewHolder(
            FriendRequestsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    fun setOnAcceptClickListener(listener: FriendRequestsRecyclerViewAdapter.OnAcceptClickListener) {
        acceptClickListener = listener
    }
    fun setOnRejectClickListener(listener: FriendRequestsRecyclerViewAdapter.OnRejectClickListener) {
        rejectClickListener = listener
    }
    fun setOnUserClickListener(listener: FriendRequestsRecyclerViewAdapter.OnUserClickListener) {
        userClickListener = listener
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        CoroutineScope(Dispatchers.Main).launch {
            val image = imageService.getImageById(item.imageId)
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
        holder.name.text = item.userName
        if(item.isFriendWithConnected == 1 && item.isDone == false){
            holder.response.visibility = View.GONE

        }else{
            holder.acceptButton.visibility = View.GONE
            holder.rejectButton.visibility = View.GONE
            holder.response.visibility = View.VISIBLE
            if(item.isAccepted){
                holder.response.text = "Accepted"
            }else{
                holder.response.text = "Rejected"
            }
        }
        holder.name.setOnClickListener {
            userClickListener?.onUserClick(item)
        }
        holder.acceptButton.setOnClickListener {
            acceptClickListener?.onAcceptClick(item)
        }
        holder.rejectButton.setOnClickListener {
            rejectClickListener?.onRejectClick(item)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FriendRequestsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val photo = binding.ivFriendRequestUserImage
        val name: TextView = binding.tvFriendRequestUserUsername
        val acceptButton = binding.btnFriendRequestAcceptFriend
        val rejectButton = binding.btnFriendRequestRejectFriend
        val response = binding.tvFriendRequestResponse
    }

}