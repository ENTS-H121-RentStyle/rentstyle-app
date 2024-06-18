package com.example.rentstyle.model.remote.retrofit

import com.example.rentstyle.model.remote.request.CartRequest
import com.example.rentstyle.model.remote.request.FavoriteRequest
import com.example.rentstyle.model.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // User Endpoints
    @POST("user")
    suspend fun createNewUser(
        @Body user: User,
    ): Response<Unit>

    @Multipart
    @PUT("user/{userId}")
    suspend fun updateUserData(
        @Path("userId") userId: String,
        @Part("name") userName: RequestBody,
        @Part("birth_date") birthDate: RequestBody,
        @Part("address") address: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<Unit>

    @GET("user/{userId}")
    suspend fun getUserData(
        @Path("userId") userId: String
    ): Response<UserResponseData>

    // User Preference Endpoints
    @POST("pref")
    suspend fun uploadUserPreference(
        @Body pref: Pref
    ): Response<Unit>

    @GET("pref/{userId}")
    suspend fun checkUserPreference(
        @Path("userId") userId: String
    ): Response<Unit>

    // Product Endpoints
    @GET("product/{id}")
    suspend fun getProductDetail(
        @Path("id") productId: String
    ): Response<ProductDetailResponse>

    @GET("product/filter")
    suspend fun getFilteredProducts(
        @Query("key") filter: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<ProductResponse>

    @GET("result/recommendation")
    suspend fun getRecommendations(
        @Query("userId") userId: String,
        @Query("page") page: Int
    ): Response<ProductResponse>

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

    // Seller Endpoints
    @GET("seller/{userId}")
    suspend fun getSellerData(
        @Path("userId") userId: String
    ): Response<SellerResponseData>

    @Multipart
    @POST("seller")
    suspend fun createNewSeller(
        @Part file: MultipartBody.Part,
        @Part("seller_name") sellerName: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("address") address: RequestBody,
        @Part("description") desc: RequestBody,
        @Part("city") city: RequestBody
    ): Response<SellerResponseData>

    @Multipart
    @PUT("seller/{sellerId}")
    suspend fun updateSellerData(
        @Path("sellerId") sellerId: String,
        @Part("seller_name") sellerName: RequestBody,
        @Part("address") address: RequestBody,
        @Part("description") desc: RequestBody
    ): Response<Unit>

    // Cart Endpoints
    @POST("/cart")
    suspend fun addToCart(
        @Body request: CartRequest
    ): Response<CartResponse>

    // Favorite Endpoints
    @POST("favorite")
    suspend fun addFavorite(
        @Body favorite: FavoriteRequest
    ): Response<FavoriteResponse>

    @GET("favorite/{userId}")
    suspend fun getFavorites(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): Response<List<FavoriteResponse>>

    @DELETE("favorite/{id}")
    suspend fun deleteFavorite(
        @Path("id") id: String
    ): Response<Unit>

    @GET("cart/{userId}")
    suspend fun getCart(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): List<CartResponse>

    @GET("product/search")
    suspend fun getProductByKeyword(
        @Query("q") q: String,
        @Query("page") page: Int
    ): Response<ProductResponse>

    @DELETE("cart/{cartId}")
    suspend fun deleteShoppingCartItem(
        @Path("cartId") id: String
    ): Response<Unit>

    @Multipart
    @PUT("cart/{cartId}")
    suspend fun updateCartDuration(
        @Path("cartId") id: String,
        @Part("duration") duration: RequestBody,
    ): Response<Unit>

    @Multipart
    @POST("order")
    suspend fun makeOrder(
        @Part("product_id") productId: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("order_date") orderDate: RequestBody,
        @Part("return_date") returnDate: RequestBody,
        @Part("rent_duration") rentDuration: RequestBody,
        @Part("service_fee") serviceFee: RequestBody,
        @Part("deposit") deposit: RequestBody,
        @Part("rent_price") rentPrice: RequestBody,
        @Part("total_payment") totalPayment: RequestBody
    ): Response<Unit>
}
