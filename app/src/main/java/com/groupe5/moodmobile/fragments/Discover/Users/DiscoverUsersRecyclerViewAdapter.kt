package com.groupe5.moodmobile.fragments.Discover.Users

import android.content.Context
import android.content.SharedPreferences
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.groupe5.moodmobile.databinding.FragmentDiscoverUsersItemBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.repositories.IImageRepository
import com.groupe5.moodmobile.services.ImageService
import com.groupe5.moodmobile.services.UserService
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiscoverUsersRecyclerViewAdapter(
    private val context: Context,
    private val values: List<DtoInputFriend>
) : RecyclerView.Adapter<DiscoverUsersRecyclerViewAdapter.ViewHolder>() {
    lateinit var jwtToken: String
    private lateinit var imageRepository: IImageRepository
    private lateinit var imageService: ImageService
    private lateinit var userService: UserService
    lateinit var prefs: SharedPreferences
    private var deleteClickListener: OnDeleteClickListener? = null
    private var addClickListener: OnAddClickListener? = null
    private var cancelClickListener: OnCancelClickListener? = null
    private var acceptClickListener: OnAcceptClickListener? = null
    private var rejectClickListener: OnRejectClickListener? = null
    private var userClickListener: OnUserClickListener? = null

    interface OnDeleteClickListener {
        fun onDeleteClick(user: DtoInputFriend)
    }
    interface OnAddClickListener {
        fun onAddClick(user: DtoInputFriend)
    }
    interface OnCancelClickListener {
        fun onCancelClick(user: DtoInputFriend)
    }
    interface OnAcceptClickListener {
        fun onAcceptClick(user: DtoInputFriend)
    }
    interface OnRejectClickListener {
        fun onRejectClick(user: DtoInputFriend)
    }
    interface OnUserClickListener {
        fun onUserClick(user: DtoInputFriend)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        prefs = context.getSharedPreferences("mood", Context.MODE_PRIVATE)
        jwtToken = prefs.getString("jwtToken", "") ?: ""
        imageRepository = RetrofitFactory.create(jwtToken, IImageRepository::class.java)
        imageService = ImageService(context, imageRepository)
        userService = UserService(context)
        return ViewHolder(
            FragmentDiscoverUsersItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        deleteClickListener = listener
    }
    fun setOnAddClickListener(listener: OnAddClickListener) {
        addClickListener = listener
    }
    fun setOnCancelClickListener(listener: OnCancelClickListener) {
        cancelClickListener = listener
    }
    fun setOnAcceptClickListener(listener: OnAcceptClickListener) {
        acceptClickListener = listener
    }
    fun setOnRejectClickListener(listener: OnRejectClickListener) {
        rejectClickListener = listener
    }
    fun setOnUserClickListener(listener: OnUserClickListener) {
        userClickListener = listener
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
            if(userService.getUserId() == item.id){
                holder.delButton.isEnabled = false
                holder.delButton.visibility = View.INVISIBLE
                holder.addButton.isEnabled = false
                holder.addButton.visibility = View.INVISIBLE
            }
            if(item.isFriendWithConnected == 2){
                holder.delButton.isEnabled = true
                holder.delButton.visibility = View.VISIBLE
            }
            if(item.isFriendWithConnected == 0){
                holder.cancelButton.isEnabled = true
                holder.cancelButton.visibility = View.VISIBLE
            }
            if(item.isFriendWithConnected == 1){
                holder.acceptButton.isEnabled = true
                holder.acceptButton.visibility = View.VISIBLE
                holder.rejectButton.isEnabled = true
                holder.rejectButton.visibility = View.VISIBLE
            }
            if(item.isFriendWithConnected == -1 && userService.getUserId() != item.id){
                holder.addButton.isEnabled = true
                holder.addButton.visibility = View.VISIBLE
            }
        }
        holder.name.text = item.name
        if(item.commonFriendCount > 1){
            holder.commonFriend.text = "${item.commonFriendCount} Friends in common"
        }else if(item.commonFriendCount == 1){
            holder.commonFriend.text = "${item.commonFriendCount} Friend in common"
        }
        holder.name.setOnClickListener {
            userClickListener?.onUserClick(item)
        }
        holder.delButton.setOnClickListener {
            deleteClickListener?.onDeleteClick(item)
        }
        holder.addButton.setOnClickListener {
            addClickListener?.onAddClick(item)
        }
        holder.cancelButton.setOnClickListener {
            cancelClickListener?.onCancelClick(item)
        }
        holder.acceptButton.setOnClickListener {
            acceptClickListener?.onAcceptClick(item)
        }
        holder.rejectButton.setOnClickListener {
            rejectClickListener?.onRejectClick(item)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentDiscoverUsersItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val photo = binding.ivDiscoverUsersUserImage
        val name: TextView = binding.tvDiscoverUsersUserUsername
        val commonFriend: TextView = binding.tvDiscoverUsersUserCommonFriends
        val delButton = binding.btnDiscoverUsersDeleteFriend
        val addButton = binding.btnDiscoverUsersAddFriend
        val cancelButton = binding.btnDiscoverUsersCancelFriendRequest
        val acceptButton = binding.btnDiscoverUsersAcceptFriend
        val rejectButton = binding.btnDiscoverUsersRejectFriend
    }

}