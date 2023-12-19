package com.groupe5.moodmobile.fragments.Publication

import android.content.Context
import android.content.SharedPreferences
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.groupe5.moodmobile.databinding.PublicationInformationContentItemBinding
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubElement
import com.groupe5.moodmobile.repositories.IImageRepository
import com.groupe5.moodmobile.services.ImageService
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PublicationInformationElementRecyclerViewAdapter(
    private val context: Context,
    private val values: List<DtoInputPubElement>
) : RecyclerView.Adapter<PublicationInformationElementRecyclerViewAdapter.ViewHolder>() {
    lateinit var jwtToken: String
    private lateinit var imageRepository: IImageRepository
    private lateinit var imageService: ImageService
    lateinit var prefs: SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        prefs = context.getSharedPreferences("mood", Context.MODE_PRIVATE)
        jwtToken = prefs.getString("jwtToken", "") ?: ""
        imageRepository = RetrofitFactory.create(jwtToken, IImageRepository::class.java)
        imageService = ImageService(context, imageRepository)
        return ViewHolder(
            PublicationInformationContentItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )

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
                Picasso.with(holder.element.context).load(resourceId).into(holder.element)
            } else {
                Picasso.with(holder.element.context).load(image).into(holder.element)
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: PublicationInformationContentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val element = binding.imPublicationInformationElementElement
    }

}