package com.groupe5.moodmobile.fragments.Publication.Comments

import android.content.Context
import android.content.SharedPreferences
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.groupe5.moodmobile.placeholder.PlaceholderContent.PlaceholderItem
import com.groupe5.moodmobile.databinding.FragmentPublicationInformationCommentItemBinding
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubComment
import com.groupe5.moodmobile.repositories.IImageRepository
import com.groupe5.moodmobile.services.ImageService
import com.groupe5.moodmobile.services.UserService
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PublicationInformationCommentRecyclerViewAdapter(
    private val context: Context,
    private val values: List<DtoInputPubComment>
) : RecyclerView.Adapter<PublicationInformationCommentRecyclerViewAdapter.ViewHolder>() {
    lateinit var jwtToken: String
    private lateinit var imageRepository: IImageRepository
    private lateinit var imageService: ImageService
    private lateinit var userService: UserService
    lateinit var prefs: SharedPreferences
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        prefs = context.getSharedPreferences("mood", Context.MODE_PRIVATE)
        jwtToken = prefs.getString("jwtToken", "") ?: ""
        imageRepository = RetrofitFactory.create(jwtToken, IImageRepository::class.java)
        imageService = ImageService(context, imageRepository)
        userService = UserService(context)

        return ViewHolder(
            FragmentPublicationInformationCommentItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        CoroutineScope(Dispatchers.Main).launch {
            val image = imageService.getImageById(item.idAuthorImage)
            if (image.startsWith("@drawable/")) {
                val resourceId = context.resources.getIdentifier(
                    image.substringAfterLast('/'),
                    "drawable",
                    context.packageName
                )
                Picasso.with(holder.userImage.context).load(resourceId).into(holder.userImage)
            } else {
                Picasso.with(holder.userImage.context).load(image).into(holder.userImage)
            }

            if(item.idAuthor == userService.getUserId()){
                holder.btnDeleteComment.visibility = View.VISIBLE
                holder.btnDeleteComment.isEnabled = true
            }
        }
        holder.username.text = item.nameAuthor
        holder.userComment.text = item.content
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentPublicationInformationCommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val userImage = binding.ivPublicationInormationCommentUserImage
        val username = binding.tvPublicationInormationCommentUserUsername
        val userComment = binding.tvPublicationInormationCommentUserComment
        val btnDeleteComment = binding.btnPublicationInormationCommentDeleteComment
    }

}