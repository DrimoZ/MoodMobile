import com.groupe5.moodmobile.dtos.Friend.DtoInputFriend
import com.groupe5.moodmobile.dtos.Friend.DtoInputFriendsResponse
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublication
import com.groupe5.moodmobile.dtos.Publication.Input.DtoInputPublicationsResponse
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserAccount
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserIdAndRole
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserPrivacy
import com.groupe5.moodmobile.dtos.Users.Input.DtoInputUserProfile
import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserAccount
import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserPassword
import com.groupe5.moodmobile.dtos.Users.Output.DtoOutputUserPrivacy
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface IUserRepository {
    @GET("/api/v1/user/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: String): DtoInputUserProfile
    @GET("/api/v1/user/{userId}/account")
    suspend fun getUserAccount(@Path("userId") userId: String): DtoInputUserAccount
    @GET("/api/v1/user")
    suspend fun getUserIdAndRole(): DtoInputUserIdAndRole
    @GET("/api/v1/user/{userId}/publications")
    suspend fun getUserPublications(@Path("userId") userId: String): DtoInputPublicationsResponse
    @GET("/api/v1/user/discover/publications")
    suspend fun getDiscoverPublications(
        @Query("publicationCount") count: Int,
        @Query("searchValue") search: String
    ): Response<List<DtoInputPublication>>
    @GET("/api/v1/user/{userId}/friends")
    suspend fun getUserFriends(@Path("userId") userId: String): DtoInputFriendsResponse
    @GET("/api/v1/user/discover/users")
    suspend fun getDiscoverUsers(
        @Query("userCount") count: Int,
        @Query("searchValue") search: String
    ): List<DtoInputFriend>
    @GET("/api/v1/user/privacy")
    suspend fun getUserPrivacy(): DtoInputUserPrivacy




    @PUT("/api/v1/user")
    fun setUserAccount(@Body userAccount: DtoOutputUserAccount): Call<Void>
    @PATCH("/api/v1/user")
    fun setUserPrivacy(@Body userPrivacy: DtoOutputUserPrivacy): Call<Void>
    @POST("/api/v1/user/userPassword")
    fun setUserPassword(@Body userPassword: DtoOutputUserPassword): Call<Void>
    @POST("/api/v1/user/delete")
    suspend fun deleteAccount(): Void?
    @GET("/api/v1/user")
    fun getUserIdAndRoleService(): Call<DtoInputUserIdAndRole>
    @GET("/api/v1/user/{userId}")
    fun getUserProfileService(@Path("userId") userId: String): Call<DtoInputUserProfile>
}
