package com.groupe5.moodmobile.fragments.Publication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
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
import com.groupe5.moodmobile.utils.RetrofitFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PublicationInformationFragment(idPublication: Int) : Fragment() {
    private lateinit var commentManagerFragment: PublicationInformationCommentManagerFragment
    private lateinit var binding : FragmentPublicationInformationBinding
    private lateinit var publicationRepository: IPublicationRepository
    private lateinit var imageRepository: IImageRepository
    private lateinit var imageService: ImageService
    var idPublication = idPublication
    var liked = false
    var likeCount = 0
    var commentDisplay = false
    var commentCount = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPublicationInformationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }

    private fun setPublicationDisplaysComments() {
        if(!commentDisplay){
            binding.llFragmentPublicationInformationComments.visibility = View.VISIBLE
            binding.divider3.visibility = View.VISIBLE
            binding.fcvFragmentPublicationInformationComments.visibility = View.VISIBLE
            commentDisplay = !commentDisplay
            startComments()
        }else{
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
                        liked = userProfile.hasConnectedLiked
                        likeCount = userProfile.likeCount
                        commentCount = userProfile.commentCount
                        startElements(userProfile)
                        binding.tvFragmentPublicationInformationUserUsername.text = up.nameAuthor
                        binding.tvFragmentPublicationInformationContent.text = up.content

                        if(up.isFromConnected){
                            binding.llFragmentPublicationInformationNotRemove.visibility = View.GONE
                            binding.llFragmentPublicationInformationRemove.visibility = View.VISIBLE
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