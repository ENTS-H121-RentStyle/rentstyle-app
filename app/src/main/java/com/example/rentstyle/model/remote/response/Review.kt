package com.example.rentstyle.model.remote.response

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("id") val id: String,
    @SerializedName("order_id") val orderId: String,
    @SerializedName("seller_id") val sellerId: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("review") val review: String,
    @SerializedName("image") val image: String?,
    @SerializedName("User") val user: User
)