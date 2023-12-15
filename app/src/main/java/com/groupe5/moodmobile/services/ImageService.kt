package com.groupe5.moodmobile.services

import android.content.Context
import android.util.Base64
import android.util.Log
import com.groupe5.moodmobile.dtos.Image.DtoInputImage
import com.groupe5.moodmobile.repositories.IImageRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ImageService(private val context: Context, private val imageRepository: IImageRepository) {

    suspend fun getImageById(imageId: Int): String = suspendCoroutine { continuation ->
        if(imageId == -1){
            continuation.resume("@drawable/no_publication_picture")
        }
        if(imageId == 1 || imageId == 2){
            continuation.resume("@drawable/no_profile_picture")
        }
        else{
            val callImage = imageRepository.getImageData(imageId)
            callImage.enqueue(object : Callback<DtoInputImage> {
                override fun onResponse(call: Call<DtoInputImage>, response: Response<DtoInputImage>) {
                    if (response.isSuccessful) {
                        val imageData = response.body()?.data ?: ""
                        if (imageData.isNotEmpty()) {
                            val image = imageToURL(imageData)
                            println("image recue : " + image)
                            continuation.resume(image)
                        } else {
                            Log.d("erreur", "imageData est null ou vide")
                            continuation.resumeWithException(Exception("imageData est null ou vide"))
                        }
                    } else {
                        Log.d("erreur", "response : "+response)
                        continuation.resumeWithException(Exception("Erreur de réponse"))
                    }
                }

                override fun onFailure(call: Call<DtoInputImage>, t: Throwable) {
                    Log.d("erreur", "response : "+t)
                    continuation.resumeWithException(t)
                }
            })
        }

    }

    private fun imageToURL(image: String): String {
        // Convert the string to a byte array
        val bytes = image.toByteArray()

        // Create an InputStream from the byte array
        val byteArrayInputStream = ByteArrayInputStream(bytes)

        // Create a Blob from the InputStream
        val blob = byteArrayInputStream.readBytes()

        // Decode the Blob
        val decodedBytes: ByteArray = Base64.decode(blob, Base64.DEFAULT)

        // Create a file in the application's temporary files directory
        val directory = File(context.filesDir, "images")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        // Generate a random file name
        val randomFileName = UUID.randomUUID().toString() + ".png"
        val imageFile = File(directory, randomFileName)

        // Write the bytes into the image file
        try {
            FileOutputStream(imageFile).use { stream ->
                stream.write(decodedBytes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("erreur", "exception")
        }

        // Get the URL of the file
        val imageUrl: String = imageFile.toURI().toURL().toString()

        // Display the image URL
        println("Image URL: $imageUrl")

        return imageUrl
    }
}
