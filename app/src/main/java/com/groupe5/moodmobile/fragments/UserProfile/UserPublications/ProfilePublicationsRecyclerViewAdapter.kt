package com.groupe5.moodmobile.fragments.UserProfile.UserPublications

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.groupe5.moodmobile.databinding.ProfilePublicationItemBinding
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublication
import com.groupe5.moodmobile.repositories.IImageRepository
import com.groupe5.moodmobile.services.ImageService
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfilePublicationsRecyclerViewAdapter(
    private val context: Context,
    private val values: List<DtoInputPublication>
) : RecyclerView.Adapter<ProfilePublicationsRecyclerViewAdapter.ViewHolder>() {
    lateinit var jwtToken: String
    private lateinit var imageRepository: IImageRepository
    private lateinit var imageService: ImageService
    lateinit var prefs: SharedPreferences
    private var openClickListener: OnOpenClickListener? = null
    interface OnOpenClickListener {
        fun onOpenClick(imagePublication: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        prefs = context.getSharedPreferences("mood", Context.MODE_PRIVATE)
        jwtToken = prefs.getString("jwtToken", "") ?: ""
        imageRepository = RetrofitFactory.create(jwtToken, IImageRepository::class.java)
        imageService = ImageService(context, imageRepository)
        return ViewHolder(
            ProfilePublicationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun setOnOpenClickListener(listener: OnOpenClickListener) {
        openClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        if (item.elements.isNotEmpty()) {
            if(item.elements.size > 1){
                holder.moreContent.visibility = View.VISIBLE

            }
            val element = item.elements[0]

            CoroutineScope(Dispatchers.Main).launch {
                val image = imageService.getImageById(element.imageId)
                if (image.startsWith("@drawable/")) {
                    val resourceId = context.resources.getIdentifier(
                        image.substringAfterLast('/'),
                        "drawable",
                        context.packageName
                    )
                    Picasso.with(holder.content.context).load(resourceId).into(holder.content)
                } else {
                    Picasso.with(holder.content.context).load(image).into(holder.content)
                }
            }
        } else {
            val resourceId = context.resources.getIdentifier(
                "no_publication_picture",
                "drawable",
                context.packageName
            )
            Picasso.with(holder.content.context).load(resourceId).into(holder.content)
        }

        holder.content.setOnClickListener {
            openClickListener?.onOpenClick(item.publicationId)
        }

        holder.moreContent.setOnClickListener {
            openClickListener?.onOpenClick(item.publicationId)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: ProfilePublicationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val content = binding.imProfilePublicationItemContent
        val moreContent = binding.imProfilePublicationItemMoreContent
    }
}
