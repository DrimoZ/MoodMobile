package com.groupe5.moodmobile.fragments.UserProfile.UserFriends

import android.content.Context
import android.content.SharedPreferences
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.groupe5.moodmobile.databinding.FragmentFriendItemBinding
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.repositories.IImageRepository
import com.groupe5.moodmobile.services.ImageService
import com.groupe5.moodmobile.services.UserService
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
    private lateinit var userService: UserService
    lateinit var prefs: SharedPreferences
    private var deleteClickListener: OnDeleteClickListener? = null
    private var addClickListener: OnAddClickListener? = null
    private var cancelClickListener: OnCancelClickListener? = null
    private var acceptClickListener: OnAcceptClickListener? = null
    private var rejectClickListener: OnRejectClickListener? = null
    private var friendClickListener: OnFriendClickListener? = null

    interface OnDeleteClickListener {
        fun onDeleteClick(friend: DtoInputFriend)
    }
    interface OnAddClickListener {
        fun onAddClick(friend: DtoInputFriend)
    }
    interface OnCancelClickListener {
        fun onCancelClick(friend: DtoInputFriend)
    }
    interface OnAcceptClickListener {
        fun onAcceptClick(friend: DtoInputFriend)
    }
    interface OnRejectClickListener {
        fun onRejectClick(friend: DtoInputFriend)
    }
    interface OnFriendClickListener {
        fun onFriendClick(friend: DtoInputFriend)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        prefs = context.getSharedPreferences("mood", Context.MODE_PRIVATE)
        jwtToken = prefs.getString("jwtToken", "") ?: ""
        imageRepository = RetrofitFactory.create(jwtToken, IImageRepository::class.java)
        imageService = ImageService(context, imageRepository)
        userService = UserService(context)
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
    fun setOnFriendClickListener(listener: OnFriendClickListener) {
        friendClickListener = listener
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
            if(userService.getUserId() == item.userId){
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
            if(item.isFriendWithConnected == -1 && userService.getUserId() != item.userId){
                holder.addButton.isEnabled = true
                holder.addButton.visibility = View.VISIBLE
            }
        }
        holder.name.text = item.userName
        if(item.commonFriendCount > 1){
            holder.commonFriend.text = "${item.commonFriendCount} Friends in common"
        }else if(item.commonFriendCount == 1){
            holder.commonFriend.text = "${item.commonFriendCount} Friend in common"
        }
        holder.name.setOnClickListener {
            friendClickListener?.onFriendClick(item)
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

    inner class ViewHolder(binding: FragmentFriendItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val photo = binding.ivProfileFriendUserImage
        val name: TextView = binding.tvProfileFriendUserUsername
        val commonFriend: TextView = binding.tvProfileFriendUserCommonFriends
        val delButton = binding.btnProfileFriendDeleteFriend
        val addButton = binding.btnProfileFriendAddFriend
        val cancelButton = binding.btnProfileFriendCancelFriendRequest
        val acceptButton = binding.btnProfileFriendAcceptFriend
        val rejectButton = binding.btnProfileFriendRejectFriend
    }
}
