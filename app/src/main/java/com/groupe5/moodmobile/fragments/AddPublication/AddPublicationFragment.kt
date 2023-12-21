package com.groupe5.moodmobile.fragments.AddPublication

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import com.groupe5.moodmobile.R
import com.groupe5.moodmobile.classes.SharedViewModel
import com.groupe5.moodmobile.databinding.FragmentAddPublicationBinding
import com.groupe5.moodmobile.repositories.IPublicationRepository
import com.groupe5.moodmobile.utils.RetrofitFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddPublicationFragment : Fragment() {
    private lateinit var binding: FragmentAddPublicationBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var publicationRepository: IPublicationRepository

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data

                imageUri?.let {
                    val list = sharedViewModel.mutableElementLiveData.value ?: mutableListOf()
                    list.add(imageUri)
                    sharedViewModel.mutableElementLiveData.value = list
                    sharedViewModel.mutableElementLiveData.observe(viewLifecycleOwner) { newList ->
                        if(newList != null){
                            val size = newList.size
                            if(size == 4){
                                binding.btnFragmentAddPublicationSelectImages.visibility = View.GONE
                                binding.tvFragmentAddPublicationMaximum.visibility = View.VISIBLE
                            }else{
                                binding.btnFragmentAddPublicationSelectImages.visibility = View.VISIBLE
                                binding.tvFragmentAddPublicationMaximum.visibility = View.GONE
                            }
                            binding.tvFragmentAddPublicationNbImage.text = "$size/4"
                        }else{
                            binding.tvFragmentAddPublicationNbImage.text = "0/4"
                        }
                    }
                    childFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.fcv_fragmentAddPublication_imagePreview,
                            AddPublicationElementManagerFragment.newInstance(list),
                            "AddPublicationElementManagerFragment"
                        )
                        .commit()
                }
            } else {
                Log.e("ImagePickerLauncher", "Image picking canceled or failed")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPublicationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireActivity().getSharedPreferences("mood", Context.MODE_PRIVATE)
        val jwtToken = prefs.getString("jwtToken", "") ?: ""
        publicationRepository = RetrofitFactory.create(jwtToken, IPublicationRepository::class.java)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        binding.btnFragmentAddPublicationSelectImages.setOnClickListener {
            val image = ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(500, 500)
                .createIntent { intent ->
                    imagePickerLauncher.launch(intent)
                }
        }
        binding.btnFragmentAddPublicationCreatePublication.setOnClickListener {
            val elementList = sharedViewModel.mutableElementLiveData.value

            if (elementList != null) {
                val desc = binding.etFragmentAddPublicationDescriptionText.text.toString()

                val imageParts: MutableList<MultipartBody.Part> = mutableListOf()

                elementList.forEach {
                    val imageFile = File(it.path)

                    val requestFile: RequestBody =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), imageFile)

                    val imagePart: MultipartBody.Part =
                        MultipartBody.Part.createFormData("images", imageFile.name, requestFile)

                    imageParts.add(imagePart)
                }

                val description: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), desc)

                val call: Call<Void> = publicationRepository.setNewUserPublucation(imageParts, description)

                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        binding.etFragmentAddPublicationDescriptionText.text.clear()
                        sharedViewModel.mutableElementLiveData.value = null
                        val addPublicationElementManagerFragment =
                            childFragmentManager.findFragmentByTag("AddPublicationElementManagerFragment")
                        if (addPublicationElementManagerFragment != null) {
                            childFragmentManager
                                .beginTransaction()
                                .remove(addPublicationElementManagerFragment)
                                .commit()
                        }
                        Toast.makeText(context, "New publication uploaded", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                    }
                })
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddPublicationFragment()
    }
}