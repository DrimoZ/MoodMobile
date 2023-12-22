package com.groupe5.moodmobile.fragments.NewsFeed

import android.content.Context
import android.content.SharedPreferences
import android.text.format.DateUtils
import android.util.Log
import android.util.SparseArray
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.activities.MainActivity
import com.groupe5.moodmobile.classes.SharedViewModel

import com.groupe5.moodmobile.databinding.FragmentNewsFeedPublicationItemBinding
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubLike
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublicationInformation
import com.groupe5.moodmobile.dtos.Publication.Output.DtoOutputPubComment
import com.groupe5.moodmobile.dtos.Publication.PublicationViewState
import com.groupe5.moodmobile.fragments.Publication.Comments.PublicationInformationCommentManagerFragment
import com.groupe5.moodmobile.fragments.Publication.Element.PublicationInformationElementManagerFragment
import com.groupe5.moodmobile.repositories.IImageRepository
import com.groupe5.moodmobile.repositories.IPublicationRepository
import com.groupe5.moodmobile.services.ImageService
import com.groupe5.moodmobile.services.UserService
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFeedPublicationRecyclerViewAdapter(
    private val context: Context,
    private val activity: MainActivity,
    private val fragmentManager: FragmentManager,
    private val values: List<DtoInputPublicationInformation>,
    private val sharedViewModel: SharedViewModel,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<NewsFeedPublicationRecyclerViewAdapter.ViewHolder>() {
    lateinit var jwtToken: String
    private lateinit var imageRepository: IImageRepository
    private lateinit var publicationRepository: IPublicationRepository
    private lateinit var imageService: ImageService
    private lateinit var userService: UserService
    lateinit var prefs: SharedPreferences
    private val publicationViewStates: SparseArray<PublicationViewState> = SparseArray()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        prefs = context.getSharedPreferences("mood", Context.MODE_PRIVATE)
        jwtToken = prefs.getString("jwtToken", "") ?: ""
        imageRepository = RetrofitFactory.create(jwtToken, IImageRepository::class.java)
        publicationRepository = RetrofitFactory.create(jwtToken, IPublicationRepository::class.java)
        imageService = ImageService(context, imageRepository)
        userService = UserService(context)
        return ViewHolder(
            FragmentNewsFeedPublicationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val state = publicationViewStates.getOrPut(position) { PublicationViewState() }
        state.id = item.publicationId
        state.liked = item.hasConnectedLiked
        state.likeCount = item.likeCount
        state.commentCount = item.commentCount
        CoroutineScope(Dispatchers.Main).launch {
            val image = imageService.getImageById(item.authorImageId)
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
        }
        holder.username.text = item.authorName
        holder.content.text = item.publicationContent
        val currentDateMillis = System.currentTimeMillis()
        val itemDateMillis = item.publicationDate.time

        val timeAgo = DateUtils.getRelativeTimeSpanString(
            itemDateMillis,
            currentDateMillis,
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
        holder.date.text = timeAgo
        if(state.liked) {
            holder.likeImage.setImageDrawable(
                AppCompatResources.getDrawable(context, R.drawable.baseline_star_24))
            holder.likeText.text = "Liked ( ${state.likeCount} )"
        }else if(state.likeCount > 1) {
            holder.likeImage.setImageDrawable(
                AppCompatResources.getDrawable(context, R.drawable.baseline_star_border_purple500_24))
            holder.likeText.text = "Likes ( ${state.likeCount} )"
        }else {
            holder.likeImage.setImageDrawable(
                AppCompatResources.getDrawable(context, R.drawable.baseline_star_border_purple500_24))
            holder.likeText.text = "Like ( ${state.likeCount} )"
        }
        val commentText = if (item.commentCount > 1) "Comments" else "Comment"
        holder.commentText.text = "$commentText ( ${item.commentCount} )"

        startElements(holder, item)
        holder.likeImage.setOnClickListener {
            setPublicationLike(holder, item, state)
        }
        holder.likeText.setOnClickListener {
            setPublicationLike(holder, item, state)
        }
        holder.commentImage.setOnClickListener {
            if (state.displayComments){
                holder.commentsContainer.visibility = View.GONE
                holder.divider.visibility = View.GONE
                holder.commentsVisibility.visibility = View.GONE
                state.displayComments = !state.displayComments
            }else{
                holder.commentsContainer.visibility = View.VISIBLE
                holder.divider.visibility = View.VISIBLE
                holder.commentsVisibility.visibility = View.VISIBLE
                state.displayComments = !state.displayComments
            }
            startComments(holder, item)
        }
        holder.commentText.setOnClickListener {
            if (state.displayComments){
                holder.commentsContainer.visibility = View.GONE
                holder.divider.visibility = View.GONE
                holder.commentsVisibility.visibility = View.GONE
                state.displayComments = !state.displayComments
            }else{
                holder.commentsContainer.visibility = View.VISIBLE
                holder.divider.visibility = View.VISIBLE
                holder.commentsVisibility.visibility = View.VISIBLE
                state.displayComments = !state.displayComments
            }
            startComments(holder, item)
        }
        holder.btnSendComment.setOnClickListener {
            val comment = holder.editTextComment.text.toString()
            holder.editTextComment.text.clear()
            addPublicationComment(item, comment, state, holder)
        }
        sharedViewModel.numberCommentAfterDelete.observe(lifecycleOwner, Observer { observerId ->
            Log.e("",""+state.id)
            Log.e("",""+observerId)
            if(state.id == observerId){
                state.commentCount -= 1
                val commentText = if (state.commentCount > 1) "Comments" else "Comment"
                holder.commentText.text = "$commentText ( ${state.commentCount} )"
            }
        })
        holder.username.setOnClickListener {
            activity.lifecycleScope.launch {
                activity.onFriendClick(userService.getFriendDto(item.authorId))
             }
        }
    }
    private fun addPublicationComment(dtoP: DtoInputPublicationInformation, comment: String, state: PublicationViewState, holder: ViewHolder){
        val dto = DtoOutputPubComment(
            publicationId = state.id,
            commentContent = comment
        )
        val addCommentCall = publicationRepository.setPublicationComment(dto)
        addCommentCall.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    restartComments(holder, dtoP)
                    state.commentCount+=1
                    if(state.commentCount > 1){
                        holder.commentText.text = "Comments ( ${state.commentCount} )"
                    }else{
                        holder.commentText.text = "Comment ( ${state.commentCount} )"
                    }
                } else {
                    val message = "echec : ${response.message()}"
                    Log.d("Echec", message)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                val message = "Echec DB: ${t.message}"
                Log.e("EchecDb", message, t)
            }
        })
    }

    fun <T> SparseArray<T>.getOrPut(key: Int, defaultValue: () -> T): T {
        var value = get(key)
        if (value == null) {
            value = defaultValue()
            put(key, value)
        }
        return value
    }

    private fun startElements(holder: ViewHolder, dto: DtoInputPublicationInformation) {
        val id = dto.publicationId
        val uniqueContainerId = View.generateViewId()
        fragmentManager
            .beginTransaction()
            .add(
                uniqueContainerId,
                PublicationInformationElementManagerFragment.newInstance(dto),
                "PublicationInformationElementManagerFragment$id"
            )
            .commit()

        holder.elementsContainer.id = uniqueContainerId
    }

    private fun startComments(holder: ViewHolder, dto: DtoInputPublicationInformation) {
        val id = dto.publicationId
        val uniqueContainerId = View.generateViewId()
        fragmentManager
            .beginTransaction()
            .add(
                uniqueContainerId,
                PublicationInformationCommentManagerFragment.newInstance(dto.publicationId),
                "PublicationInformationCommentManagerFragment$id"
            )
            .commit()

        holder.commentsContainer.id = uniqueContainerId
    }

    private fun restartComments(holder: ViewHolder, dto: DtoInputPublicationInformation) {
        val id = dto.publicationId
        val uniqueContainerId = View.generateViewId()
        fragmentManager
            .beginTransaction()
            .replace(
                uniqueContainerId,
                PublicationInformationCommentManagerFragment.newInstance(dto.publicationId),
                "PublicationInformationCommentManagerFragment$id"
            )
            .commit()

        holder.commentsContainer.id = uniqueContainerId
    }

    private fun setPublicationLike(holder: ViewHolder, dto: DtoInputPublicationInformation, state: PublicationViewState){
        val dtoLike = DtoInputPubLike(
            publicationId = dto.publicationId,
            isLiked = !state.liked
        )
        val pubLikeCall = publicationRepository.setPublicationLike(dtoLike)
        pubLikeCall.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    state.liked = !state.liked
                    if(state.liked){
                        state.likeCount+=1
                    }else{
                        state.likeCount-=1
                    }
                    if(state.liked) {
                        holder.likeImage.setImageDrawable(
                            AppCompatResources.getDrawable(context, R.drawable.baseline_star_24))
                        holder.likeText.text = "Liked ( ${state.likeCount} )"
                    }else if(state.likeCount > 1) {
                        holder.likeImage.setImageDrawable(
                            AppCompatResources.getDrawable(context, R.drawable.baseline_star_border_purple500_24))
                        holder.likeText.text = "Likes ( ${state.likeCount} )"
                    }else {
                        holder.likeImage.setImageDrawable(
                            AppCompatResources.getDrawable(context, R.drawable.baseline_star_border_purple500_24))
                        holder.likeText.text = "Like ( ${state.likeCount} )"
                    }
                } else {
                    val message = "echec : ${response.message()}"
                    Log.d("Echec", message)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                val message = "Echec DB: ${t.message}"
                Log.e("EchecDb", message, t)
            }
        })
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentNewsFeedPublicationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var userImage = binding.ivNewsFeedPublicationInformationUserImage
        var username = binding.tvNewsFeedPublicationInformationUserUsername
        var content = binding.tvNewsFeedPublicationInformationContent
        var date = binding.tvNewsFeedPublicationInformationDate
        var likeImage = binding.imNewsFeedPublicationInformationLike
        var likeText = binding.tvNewsFeedPublicationInformationLike
        var commentImage = binding.imNewsFeedPublicationInformationComment
        var commentText = binding.tvNewsFeedPublicationInformationComment
        var elementsContainer = binding.fcvNewsFeedPublicationInformationPublications
        var commentsContainer = binding.fcvNewsFeedPublicationInformationComments
        var commentsVisibility = binding.llNewsFeedPublicationInformationComments
        var divider = binding.vNewsFeedPublicationInformationDivider
        var editTextComment = binding.etNewsFeedPublicationInformationWriteComment
        var btnSendComment = binding.btnNewsFeedPublicationInformationSendComment
    }

}