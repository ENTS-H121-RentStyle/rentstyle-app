package com.example.rentstyle.model.remote.retrofit

import com.example.rentstyle.model.Product
import com.example.rentstyle.model.remote.response.Pref
import com.example.rentstyle.model.remote.response.ProductDetailResponse
import com.example.rentstyle.model.remote.response.SellerResponseData
import com.example.rentstyle.model.remote.response.User
import com.example.rentstyle.model.remote.response.UserResponseData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("user")
    suspend fun createNewUser (
        @Body user: User,
    ) : Response<Unit>

    @POST("pref")
    suspend fun uploadUserPreference (
        @Body pref: Pref
    ) : Response<Unit>

    @GET("pref/{userId}")
    suspend fun checkUserPreference (
        @Path("userId") userId: String
    ) : Response<Unit>
    @GET("product/{id}")
    suspend fun getProductDetail(@Path("id") productId: String): Response<ProductDetailResponse>

    @GET("product/filter")
    suspend fun getFilteredProducts(
        @Query("key") filter: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<Product>>

    @Headers("Authorization: Sudah izin pada Wildan dan Yoga")
    @GET("product")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<Product>>

    @GET("seller/{userId}")
    suspend fun getSellerData(
        @Path("userId") userId: String
    ) : Response<SellerResponseData>

    @Multipart
    @POST("seller")
    suspend fun createNewSeller (
        @Part file: MultipartBody.Part,
        @Part("seller_name") sellerName: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("address") address: RequestBody,
        @Part("description") desc: RequestBody,
        @Part("city") city: RequestBody
    ) : Response<SellerResponseData>

    @Multipart
    @POST("product")
    suspend fun createNewProduct (
        @Part("product_name") productName: RequestBody,
        @Part("seller_id") sellerId: RequestBody,
        @Part("category") category: RequestBody,
        @Part("size") size: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("color") color: RequestBody,
        @Part("desc") desc: RequestBody,
        @Part("rent_price") rentPrice: RequestBody,
        @Part("product_price") productPrice: RequestBody
    ) : Response<Unit>

    @Multipart
    @PUT("seller/{sellerId}")
    suspend fun updateSellerData (
        @Path("sellerId") sellerId: String,
        @Part("seller_name") sellerName: RequestBody,
        @Part("address") address: RequestBody,
        @Part("description") desc: RequestBody
    ) : Response<Unit>

    @Multipart
    @PUT("user/{userId}")
    suspend fun updateUserData (
        @Path("userId") userId: String,
        @Part("name") userName: RequestBody,
        @Part("birth_date") birthDate: RequestBody,
        @Part("address") address: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part file: MultipartBody.Part
    ) : Response<Unit>

    @GET("user/{userId}")
    suspend fun getUserData(
        @Path("userId") userId: String
    ) : Response<UserResponseData>
}