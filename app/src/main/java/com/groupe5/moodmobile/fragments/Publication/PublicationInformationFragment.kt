package com.groupe5.moodmobile.fragments.Publication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.activities.MainActivity
import com.groupe5.moodmobile.databinding.FragmentPublicationInformationBinding
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPubLike
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublicationInformation
import com.groupe5.moodmobile.dtos.Publication.Output.DtoOutputPubComment
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


class PublicationInformationFragment(idPublication: Int) : Fragment() {
    private lateinit var binding : FragmentPublicationInformationBinding
    private lateinit var publicationRepository: IPublicationRepository
    private lateinit var imageRepository: IImageRepository
    private lateinit var imageService: ImageService
    private lateinit var userService: UserService
    var idPublication = idPublication
    var userId = ""
    var liked = false
    var likeCount = 0
    var commentDisplay = false
    var commentCount = 0
    var isFromConnected = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPublicationInformationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userService = UserService(requireContext())

        startPublicationInformationData()
        binding.btnFragmentPublicationInformationClose.setOnClickListener {
           (requireActivity() as MainActivity).closePublicationInformation()
        }

        binding.imFragmentPublicationInformationLike.setOnClickListener{
            setPublicationLike()
        }

        binding.tvFragmentPublicationInformationLike.setOnClickListener {
            setPublicationLike()
        }

        binding.imFragmentPublicationInformationComment.setOnClickListener{
            setPublicationDisplaysComments()
        }

        binding.tvFragmentPublicationInformationComment.setOnClickListener {
            setPublicationDisplaysComments()
        }
        binding.imFragmentPublicationInformationRemoveLike.setOnClickListener {
            setPublicationLike()
        }
        binding.tvFragmentPublicationInformationRemoveLike.setOnClickListener {
            setPublicationLike()
        }
        binding.imFragmentPublicationInformationRemoveComment.setOnClickListener{
            setPublicationDisplaysComments()
        }

        binding.tvFragmentPublicationInformationRemoveComment.setOnClickListener {
            setPublicationDisplaysComments()
        }
        binding.btnFragmentPublicationInformationSendComment.setOnClickListener {
            val comment = binding.etFragmentPublicationInformationWriteComment.text.toString()
            binding.etFragmentPublicationInformationWriteComment.text.clear()
            addPublicationComment(comment)
        }
        binding.imFragmentPublicationInformationRemove.setOnClickListener {
            deletePublication()
        }
        binding.tvFragmentPublicationInformationRemove.setOnClickListener {
            deletePublication()
        }
        binding.tvFragmentPublicationInformationUserUsername.setOnClickListener {
            lifecycleScope.launch {
                (requireActivity() as MainActivity).onFriendClick(userService.getFriendDto(userId))
            }
        }
    }

    private fun setPublicationDisplaysComments() {
        if(!commentDisplay){
            binding.imFragmentPublicationInformationRemoveCommentScroll.visibility = View.VISIBLE
            binding.imFragmentPublicationInformationCommentScroll.visibility = View.VISIBLE
            binding.llFragmentPublicationInformationComments.visibility = View.VISIBLE
            binding.divider3.visibility = View.VISIBLE
            binding.fcvFragmentPublicationInformationComments.visibility = View.VISIBLE
            commentDisplay = !commentDisplay
            startComments()
        }else{
            binding.imFragmentPublicationInformationRemoveCommentScroll.visibility = View.INVISIBLE
            binding.imFragmentPublicationInformationCommentScroll.visibility = View.INVISIBLE
            binding.llFragmentPublicationInformationComments.visibility = View.GONE
            binding.divider3.visibility = View.GONE
            binding.fcvFragmentPublicationInformationComments.visibility = View.GONE
            commentDisplay = !commentDisplay
        }
    }

    private fun startPublicationInformationData() {
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val jwtToken = prefs.getString("jwtToken", "") ?: ""
        publicationRepository = RetrofitFactory.create(jwtToken, IPublicationRepository::class.java)
        imageRepository = RetrofitFactory.create(jwtToken, IImageRepository::class.java)
        imageService = ImageService(requireContext(), imageRepository)

        val pubInfCall = publicationRepository.getPublicationInformation(idPublication)
        pubInfCall.enqueue(object : Callback<DtoInputPublicationInformation> {
            override fun onResponse(call: Call<DtoInputPublicationInformation>, response: Response<DtoInputPublicationInformation>) {
                if (response.isSuccessful) {
                    val userProfile = response.body()
                    userProfile?.let { up ->
                        userId = userProfile.idAuthor
                        liked = userProfile.hasConnectedLiked
                        likeCount = userProfile.likeCount
                        commentCount = userProfile.commentCount
                        isFromConnected = userProfile.isFromConnected
                        startElements(userProfile)
                        binding.tvFragmentPublicationInformationUserUsername.text = up.nameAuthor
                        binding.tvFragmentPublicationInformationContent.text = up.content

                        if(up.isFromConnected){
                            binding.llFragmentPublicationInformationNotRemove.visibility = View.GONE
                            binding.llFragmentPublicationInformationRemove.visibility = View.VISIBLE
                            if(up.hasConnectedLiked) {
                                binding.imFragmentPublicationInformationRemoveLike.setImageDrawable(
                                    AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_star_24))
                                binding.tvFragmentPublicationInformationRemoveLike.text = "Liked ( ${up.likeCount} )"
                            }else if(up.likeCount > 1) {
                                binding.imFragmentPublicationInformationRemoveLike.setImageDrawable(
                                    AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_star_border_purple500_24))
                                binding.tvFragmentPublicationInformationRemoveLike.text = "Likes ( ${up.likeCount} )"
                            }else {
                                binding.imFragmentPublicationInformationRemoveLike.setImageDrawable(
                                    AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_star_border_purple500_24))
                                binding.tvFragmentPublicationInformationRemoveLike.text = "Like ( ${up.likeCount} )"
                            }
                            if(up.commentCount > 1){
                                binding.tvFragmentPublicationInformationRemoveComment.text = "Comments ( ${up.commentCount} )"
                            }else{
                                binding.tvFragmentPublicationInformationRemoveComment.text = "Comment ( ${up.commentCount} )"
                            }
                        }else{
                            if(up.hasConnectedLiked) {
                                binding.imFragmentPublicationInformationLike.setImageDrawable(
                                    AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_star_24))
                                binding.tvFragmentPublicationInformationLike.text = "Liked ( ${up.likeCount} )"
                            }else if(up.likeCount > 1) {
                                binding.imFragmentPublicationInformationLike.setImageDrawable(
                                    AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_star_border_purple500_24))
                                binding.tvFragmentPublicationInformationLike.text = "Likes ( ${up.likeCount} )"
                            }else {
                                binding.imFragmentPublicationInformationLike.setImageDrawable(
                                    AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_star_border_purple500_24))
                                binding.tvFragmentPublicationInformationLike.text = "Like ( ${up.likeCount} )"
                            }
                            if(up.commentCount > 1){
                                binding.tvFragmentPublicationInformationComment.text = "Comments ( ${up.commentCount} )"
                            }else{
                                binding.tvFragmentPublicationInformationComment.text = "Comment ( ${up.commentCount} )"
                            }
                        }
                    }
                    val imageId = response.body()?.idAuthorImage
                    imageId?.let { id ->
                        CoroutineScope(Dispatchers.Main).launch {
                            val image = imageService.getImageById(id)
                            if (image.startsWith("@drawable/")) {
                                val resourceId = resources.getIdentifier(
                                    image.substringAfterLast('/'),
                                    "drawable",
                                    "com.groupe5.moodmobile"
                                )
                                Picasso.with(binding.ivFragmentPublicationInformationUserImage.context).load(resourceId).into(binding.ivFragmentPublicationInformationUserImage)
                            } else {
                                Picasso.with(binding.ivFragmentPublicationInformationUserImage.context).load(image).into(binding.ivFragmentPublicationInformationUserImage)
                            }
                        }
                    }
                } else {
                    val message = "echec : ${response.message()}"
                    Log.d("Echec", message)
                }
            }

            override fun onFailure(call: Call<DtoInputPublicationInformation>, t: Throwable) {
                val message = "Echec DB: ${t.message}"
                Log.e("EchecDb", message, t)
            }
        })
    }

    private fun setPublicationLike(){
        val dto = DtoInputPubLike(
            idPublication = idPublication,
            isLiked = !liked
        )
        val pubLikeCall = publicationRepository.setPublicationLike(dto)
        pubLikeCall.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    liked = !liked
                    if(liked){
                        likeCount+=1
                    }else{
                        likeCount-=1
                    }
                    if (isFromConnected){
                        if(liked) {
                            binding.imFragmentPublicationInformationRemoveLike.setImageDrawable(
                                AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_star_24))
                            binding.tvFragmentPublicationInformationRemoveLike.text = "Liked ( ${likeCount} )"
                        }else if(likeCount > 1) {
                            binding.imFragmentPublicationInformationRemoveLike.setImageDrawable(
                                AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_star_border_purple500_24))
                            binding.tvFragmentPublicationInformationRemoveLike.text = "Likes ( ${likeCount} )"
                        }else {
                            binding.imFragmentPublicationInformationRemoveLike.setImageDrawable(
                                AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_star_border_purple500_24))
                            binding.tvFragmentPublicationInformationRemoveLike.text = "Like ( ${likeCount} )"
                        }
                    }else{
                        if(liked) {
                            binding.imFragmentPublicationInformationLike.setImageDrawable(
                                AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_star_24))
                            binding.tvFragmentPublicationInformationLike.text = "Liked ( ${likeCount} )"
                        }else if(likeCount > 1) {
                            binding.imFragmentPublicationInformationLike.setImageDrawable(
                                AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_star_border_purple500_24))
                            binding.tvFragmentPublicationInformationLike.text = "Likes ( ${likeCount} )"
                        }else {
                            binding.imFragmentPublicationInformationLike.setImageDrawable(
                                AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_star_border_purple500_24))
                            binding.tvFragmentPublicationInformationLike.text = "Like ( ${likeCount} )"
                        }
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

    private fun deletePublication(){
        val addCommentCall =  publicationRepository.deletePublication(idPublication)
        addCommentCall.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    (requireActivity() as MainActivity).onRefreshUserProfile()
                    (requireActivity() as MainActivity).closePublicationInformation()
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

    private fun addPublicationComment(comment: String){
        val dto = DtoOutputPubComment(
            idPublication = idPublication,
            content = comment
        )
        val addCommentCall = publicationRepository.setPublicationComment(dto)
        addCommentCall.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    restartComments(false)
                    commentCount+=1
                    if(commentCount > 1){
                        binding.tvFragmentPublicationInformationComment.text = "Comments ( ${commentCount} )"
                    }else{
                        binding.tvFragmentPublicationInformationComment.text = "Comment ( ${commentCount} )"
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

    fun removePublicationComment(){
        commentCount-=1
        if(commentCount > 1){
            binding.tvFragmentPublicationInformationComment.text = "Comments ( ${commentCount} )"
        }else{
            binding.tvFragmentPublicationInformationComment.text = "Comment ( ${commentCount} )"
        }
    }

    private fun startElements(dto: DtoInputPublicationInformation){
        childFragmentManager
            .beginTransaction()
            .add(
                R.id.fcv_fragmentPublicationInformation_publications,
                PublicationInformationElementManagerFragment.newInstance(dto),
                "PublicationInformationElementManagerFragment"
            )
            .commit()
    }
    private fun startComments(){
        childFragmentManager
            .beginTransaction()
            .add(
                R.id.fcv_fragmentPublicationInformation_comments,
                PublicationInformationCommentManagerFragment.newInstance(idPublication),
                "PublicationInformationCommentManagerFragment"
            )
            .commit()
    }

    fun restartComments(delete: Boolean){
        childFragmentManager
            .beginTransaction()
            .replace(
                R.id.fcv_fragmentPublicationInformation_comments,
                PublicationInformationCommentManagerFragment.newInstance(idPublication),
                "PublicationInformationCommentManagerFragment"
            )
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(idPublication : Int) = PublicationInformationFragment(idPublication)
    }
}