package com.example.rentstyle.model.remote.retrofit

import com.example.rentstyle.model.Product
import com.example.rentstyle.model.remote.response.Pref
import com.example.rentstyle.model.remote.response.PrefResponse
import com.example.rentstyle.model.remote.response.ProductDetailResponse
import com.example.rentstyle.model.remote.response.SellerResponseData
import com.example.rentstyle.model.remote.response.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
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
}