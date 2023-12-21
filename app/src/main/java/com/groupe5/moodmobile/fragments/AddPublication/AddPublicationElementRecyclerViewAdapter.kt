package com.groupe5.moodmobile.fragments.AddPublication

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.groupe5.moodmobile.databinding.AddPublicationElementItemBinding
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubComment
import com.groupe5.moodmobile.fragments.Publication.Comments.PublicationInformationCommentRecyclerViewAdapter
import com.groupe5.moodmobile.repositories.IImageRepository
import com.groupe5.moodmobile.services.ImageService
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso

class AddPublicationElementRecyclerViewAdapter(
    private val context: Context,
    private val values: List<Uri>
) : RecyclerView.Adapter<AddPublicationElementRecyclerViewAdapter.ViewHolder>() {
    lateinit var jwtToken: String
    private lateinit var imageRepository: IImageRepository
    private lateinit var imageService: ImageService
    lateinit var prefs: SharedPreferences
    private var deleteClickListener: AddPublicationElementRecyclerViewAdapter.OnDeleteClickListener? = null
    interface OnDeleteClickListener {
        fun onDeleteClick(image: Uri)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        prefs = context.getSharedPreferences("mood", Context.MODE_PRIVATE)
        jwtToken = prefs.getString("jwtToken", "") ?: ""
        imageRepository = RetrofitFactory.create(jwtToken, IImageRepository::class.java)
        imageService = ImageService(context, imageRepository)
        return ViewHolder(
            AddPublicationElementItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    fun setOnDeleteClickListener(listener: AddPublicationElementRecyclerViewAdapter.OnDeleteClickListener) {
        deleteClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        Picasso.with(holder.element.context).load(item).into(holder.element)
        holder.deleteElement.setOnClickListener {
            deleteClickListener?.onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: AddPublicationElementItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val element = binding.imAddPublicationElementElement
        val deleteElement = binding.imAddPublicationElementDeleteElement
    }

}